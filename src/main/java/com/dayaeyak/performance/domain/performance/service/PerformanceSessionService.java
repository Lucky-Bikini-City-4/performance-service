package com.dayaeyak.performance.domain.performance.service;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.domain.performance.dto.request.CreateSessionRequestDto;
import com.dayaeyak.performance.domain.performance.dto.response.CreateSessionResponseDto;
import com.dayaeyak.performance.domain.performance.entity.Performance;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSession;
import com.dayaeyak.performance.domain.performance.exception.PerformanceErrorCode;
import com.dayaeyak.performance.domain.performance.repository.PerformanceRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
