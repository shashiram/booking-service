package com.home.controller;

import com.home.dto.CleanerAvailabilityRespDTO;
import com.home.service.CleanerAvailabilityService;
import com.home.service.CleanerBookingService;
import com.home.service.ValidationService;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/cleaner-availability")
public class CleanerAvailabilityController {

    private static final Logger log = LoggerFactory.getLogger(CleanerAvailabilityController.class);
    private final CleanerAvailabilityService cleanerAvailabilityService;
    private final ValidationService validationService;

    public CleanerAvailabilityController(CleanerBookingService cleanerBookingService, CleanerAvailabilityService cleanerAvailabilityService, ValidationService validationService) {
        this.cleanerAvailabilityService = cleanerAvailabilityService;
        this.validationService = validationService;
    }

    @GetMapping
    public List<CleanerAvailabilityRespDTO> checkAvailability(
            @RequestParam @NotNull(message = "Date is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam(required = false) Integer duration) {

        validationService.validateCheckAvailabilityParams(date, startTime, duration);
        if (duration == null && startTime == null) {
            return cleanerAvailabilityService.checkAvailability(date);
        }
        return cleanerAvailabilityService.checkAvailability(date, startTime, duration);

    }
}
