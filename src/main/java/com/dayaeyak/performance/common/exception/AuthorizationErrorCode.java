package com.dayaeyak.performance.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthorizationErrorCode implements ErrorCode {

    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 유저 ID입니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 유저 권한입니다."),
    REQUEST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "요청 접근 권한이 부족합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}