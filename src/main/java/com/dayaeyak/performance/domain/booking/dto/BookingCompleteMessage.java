package com.dayaeyak.performance.domain.booking.dto;

import com.dayaeyak.performance.domain.alarm.enums.ServiceType;

public record BookingCompleteMessage(
        Long userId,
        Long serviceId,
        ServiceType serviceType,
        Long totalFee,
        String status,
        BookingDetailRequest bookingDetailRequest
) {
}