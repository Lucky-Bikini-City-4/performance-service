package com.dayaeyak.performance.domain.performance.dto.response;

import com.dayaeyak.performance.domain.cast.entity.Cast;

public record CastDto(
        Long castId,
        String name
) {
    public static CastDto from(Cast cast) {
        return new CastDto(cast.getCastId(), cast.getCastName());
    }
}
