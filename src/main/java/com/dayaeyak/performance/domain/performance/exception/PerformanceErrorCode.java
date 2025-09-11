package com.dayaeyak.performance.domain.performance.exception;

import com.dayaeyak.performance.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PerformanceErrorCode implements ErrorCode {
    PERFORMANCE_ALREADY_OPENED(HttpStatus.BAD_REQUEST, "해당 공연의 예매가 이미 시작되어서 수정이 불가합니다."),
    PERFORMANCE_NOT_FOUND(HttpStatus.BAD_REQUEST, "공연 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
