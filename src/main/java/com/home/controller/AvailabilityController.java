package com.home.controller;

import com.home.dto.BookingReqDTO;
import com.home.dto.CleanerAvailabilityRespDTO;
import com.home.service.AvailabilityService;
import com.home.service.BookingService;
import com.home.service.ValidationService;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final ValidationService validationService;

    public AvailabilityController(BookingService bookingService, AvailabilityService availabilityService, ValidationService validationService) {
        this.availabilityService = availabilityService;
        this.validationService = validationService;
    }

    @GetMapping("/check")
    public List<CleanerAvailabilityRespDTO> checkAvailability(
            @RequestParam @NotNull(message = "Date is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam(required = false) Integer duration) {

        validationService.validateCheckAvailabilityParams(date, startTime, duration);
        if (duration == null && startTime == null) {
            return availabilityService.checkAvailability(date);
        }
        return availabilityService.checkAvailability(date, startTime, duration);

    }
}
