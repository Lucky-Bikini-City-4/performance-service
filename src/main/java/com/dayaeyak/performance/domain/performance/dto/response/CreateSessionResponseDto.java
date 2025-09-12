package com.dayaeyak.performance.domain.performance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateSessionResponseDto(
        @Schema(description = "공연 회차 ID", example = "1")
        Long performanceSessionId
) { }
