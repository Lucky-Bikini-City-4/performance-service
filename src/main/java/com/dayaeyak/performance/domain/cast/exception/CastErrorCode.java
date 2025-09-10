package com.dayaeyak.performance.domain.cast.exception;

import com.dayaeyak.performance.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CastErrorCode implements ErrorCode {
    CAST_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 출연진 이름이 존재합니다"),
    CAST_NOT_FOUND(HttpStatus.NOT_FOUND, "출연진 정보를 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
