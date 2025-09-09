package com.dayaeyak.performance.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),
    USER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "사용자 서비스에 일시적인 장애가 발생했습니다."),
    INVALID_PAGE_OR_SIZE(HttpStatus.BAD_REQUEST, "page, size는 0 이상의 정수여야 합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
