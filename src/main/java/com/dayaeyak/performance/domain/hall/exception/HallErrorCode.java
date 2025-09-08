package com.dayaeyak.performance.domain.hall.exception;

import com.dayaeyak.performance.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HallErrorCode implements ErrorCode {
    HALL_NOT_FOUND(HttpStatus.NOT_FOUND, "공연장 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
