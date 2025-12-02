package com.home.controller;

import com.home.dto.BookingReqDTO;
import com.home.dto.CleanerAvailabilityRespDTO;
import com.home.service.BookingService;
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

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public String createBooking(@RequestBody BookingReqDTO bookingReqDTO) {
        bookingService.validateBookingParams(bookingReqDTO);
        String bookingId = bookingService.createBooking(bookingReqDTO);
        return "bookingId:" + bookingId;
    }

    @GetMapping("/checkAvailability")
    public List<CleanerAvailabilityRespDTO> checkAvailability(
            @RequestParam @NotNull(message = "Date is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam(required = false) Integer duration) {

        bookingService.validateCheckAvailabilityParams(date, startTime, duration);
        if (duration == null && startTime == null) {
            return bookingService.checkAvailability(date);
        }
        return bookingService.checkAvailability(date, startTime, duration);

    }
}
