package com.home.service;

import com.home.dto.BookingDTO;
import com.home.dto.CleanerAvailabilityRespDTO;
import com.home.dto.CleanerAvailabilitySlotDTO;
import com.home.mapper.BookingMapper;
import com.home.record.CleanerRecord;
import com.home.record.SlotRangeRecord;
import com.home.repository.AvailabilitySlotRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.home.constant.AppConstant.*;

@Service
public class CleanerAvailabilityService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AvailabilitySlotRepository availabilitySlotRepository;

    private final BookingMapper  bookingMapper;

    @PersistenceContext
    private final EntityManager entityManager;

    public CleanerAvailabilityService(AvailabilitySlotRepository availabilitySlotRepository, BookingMapper bookingMapper, EntityManager entityManager) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.bookingMapper = bookingMapper;
        this.entityManager = entityManager;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<CleanerAvailabilityRespDTO> checkAvailability(LocalDate date) {

        List<SlotRangeRecord> slotFor2HoursList = getAvailableSlotsFor2Hours(date);
        List<SlotRangeRecord> slotFor4HoursList = getAvailableSlotsFor4Hours(date);

        Map<Long, List<CleanerAvailabilitySlotDTO>> availabilityMap = new HashMap<>();

        processSlotRecords(slotFor2HoursList, availabilityMap, TWO_HOURS_DURATION);
        processSlotRecords(slotFor4HoursList, availabilityMap, FOUR_HOURS_DURATION);

        return buildResponseDTOList(availabilityMap, date);
    }

    @Transactional
    public List<SlotRangeRecord> getAvailableSlotsFor2Hours(LocalDate date) {
        String slotSqlQueryFor2Hours = queryStringForAvailableSlots(END_INTERVAL_2_HOURS, WINDOW_INTERVAL_2_HOURS);

        log.debug("Generated SQL (2 hours): {}", slotSqlQueryFor2Hours);

        Query slotQueryFor2Hours = entityManager.createNativeQuery(slotSqlQueryFor2Hours, SlotRangeRecord.class);
        slotQueryFor2Hours.setParameter("slotDate", date);
        slotQueryFor2Hours.setParameter("requiredSlotCount", REQUIRED_SLOTS_IN_2_HOURS_DURATION);

        return slotQueryFor2Hours.getResultList();
    }

    @Transactional
    public List<SlotRangeRecord> getAvailableSlotsFor4Hours(LocalDate date) {
        String slotSqlQueryFor4Hours = queryStringForAvailableSlots(END_INTERVAL_4_HOURS, WINDOW_INTERVAL_4_HOURS);

        log.debug("Generated SQL (4 hours): {}", slotSqlQueryFor4Hours);

        Query slotQueryFor4Hours = entityManager.createNativeQuery(slotSqlQueryFor4Hours, SlotRangeRecord.class);
        slotQueryFor4Hours.setParameter("slotDate", date);
        slotQueryFor4Hours.setParameter("requiredSlotCount", REQUIRED_SLOTS_IN_4_HOURS_DURATION);

        return slotQueryFor4Hours.getResultList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<CleanerAvailabilityRespDTO> checkAvailability(LocalDate date, LocalTime startTime, Integer duration) {

        log.info("Checking cleaner availability for date: {}", date);

        BookingDTO bookingDTO = bookingMapper.toBookingQueryParamDto(date, startTime, duration);
        List<CleanerRecord> slots = availabilitySlotRepository
                .findAvailableCleanersWithExactSlotCount(bookingDTO.getDate(), bookingDTO.getStartTime(),
                        bookingDTO.getEndTime(), bookingDTO.getRequiredSlotCount());

        List<CleanerAvailabilityRespDTO> respDTOList = new ArrayList<>();
        for (CleanerRecord slot : slots) {

            List<CleanerAvailabilitySlotDTO> slotDTOS = new ArrayList<>();
            slotDTOS.add(new CleanerAvailabilitySlotDTO(duration, bookingDTO.getStartTime(), bookingDTO.getBookingEndTime()));

            respDTOList.add(new CleanerAvailabilityRespDTO(
                            slot.cleanerId(),
                            bookingDTO.getDate(),
                            slotDTOS
                    )
            );
        }
        return respDTOList;

    }

    private List<CleanerAvailabilityRespDTO> buildResponseDTOList(
            Map<Long, List<CleanerAvailabilitySlotDTO>> availabilityMap,
            LocalDate date) {

        List<CleanerAvailabilityRespDTO> availableRespDTOList = new ArrayList<>();

        for (Map.Entry<Long, List<CleanerAvailabilitySlotDTO>> entry :
                availabilityMap.entrySet()) {
            availableRespDTOList.add(
                    new CleanerAvailabilityRespDTO(entry.getKey(), date, entry.getValue()));
        }

        log.info("Total cleaners available: {}", availableRespDTOList.size());
        log.info("Availability check completed for date {}", date);

        return availableRespDTOList;
    }
    private void processSlotRecords(List<SlotRangeRecord> slotRecords,
                                    Map<Long, List<CleanerAvailabilitySlotDTO>> map,
                                    Integer duration) {
        for (SlotRangeRecord slot : slotRecords) {
            for (String cleanerId : slot.cleanerIds().split(",")) {
                map.computeIfAbsent(Long.parseLong(cleanerId), k -> new ArrayList<>())
                        .add(new CleanerAvailabilitySlotDTO(
                                duration,
                                slot.startTime(),
                                slot.endTime().minusMinutes(INTERVAL)));
            }
        }
    }

    private String queryStringForAvailableSlots(String endInterval, String windowInterval) {
        return String.format("""
                    WITH slot_range AS (
                        SELECT
                            start_time,
                            start_time + interval '%s' as end_time,
                            STRING_AGG(DISTINCT cleaner_id::text, ',') AS cleaner_ids,
                            COUNT(*)
                            OVER (
                                ORDER BY start_time
                                RANGE BETWEEN CURRENT ROW AND INTERVAL '%s' FOLLOWING
                                ) as slots_count
                        FROM availability_slots
                        WHERE  slot_date = :slotDate
                          AND is_available = true
                        GROUP BY start_time
                    )
                    SELECT
                        start_time,
                        end_time,
                        cleaner_ids
                    FROM slot_range
                    WHERE slots_count = :requiredSlotCount
                """, endInterval, windowInterval);
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
