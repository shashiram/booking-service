package com.home.controller;

import com.home.dto.BookingReqDTO;
import com.home.service.CleanerBookingService;
import com.home.service.ValidationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cleaner-booking")
public class CleanerBookingController {

    private final CleanerBookingService cleanerBookingService;
    private final ValidationService validationService;

    public CleanerBookingController(CleanerBookingService cleanerBookingService, ValidationService validationService) {
        this.cleanerBookingService = cleanerBookingService;
        this.validationService = validationService;
    }

    @PostMapping
    public String bookCleaners(@RequestBody BookingReqDTO bookingReqDTO) {

        validationService.validateBookingParams(bookingReqDTO);
        String bookingId = cleanerBookingService.bookCleaners(bookingReqDTO);
        return "bookingId:" + bookingId;
    }
}
