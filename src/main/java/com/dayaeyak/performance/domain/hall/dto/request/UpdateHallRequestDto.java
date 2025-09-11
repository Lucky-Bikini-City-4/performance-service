package com.dayaeyak.performance.domain.hall.dto.request;

import com.dayaeyak.performance.domain.hall.enums.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateHallRequestDto(
        @Schema(description = "공연장 이름", example = "KSPO DOME")
        @NotBlank(message = "공연장 이름은 필수 입력값입니다.")
        String hallName,

        @Schema(description = "공연장 주소", example = "서울특별시 송파구 올림픽로 424")
        @NotBlank(message = "공연장 주소는 필수 입력값입니다.")
        String address,

        @Schema(description = "공연장 소재 지역", example = "SEOUL")
        @NotNull(message = "공연장이 위치한 지역을 입력해주세요.")
        Region city,

        @Schema(description = "수용 인원", example = "12000")
        @NotNull(message = "공연장의 수용인원을 입력해주세요.")
        @Min(value = 0, message = "수용인원은 0 이상이어야 합니다.")
        Integer capacity,

        @Schema(description = "구역 정보 변경 시 기존 구역 정보 초기화")
        @Valid
        List<CreateHallSectionDto> sections
) {
}
