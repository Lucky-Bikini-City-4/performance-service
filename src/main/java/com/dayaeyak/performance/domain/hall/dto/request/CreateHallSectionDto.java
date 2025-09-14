package com.dayaeyak.performance.domain.hall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateHallSectionDto(
        @Schema(description = "구역 이름", example = "101")
        @NotBlank(message = "구역 이름은 필수 입력값입니다.")
        @Size(max = 50, message = "구역 이름은 50자까지 입력 가능합니다.")
        String sectionName,

        @Schema(description = "구역의 좌석수", example = "300")
        @NotNull(message = "좌석 수량을 입력해주세요.")
        @Min(value = 0, message = "좌석 수량은 0 이상이어야 합니다.")
        Integer seats) {
}
