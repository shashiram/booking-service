package com.home.controller;

import com.home.dto.BookingReqDTO;
import com.home.dto.CleanerAvailabilityRespDTO;
import com.home.service.BookingService;
import com.home.service.ValidationService;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;
    private final ValidationService validationService;

    public BookingController(BookingService bookingService, ValidationService validationService) {
        this.bookingService = bookingService;
        this.validationService = validationService;
    }

    @PostMapping
    public String createBooking(@RequestBody BookingReqDTO bookingReqDTO) {
        validationService.validateBookingParams(bookingReqDTO);
        String bookingId = bookingService.createBooking(bookingReqDTO);
        return "bookingId:" + bookingId;
    }
}
