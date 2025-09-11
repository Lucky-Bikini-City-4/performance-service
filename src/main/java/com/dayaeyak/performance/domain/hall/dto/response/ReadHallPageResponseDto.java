package com.dayaeyak.performance.domain.hall.dto.response;

import com.dayaeyak.performance.common.dto.PageInfoDto;

import java.util.List;

public record ReadHallPageResponseDto(
        PageInfoDto pageInfo,
        List<ReadHallResponseDto> halls
) {
}
