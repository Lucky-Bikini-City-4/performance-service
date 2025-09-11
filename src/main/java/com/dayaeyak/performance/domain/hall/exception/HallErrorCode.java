package com.dayaeyak.performance.domain.hall.exception;

import com.dayaeyak.performance.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HallErrorCode implements ErrorCode {
    HALL_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 공연장 이름이 존재합니다."),
    HALL_SECTION_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, "같은 공연장 내 중복된 구역 이름이 존재합니다."),
    HALL_NOT_FOUND(HttpStatus.NOT_FOUND, "공연장 정보를 찾을 수 없습니다."),
    HALL_SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "공연장 구역 정보를 찾을 수 없습니다."),
    HALL_ID_MISMATCH(HttpStatus.BAD_REQUEST, "공연 구역 ID가 소속된 공연장 ID를 입력해주세요."),
    CANNOT_DELETE_HALL(HttpStatus.BAD_REQUEST, "연결된 공연이 있어 공연장을 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
