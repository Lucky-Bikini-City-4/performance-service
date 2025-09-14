package com.dayaeyak.performance.domain.performance.dto.response;

import com.dayaeyak.performance.domain.performance.entity.PerformanceSeat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SeatResponseDto(
        @Schema(description = "공연 ID", example = "1")
        Long performanceId,

        @Schema(description = "공연 회차 ID", example = "3")
        Long sessionId,

        @Schema(description = "회차 구역 ID", example = "6")
        Long sectionId,

        @Schema(description = "구역 좌석 ID", example = "4521")
        Long seatId,

        @Schema(description = "좌석 번호", example = "32")
        Integer seatNumber,

        @Schema(description = "좌석 품절 여부", example = "true")
        Boolean isSoldOut)
{
    public static SeatResponseDto from(PerformanceSeat seat){
        return SeatResponseDto.builder()
                .performanceId(seat.getPerformanceSection().getPerformanceSession().getPerformance().getPerformanceId())
                .sessionId(seat.getPerformanceSection().getPerformanceSession().getPerformanceSessionId())
                .sectionId(seat.getPerformanceSection().getPerformanceSectionId())
                .seatId(seat.getPerformanceSeatId())
                .seatNumber(seat.getSeatNumber())
                .isSoldOut(seat.getIsSoldOut())
                .build();

    }
}
