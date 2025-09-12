package com.dayaeyak.performance.domain.cast.dto.response;

import com.dayaeyak.performance.common.dto.PageInfoDto;

import java.util.List;

public record ReadCastPageResponseDto(
        PageInfoDto pageInfo,
        List<ReadCastResponseDto> cast
) {
}
