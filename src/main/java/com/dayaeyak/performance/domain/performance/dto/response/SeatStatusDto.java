package com.dayaeyak.performance.domain.performance.dto.response;

public record SeatStatusDto (
        Long seatId,
        boolean isBooked
){
}