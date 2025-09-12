package com.dayaeyak.performance.domain.performance.service;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.domain.performance.dto.request.CreateSessionRequestDto;
import com.dayaeyak.performance.domain.performance.dto.request.UpdateSessionRequestDto;
import com.dayaeyak.performance.domain.performance.dto.response.CreateSessionResponseDto;
import com.dayaeyak.performance.domain.performance.entity.Performance;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSession;
import com.dayaeyak.performance.domain.performance.exception.PerformanceErrorCode;
import com.dayaeyak.performance.domain.performance.repository.PerformanceRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceSessionService {
    private final PerformanceSessionRepository performanceSessionRepository;
    private final PerformanceRepository performanceRepository;

    /* 공연 회차 생성 */
    @Transactional
    public CreateSessionResponseDto createSession(Long performanceId, CreateSessionRequestDto requestDto){
        // 경로 변수의 공연 ID와 요청 DTO의 공연 ID가 일치하지 않을 시 예외
        if(!Objects.equals(performanceId, requestDto.performanceId())){
            throw new CustomException(PerformanceErrorCode.MISMATCHED_PERFORMANCE_ID);
        }

        // 공연 ID로 공연 찾기
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNull(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        // 공연 회차 날짜가 시작일/마감일 범위가 아니면 예외 처리
        if(requestDto.date().isBefore(performance.getStartDate()) || requestDto.date().isAfter(performance.getEndDate())){
            throw new CustomException(PerformanceErrorCode.INVALID_SESSION_DATE);
        }

        // 공연 회차 엔티티 생성 및 저장
        PerformanceSession session = new PerformanceSession(performance, requestDto.date(), requestDto.time());
        PerformanceSession savedSession = performanceSessionRepository.save(session);

        // 응답 DTO 반환
        return new CreateSessionResponseDto(savedSession.getPerformanceSessionId());
    }

    /* 공연 회차 수정 */
    @Transactional
    public CreateSessionResponseDto updateSession(Long performanceId, Long sessionId, UpdateSessionRequestDto requestDto){
        // 공연 회차 조회
        PerformanceSession session = performanceSessionRepository.findByPerformanceSessionIdAndDeletedAtIsNull(sessionId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.SESSION_NOT_FOUND));

        // 공연 회차가 해당 공연에 속하는지 검증
        if (!Objects.equals(session.getPerformance().getPerformanceId(), performanceId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_PERFORMANCE_AND_SESSION);
        }

        // 공연 ID로 공연 찾기
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNull(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        // 티켓 오픈 시간이 이미 지났다면 수정 불가
        if (performance.getTicketOpenAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new CustomException(PerformanceErrorCode.PERFORMANCE_ALREADY_OPENED);
        }

        // 공연 회차 날짜가 시작일/마감일 범위가 아니면 예외 처리
        if(requestDto.date().isBefore(performance.getStartDate()) || requestDto.date().isAfter(performance.getEndDate())){
            throw new CustomException(PerformanceErrorCode.INVALID_SESSION_DATE);
        }

        session.update(requestDto.date(), requestDto.time());

        return new CreateSessionResponseDto(session.getPerformanceSessionId());
    }


}
