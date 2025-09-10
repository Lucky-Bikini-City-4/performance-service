package com.dayaeyak.performance.domain.hall.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MinimumAge {
    ALL("전체 관람가"),
    R15("15세 이상 관람가"),
    R18("18세 이상 관람가");
    private final String minimumAge;
}
