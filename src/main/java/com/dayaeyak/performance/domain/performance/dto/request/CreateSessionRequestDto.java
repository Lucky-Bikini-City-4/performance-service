package com.dayaeyak.performance.domain.performance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateSessionRequestDto(
        @Schema(description = "공연 ID", example = "3")
        @NotNull(message = "공연 ID는 필수 입력값입니다.")
        Long performanceId,

        @Schema(description = "공연 회차 날짜", example = "2025-10-05")
        @NotNull(message = "공연 회차 날짜는 필수 입력값입니다.")
        LocalDate date,

        @Schema(description = "공연 회차 시간", example = "18:00:00")
        @NotNull(message = "공연 회차 시간은 필수 입력값입니다.")
        LocalTime time
) {
}
