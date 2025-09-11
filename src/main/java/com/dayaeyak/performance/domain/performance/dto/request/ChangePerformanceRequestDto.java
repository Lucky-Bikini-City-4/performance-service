package com.dayaeyak.performance.domain.performance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ChangePerformanceRequestDto(
        @Schema(description = "공연 활성화 여부", example = "false")
        @NotNull(message = "공연 활성화 여부는 필수 입력값입니다.")
        Boolean isActivated
) {
}
