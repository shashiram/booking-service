package com.home.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingDTO {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime bookingEndTime;
    private Integer requiredSlotCount;

    public BookingDTO(LocalDate date, LocalTime startTime, LocalTime endTime, LocalTime bookingEndTime, Integer requiredSlotCount) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingEndTime = bookingEndTime;
        this.requiredSlotCount = requiredSlotCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getBookingEndTime() {
        return bookingEndTime;
    }

    public void setBookingEndTime(LocalTime bookingEndTime) {
        this.bookingEndTime = bookingEndTime;
    }

    public Integer getRequiredSlotCount() {
        return requiredSlotCount;
    }

    public void setRequiredSlotCount(Integer requiredSlotCount) {
        this.requiredSlotCount = requiredSlotCount;
    }
}
