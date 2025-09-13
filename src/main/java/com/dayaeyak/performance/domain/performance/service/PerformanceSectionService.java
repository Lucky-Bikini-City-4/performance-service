package com.dayaeyak.performance.domain.performance.service;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.domain.performance.dto.response.ReadPerformanceSectionResponseDto;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSection;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSession;
import com.dayaeyak.performance.domain.performance.exception.PerformanceErrorCode;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSeatRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSectionRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceSectionService {
    private final PerformanceSectionRepository performanceSectionRepository;
    private final PerformanceSessionRepository performanceSessionRepository;
    private final PerformanceSeatRepository performanceSeatRepository;

    /* 공연 회차 구역 조회 */
    public ReadPerformanceSectionResponseDto readPerformanceSection(Long performanceId, Long sessionId, Long sectionId){
        PerformanceSection section = performanceSectionRepository.findByPerformanceSectionIdAndDeletedAtIsNull(sectionId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.SECTION_NOT_FOUND));

        // 회차 구역이 요청한 공연 회차에 속하는지 검증
        if (!Objects.equals(section.getPerformanceSession().getPerformanceSessionId(), sessionId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_SESSION_AND_SECTION);
        }

        // 공연 회차가 요청한 공연에 속하는지 검증
        if (!Objects.equals(section.getPerformanceSession().getPerformance().getPerformanceId(), performanceId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_PERFORMANCE_AND_SESSION);
        }

        return ReadPerformanceSectionResponseDto.from(section);
    }

    /* 공연 회차 구역 전체 조회 */
    public List<ReadPerformanceSectionResponseDto> readPerformanceSections(Long performanceId, Long sessionId){
        // 공연 회차 조회
        PerformanceSession session = performanceSessionRepository.findByPerformanceSessionIdAndDeletedAtIsNull(sessionId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.SESSION_NOT_FOUND));

        // 공연 회차가 해당 공연에 속하는지 검증
        if (!Objects.equals(session.getPerformance().getPerformanceId(), performanceId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_PERFORMANCE_AND_SESSION);
        }

        // 해당 공연 회차의 구역 전체 조회
        List<PerformanceSection> sections = performanceSectionRepository.findByPerformanceSessionAndDeletedAtIsNull(session);

        return sections.stream().
                map(ReadPerformanceSectionResponseDto::from)
                .toList();
    }
}
