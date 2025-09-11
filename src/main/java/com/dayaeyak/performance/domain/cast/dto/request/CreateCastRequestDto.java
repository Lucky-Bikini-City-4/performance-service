package com.dayaeyak.performance.domain.cast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCastRequestDto(
        @Schema(description = "출연진 이름", example = "아이유")
        @NotBlank(message = "출연진 이름을 입력해주세요.")
        @Size(max = 30, message = "출연진 이름은 30자까지 입력 가능합니다.")
        String castName
) {
}
