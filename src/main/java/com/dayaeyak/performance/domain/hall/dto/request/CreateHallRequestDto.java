package com.dayaeyak.performance.domain.hall.dto.request;

import com.dayaeyak.performance.domain.hall.enums.Region;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateHallRequestDto(
        @NotBlank(message = "공연장 이름은 필수 입력값입니다.") String hallName,
        @NotBlank(message = "공연장 주소는 필수 입력값입니다.") String address,
        @NotNull(message = "공연장이 위치한 지역을 입력해주세요.") Region city,
        @NotNull(message = "공연장의 수용인원을 입력해주세요.") Integer capacity,
        List<CreateHallSectionDto> sections
) { }
