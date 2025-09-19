package com.dayaeyak.performance.common.dto;

import com.dayaeyak.performance.common.enums.UserRole;

public record Passport(
        Long userId,

        UserRole role
) {
}
