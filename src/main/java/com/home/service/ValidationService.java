package com.home.service;

import com.home.dto.BookingDTO;
import com.home.dto.BookingReqDTO;
import com.home.dto.CleanerAvailabilityRespDTO;
import com.home.dto.CleanerAvailabilitySlotDTO;
import com.home.exception.ValidationException;
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
public class ValidationService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AvailabilitySlotRepository availabilitySlotRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    public ValidationService(AvailabilitySlotRepository availabilitySlotRepository, EntityManager entityManager) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.entityManager = entityManager;
    }

    public void validateBookingParams(BookingReqDTO bookingReqDTO) {
        validateRequestParams(bookingReqDTO.getDate(), bookingReqDTO.getStartTime(), bookingReqDTO.getDuration());
    }

    public void validateCheckAvailabilityParams(LocalDate date, LocalTime startTime, Integer duration) {
        validateRequestParams(date, startTime, duration);
    }

    public void validateRequestParams(LocalDate date, LocalTime startTime, Integer duration) {
        if (date.isBefore(LocalDate.now())) {
            throw new ValidationException("Invalid date");
        }

        if (startTime != null) {
            LocalTime start = LocalTime.of(8, 0);
            LocalTime end = LocalTime.of(22, 0);

            if (startTime.isBefore(start) || startTime.isAfter(end)) {
                throw new ValidationException("Slot must be between 08:00 and 22:00");
            }
            if (startTime.getMinute() % 30 != 0) {
                throw new ValidationException("Slot must be in 30-minute intervals (e.g., 08:00, 08:30, 09:00)");
            }
        }

        if (duration != null && !(duration.equals(TWO_HOURS_DURATION)
                || duration.equals(FOUR_HOURS_DURATION))) {
            throw new ValidationException("Invalid duration");
        }
    }

}
