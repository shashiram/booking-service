package com.home.dto;

import java.time.LocalTime;

public class CleanerAvailabilitySlotDTO {
    private Integer duration;
    private LocalTime startTime;
    private LocalTime endTime;

    public CleanerAvailabilitySlotDTO(Integer duration, LocalTime startTime, LocalTime endTime) {

        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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
}
