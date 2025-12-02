package com.home.service;

import com.home.entity.AvailabilitySlot;
import com.home.entity.Cleaner;
import com.home.record.SlotRecord;
import com.home.repository.AvailabilitySlotRepository;
import com.home.repository.CleanerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CronService {

    private final static Logger log = LoggerFactory.getLogger(CronService.class);

    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final CleanerRepository cleanerRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    public CronService(AvailabilitySlotRepository availabilitySlotRepository, CleanerRepository cleanerRepository, EntityManager entityManager) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.cleanerRepository = cleanerRepository;
        this.entityManager = entityManager;
    }

    /*
        This cron job runs daily at 2 AM to generate 30-minute availability slots for each cleaner.
        If the system supports advance booking (e.g., a 2-week window), the initial run pre-populates slots for the full window.
        Subsequent executions add only the current day's slots,
        ensuring the system always maintains a rolling two-week availability schedule.
    * */
   // @Scheduled(cron = "0 */2 * * * *")
    @Scheduled(cron = "0 0 2 * * *") // daily at 2:00AM
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void createDailySlotsCron() {

        log.info(" Cron Triggered: Starting daily slot creation at {}", LocalDateTime.now());

        List<Cleaner> cleaners = cleanerRepository.findAll();

        log.info("Found {} cleaners", cleaners.size());

        List<AvailabilitySlot> slots = new ArrayList<>();

        for (Cleaner cleaner : cleaners) {

            LocalTime start = LocalTime.of(8, 0);   // 08:00
            LocalTime end = LocalTime.of(22, 30);   // 22:30

            while (!start.equals(end)) {
                AvailabilitySlot slot = new AvailabilitySlot();

                slot.setCleaner(cleaner);
                slot.setSlotDate(LocalDate.now());
                slot.setStartTime(start);
                slot.setEndTime(start.plusMinutes(30));
                slot.setAvailable(true);
                slots.add(slot);
                start = start.plusMinutes(30);
            }
        }

        log.info("Saving total {} slots into database…", slots.size());

        availabilitySlotRepository.saveAll(slots);

        log.info("Daily slot creation completed successfully");

    }

    /*
    This scheduled job runs every 30 minutes starting at 7:55 AM until 9:55 PM. Its purpose is to:

    - Determine the current active 30-minute slot (rounded to the nearest 00 or 30 minute based on the current time +10 minutes buffer).


    -Mark those slots as unavailable by updating is_available = false.

    Effectively  block slot that about to start and going  into the current 30-minute window so that:

    those slots cannot be booked anymore,

    This ensures the system continuously updates cleaner availability slots in real-time every 30 minutes.
        *
    * */


//    @Scheduled(cron = "0 55/30 7-21 * * *")
    //@Scheduled(cron = "0 0 2 * * *") //
    @Scheduled(cron = "0 55,25 7-21 * * *")
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void Every30MinCron() {

        log.info("=== Every30MinCron started ===");

        String slotQueryForUpdateSql = """
                SELECT
                    asl.id
                FROM
                    availability_slots asl
                WHERE
                    asl.slot_date = :slotDate
                    AND asl.start_time = :startTime
                    AND asl.is_available = true
                    FOR UPDATE
                """;

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalTime currentTime =LocalTime.now().plusMinutes(10);

        log.info("Current time: {}  | Adjusted time (+10m): {}", LocalTime.now(), currentTime);

        LocalTime startTime = null;
        if(currentTime.getMinute()<30){
            startTime=LocalTime.of(currentTime.getHour(),0);
        }else {
            startTime=LocalTime.of(currentTime.getHour(),30);
        }

        log.info("Calculated target slot startTime: {}", startTime);

        Query slotQueryForUpdateQuery = entityManager.createNativeQuery(slotQueryForUpdateSql, SlotRecord.class);
        slotQueryForUpdateQuery.setParameter("slotDate", LocalDate.now());
        slotQueryForUpdateQuery.setParameter("startTime", startTime);

        List<SlotRecord> slotIds = slotQueryForUpdateQuery.getResultList();

        String slotIdsString = slotIds.stream()
                .map(x -> String.valueOf(x.id()))
                .collect(Collectors.joining(","));

        String slotUpdateSql = """
                UPDATE availability_slots
                SET is_available = false,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id IN (%s)
                """.formatted(slotIdsString);

        log.info("Locked {} slots for update", slotIds.size());

        if (slotIds.isEmpty()) {
            log.info("No available slots found to update. Exiting cron.");
            return;
        }

        Query slotUpdateQuery = entityManager.createNativeQuery(slotUpdateSql);

        int updateRowsAffected = slotUpdateQuery.executeUpdate();

        log.info("Updated {} slots to is_available = false", updateRowsAffected);
        log.info("=== Every30MinCron completed ===");

    }
}
