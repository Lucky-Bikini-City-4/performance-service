package com.dayaeyak.performance.common.enums;

import com.dayaeyak.performance.common.exception.AuthorizationErrorCode;
import com.dayaeyak.performance.common.exception.CustomException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum UserRole {

    MASTER,
    SELLER,
    NORMAL;

    @JsonCreator
    public static UserRole of(String role) {
        return Stream.of(UserRole.values())
                .filter(userRole -> userRole.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new CustomException(AuthorizationErrorCode.INVALID_USER_ROLE));
    }
}
