package com.home.dto;

import java.time.LocalDate;
import java.util.List;

public class CleanerAvailabilityRespDTO {
    private Long cleanerId;
    private LocalDate date;
    private List<CleanerAvailabilitySlotDTO> availableSlots;

    public CleanerAvailabilityRespDTO(Long cleanerId, LocalDate date, List<CleanerAvailabilitySlotDTO> cleanerAvailability) {
        this.cleanerId = cleanerId;
        this.date = date;
        this.availableSlots = cleanerAvailability;
    }

    public Long getCleanerId() {
        return cleanerId;
    }

    public void setCleanerId(Long cleanerId) {
        this.cleanerId = cleanerId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<CleanerAvailabilitySlotDTO> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<CleanerAvailabilitySlotDTO> availableSlots) {
        this.availableSlots = availableSlots;
    }
}
