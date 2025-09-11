package com.dayaeyak.performance.domain.performance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreatePerformanceResponseDto(
        @Schema(description = "공연 ID", example = "1")
        Long performanceId
) {
}
