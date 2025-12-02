package com.home.dto;

import java.time.LocalDate;
import java.util.List;

public class CleanerAvailabilityRespDTO {
    private Long cleanerId;
    private LocalDate date;
    private List<CleanerAvailabilitySlotDTO> cleanerAvailability;

    public CleanerAvailabilityRespDTO(Long cleanerId, LocalDate date, List<CleanerAvailabilitySlotDTO> cleanerAvailability) {
        this.cleanerId = cleanerId;
        this.date = date;
        this.cleanerAvailability = cleanerAvailability;
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

    public List<CleanerAvailabilitySlotDTO> getCleanerAvailability() {
        return cleanerAvailability;
    }

    public void setCleanerAvailability(List<CleanerAvailabilitySlotDTO> cleanerAvailability) {
        this.cleanerAvailability = cleanerAvailability;
    }
}
