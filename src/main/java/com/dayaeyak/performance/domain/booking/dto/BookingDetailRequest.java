package com.dayaeyak.performance.domain.booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record BookingDetailRequest(
        Long sessionId,
        LocalDate sessionDate,
        LocalTime sessionTime,
        String sectionName,
        String seatPrice,
        List<Long> seatNumber,
        boolean isSoldOut,
        Long performanceId,
        Long sectionId,
        List<Long> seatIds
) {
}
