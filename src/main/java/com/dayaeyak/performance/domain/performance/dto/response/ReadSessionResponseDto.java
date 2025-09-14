package com.dayaeyak.performance.domain.performance.dto.response;

import com.dayaeyak.performance.domain.performance.entity.PerformanceSession;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ReadSessionResponseDto(
        @Schema(description = "공연 ID", example = "1")
        Long performanceId,

        @Schema(description = "공연 회차 ID", example = "3")
        Long sessionId,

        @Schema(description = "공연 회차 날짜", example = "2025-11-01")
        LocalDate date,

        @Schema(description = "공연 회차 시간", example = "14:00:00")
        LocalTime time
) {

    public static ReadSessionResponseDto from(PerformanceSession performanceSession) {
        return ReadSessionResponseDto.builder()
                .performanceId(performanceSession.getPerformance().getPerformanceId())
                .sessionId(performanceSession.getPerformanceSessionId())
                .date(performanceSession.getDate())
                .time(performanceSession.getTime())
                .build();
    }
}
