package com.dayaeyak.performance.domain.hall.dto.request;

import com.dayaeyak.performance.domain.hall.enums.Region;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateHallRequestDto(
        @NotBlank(message = "공연장 이름은 필수 입력값입니다.") String hallName,
        @NotBlank(message = "공연장 주소는 필수 입력값입니다.") String address,
        @NotNull(message = "공연장이 위치한 지역을 입력해주세요.") Region city,
        @NotNull(message = "공연장의 수용인원을 입력해주세요.")
        @Min(value = 0, message = "수용인원은 0 이상이어야 합니다.") Integer capacity,
        @Valid List<CreateHallSectionDto> sections
) { }
