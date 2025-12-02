package com.home.record;

import java.time.LocalTime;

public record SlotRangeRecord(LocalTime startTime, LocalTime endTime, String cleanerIds) {
}
