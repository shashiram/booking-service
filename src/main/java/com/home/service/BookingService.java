package com.home.service;

import com.home.dto.BookingReqDTO;
import com.home.dto.BookingDTO;
import com.home.dto.CleanerAvailabilityRespDTO;
import com.home.dto.CleanerAvailabilitySlotDTO;
import com.home.exception.ValidationException;
import com.home.record.CleanerRecord;
import com.home.record.SlotRecord;
import com.home.record.SlotRangeRecord;
import com.home.repository.AvailabilitySlotRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.home.constant.AppConstant.*;

@Service
public class BookingService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AvailabilitySlotRepository availabilitySlotRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    public BookingService(AvailabilitySlotRepository availabilitySlotRepository, EntityManager entityManager) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.entityManager = entityManager;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public String createBooking(BookingReqDTO bookingReqDTO) {
        /*
       Steps:

        1- Validate all cleaners belong to same vehicle

        2- Reject if multiple vehicles

        3- Fetch availability for each cleaner

        4- Validate each cleaner has required slots

        5- Lock all relevant slots (FOR UPDATE NOWAIT)

        6- Reject if no slots locked

        7- Insert booking into bookings table

        8- Insert cleaner mappings into booking_cleaner

        9- Update availability slots (mark unavailable & assign booking_id)

        10-Return booking ID

        * */

        log.info(" Inside create booking {} ", bookingReqDTO);

        BookingDTO bookingDTO = getBookingQueryParamDTO(bookingReqDTO.getDate(), bookingReqDTO.getStartTime()
                , bookingReqDTO.getDuration());

        String cleanerIds = bookingReqDTO.getCleanerIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // Validate that all selected cleaners belong to the same vehicle
        log.info(" Validating cleaner vehicle ");

        String cleanerSql = """
                    SELECT vehicle_id
                    FROM cleaners
                    WHERE id IN (%s)
                    GROUP BY vehicle_id
                """.formatted(cleanerIds);

        List<?> result = entityManager
                .createNativeQuery(cleanerSql)
                .getResultList();

        // If more than one vehicle is found → reject booking
        log.info("Validation result of vehicle ");

        if (result.size() > 1) {
            throw new ValidationException("Cleaners must be belong to same vehicle");
        }

        // Check cleaner availability: verify each selected cleaner has all required slots

        log.info("Checking cleaner availability in DB");
        String cleanerIdsQuery = """
                SELECT
                    asl.cleaner_id AS cleanerId
                FROM
                    availability_slots asl
                WHERE
                    asl.slot_date = :slotDate
                    AND asl.start_time >= :startTime
                    AND asl.end_time <= :endTime
                    AND asl.is_available = true
                    AND asl.cleaner_id IN (%s)
                GROUP BY
                    asl.cleaner_id
                HAVING
                    COUNT(asl.id) = :requiredSlotCount
                """.formatted(cleanerIds);

        Query cleanerQuery = entityManager.createNativeQuery(cleanerIdsQuery, CleanerRecord.class);
        cleanerQuery.setParameter("slotDate", bookingDTO.getDate());
        cleanerQuery.setParameter("startTime", bookingDTO.getStartTime());
        cleanerQuery.setParameter("endTime", bookingDTO.getEndTime());
        cleanerQuery.setParameter("requiredSlotCount", bookingDTO.getRequiredSlotCount());

        List<CleanerRecord> cleaners = cleanerQuery.getResultList();

        // Validate: every selected cleaner must appear in availability results

        log.info("Validating selected cleaners against availability appear in the available cleaner results");
        for (CleanerRecord cleaner : cleaners) {
            if (!bookingReqDTO.getCleanerIds().contains(cleaner.cleanerId())) {
                throw new ValidationException("selected cleaner not available ");
            }
        }

        // Lock all time slots for selected cleaners to prevent race conditions
        log.info("Attempting to lock required availability slots to prevent race conditions using `FOR UPDATE NOWAIT`");
        String slotQueryForUpdateSql = """
                SELECT
                    asl.id
                FROM
                    availability_slots asl
                WHERE
                    asl.slot_date = :slotDate
                    AND asl.start_time >= :startTime
                    AND asl.end_time <= :endTime
                    AND asl.is_available = true
                    AND asl.cleaner_id IN (%s)
                    FOR UPDATE NOWAIT
                """.formatted(cleanerIds);


        Query slotQueryForUpdateQuery = entityManager.createNativeQuery(slotQueryForUpdateSql, SlotRecord.class);
        slotQueryForUpdateQuery.setParameter("slotDate", bookingDTO.getDate());
        slotQueryForUpdateQuery.setParameter("startTime", bookingDTO.getStartTime());
        slotQueryForUpdateQuery.setParameter("endTime", bookingDTO.getEndTime());

        List<SlotRecord> slotIds = slotQueryForUpdateQuery.getResultList();

        // If no slots were locked → reject booking
        log.info("Slot locking result");
        if (slotIds.isEmpty()) {
            log.info("No available slots for booking");
            throw new ValidationException("No available slots for booking");
        }


        // Insert mapping records into booking_cleaner table
        log.info("Inserting booking record into the `bookings` table");
        String bookingInsertSql = """
                INSERT INTO public.bookings(
                                id, customer_id, duration_hours, total_cleaners, created_at, updated_at,
                                booking_date, status, start_time, end_time)
                            VALUES (
                                :id, :customerId, :durationHours, :totalCleaners,
                                now(), now(), :bookingDate, :status, :startTime, :endTime
                            )
                """;

        Query bookingInsertQuery = entityManager.createNativeQuery(bookingInsertSql);

        UUID bookingId = UUID.randomUUID();

        bookingInsertQuery.setParameter("id", bookingId);
        bookingInsertQuery.setParameter("customerId", bookingReqDTO.getCustomerId());
        bookingInsertQuery.setParameter("durationHours", bookingReqDTO.getDuration());
        bookingInsertQuery.setParameter("totalCleaners", bookingReqDTO.getCleanerIds().size());
        bookingInsertQuery.setParameter("bookingDate", bookingDTO.getDate());
        bookingInsertQuery.setParameter("status", "confirmed");
        bookingInsertQuery.setParameter("startTime", bookingDTO.getStartTime());
        bookingInsertQuery.setParameter("endTime", bookingDTO.getBookingEndTime());


        int bookingRowsAffected = bookingInsertQuery.executeUpdate();
        log.info("Inserted booking with ID: {}, rows affected: {}", bookingId, bookingRowsAffected);

        // insert data into booking cleaner table

        log.info("Inserting booking-cleaner relations table to map cleaners to booking");

        for (Long cleanerId : bookingReqDTO.getCleanerIds()) {
            entityManager.createNativeQuery(
                            "INSERT INTO booking_cleaner ( booking_id, cleaner_id, created_at) " +
                                    "VALUES ( :bookingId, :cleanerId, now())"
                    )
                    .setParameter("bookingId", bookingId)
                    .setParameter("cleanerId", cleanerId)
                    .executeUpdate();
        }

        // Mark all locked slots as unavailable + link them with booking

        log.info("Updating availability slots");

        String slotIdsString = slotIds.stream()
                .map(x -> String.valueOf(x.id()))
                .collect(Collectors.joining(","));

        String slotUpdateSql = """
                UPDATE availability_slots
                SET is_available = false,
                    booking_id = :bookingId,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id IN (%s)
                """.formatted(slotIdsString);

        Query slotUpdateQuery = entityManager.createNativeQuery(slotUpdateSql);
        slotUpdateQuery.setParameter("bookingId", bookingId);

        int updateRowsAffected = slotUpdateQuery.executeUpdate();
        log.info("Updated {} availability slots with booking ID: {}", updateRowsAffected, bookingId);

        // Return final booking ID to caller
        log.info(" Returning booking ID");
        return bookingId.toString();
    }

    private BookingDTO getBookingQueryParamDTO(LocalDate date, LocalTime startTime, Integer duration) {
        if (Objects.equals(duration, TWO_HOURS_DURATION)) {
            LocalTime endTime = startTime.plusHours(TWO_HOURS_DURATION).plusMinutes(INTERVAL); // adding 30 min for break
            LocalTime bookingEndTime = startTime.plusHours(TWO_HOURS_DURATION);
            return new BookingDTO(date, startTime, endTime, bookingEndTime, REQUIRED_SLOTS_IN_2_HOURS_DURATION);

        } else {
            LocalTime endTime = startTime.plusHours(FOUR_HOURS_DURATION).plusMinutes(INTERVAL); // adding 30 min for break
            LocalTime bookingEndTime = startTime.plusHours(FOUR_HOURS_DURATION);
            return new BookingDTO(date, startTime, endTime, bookingEndTime, REQUIRED_SLOTS_IN_4_HOURS_DURATION);
        }
    }
}
