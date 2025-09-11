package com.dayaeyak.performance.domain.cast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateCastRequestDto(
        @Schema(description = "출연진 이름", example = "아이유")
        @NotBlank(message = "출연진 이름을 입력해주세요.")
        String castName
) {
}
