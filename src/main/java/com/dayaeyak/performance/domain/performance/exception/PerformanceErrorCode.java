package com.dayaeyak.performance.domain.performance.exception;

import com.dayaeyak.performance.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PerformanceErrorCode implements ErrorCode {
    PERFORMANCE_ALREADY_OPENED(HttpStatus.BAD_REQUEST, "해당 공연의 예매가 이미 시작되어서 수정이 불가합니다."),
    CANNOT_CHANGE_ACTIVATION(HttpStatus.BAD_REQUEST, "지금은 공연의 활성화 상태를 변경할 수 없습니다."),
    PERFORMANCE_NOT_FOUND(HttpStatus.BAD_REQUEST, "공연 정보를 찾을 수 없습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "공연 시작일과 종료일의 범위가 올바르지 않습니다."),
    INVALID_TICKET_TIME_RANGE(HttpStatus.BAD_REQUEST, "티켓오픈일시와 마감일시의 범위가 올바르지 않습니다."),

    MISMATCHED_PERFORMANCE_ID(HttpStatus.BAD_REQUEST, "공연 ID가 일치하지 않습니다."),
    MISMATCHED_PERFORMANCE_AND_SESSION(HttpStatus.BAD_REQUEST, "선택한 회차가 해당 공연에 속하지 않습니다."),
    INVALID_SESSION_DATE(HttpStatus.BAD_REQUEST, "공연 회차 날짜는 공연 시작일과 마감일 사이여야 합니다."),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "공연 회차 정보를 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
