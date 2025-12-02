package com.home.repository;

import com.home.entity.AvailabilitySlot;
import com.home.record.CleanerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    @Query(value = """
        SELECT
            asl.cleaner_id AS cleanerId
        FROM
            availability_slots asl
        WHERE
            asl.slot_date = :slotDate
            AND asl.start_time >= :startTime
            AND asl.end_time <= :endTime
            AND asl.is_available = true
        GROUP BY
            asl.cleaner_id
        HAVING
            COUNT(asl.id) = :requiredSlotCount
        """, nativeQuery = true)
    List<CleanerRecord> findAvailableCleanersWithExactSlotCount(
            @Param("slotDate") LocalDate slotDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("requiredSlotCount") Integer requiredSlotCount);
}