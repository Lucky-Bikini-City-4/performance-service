package com.dayaeyak.performance.domain.booking.dto;

public record BulkSeatSoldOutRequestDto(
        Long performanceId,
        Long sessionId,
        Long sectionId,
        java.util.List<Long> seatIds
) {
}