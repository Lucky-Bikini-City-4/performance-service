package com.dayaeyak.performance.domain.hall.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateHallSectionDto(
        @NotBlank(message = "구역 이름은 필수 입력값입니다.") String sectionName,
        @NotNull(message = "좌석 수량을 입력해주세요.")
        @Min(value = 0, message = "좌석 수량은 0 이상이어야 합니다.") Integer seats,
        @NotNull(message = "좌석 가격을 입력해주세요.")
        @Min(value = 0, message = "좌석 가격은 0원 이상이어야 합니다.") Integer seatPrice) {
}
