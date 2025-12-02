package com.home.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class BookingReqDTO {
    private LocalDate date;
    private LocalTime startTime;
    private Integer duration;
    private Set<Long> cleanerIds;
    private Integer customerId;

    /*
    private LocalDate date;
    private LocalTime startTime;
    private Integer duration;
    private Set<Long> cleanerIds;
    private Integer customerId;
    private LocalTime endTime;
    private LocalTime bookingEndTime;
    private Integer requiredSlotCount;
    * */

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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Set<Long> getCleanerIds() {
        return cleanerIds;
    }

    public void setCleanerIds(Set<Long> cleanerIds) {
        this.cleanerIds = cleanerIds;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
