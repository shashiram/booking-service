package com.home.mapper;

import com.home.dto.BookingDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import static com.home.constant.AppConstant.*;

@Component
public class BookingMapper {

    public BookingDTO toBookingQueryParamDto(LocalDate date, LocalTime startTime, Integer duration) {

        int hours = resolveHours(duration);
        int requiredSlots = resolveRequiredSlots(duration);
        LocalTime bookingEndTime = startTime.plusHours(hours);
        LocalTime slotEndTime = bookingEndTime.plusMinutes(INTERVAL);

        return new BookingDTO(
                date,
                startTime,
                slotEndTime,
                bookingEndTime,
                requiredSlots
        );
    }

    private int resolveHours(Integer duration) {
        return Objects.equals(duration, TWO_HOURS_DURATION)
                ? TWO_HOURS_DURATION
                : FOUR_HOURS_DURATION;
    }

    private int resolveRequiredSlots(Integer duration) {
        return Objects.equals(duration, TWO_HOURS_DURATION)
                ? REQUIRED_SLOTS_IN_2_HOURS_DURATION
                : REQUIRED_SLOTS_IN_4_HOURS_DURATION;
    }
}
