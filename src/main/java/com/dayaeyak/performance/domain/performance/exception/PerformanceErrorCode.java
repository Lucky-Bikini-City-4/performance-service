package com.dayaeyak.performance.domain.performance.exception;

import com.dayaeyak.performance.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PerformanceErrorCode implements ErrorCode {
    PERFORMANCE_NOT_FOUND(HttpStatus.BAD_REQUEST, "공연 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
