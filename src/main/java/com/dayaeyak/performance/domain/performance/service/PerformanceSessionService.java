package com.dayaeyak.performance.domain.performance.service;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.domain.hall.entity.HallSection;
import com.dayaeyak.performance.domain.hall.repository.HallSectionRepository;
import com.dayaeyak.performance.domain.performance.dto.request.CreateSessionRequestDto;
import com.dayaeyak.performance.domain.performance.dto.request.UpdateSessionRequestDto;
import com.dayaeyak.performance.domain.performance.dto.response.CreateSessionResponseDto;
import com.dayaeyak.performance.domain.performance.dto.response.ReadSessionResponseDto;
import com.dayaeyak.performance.domain.performance.entity.Performance;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSeat;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSection;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSession;
import com.dayaeyak.performance.domain.performance.exception.PerformanceErrorCode;
import com.dayaeyak.performance.domain.performance.repository.PerformanceRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSeatRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSectionRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceSessionService {
    private final PerformanceSessionRepository performanceSessionRepository;
    private final PerformanceRepository performanceRepository;
    private final HallSectionRepository hallSectionRepository;
    private final PerformanceSectionRepository performanceSectionRepository;
    private final PerformanceSeatRepository performanceSeatRepository;

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

        // 공연장 구역 정보 조회
        List<HallSection> hallSections = hallSectionRepository.findByHallAndDeletedAtIsNull(performance.getHall());

        // 요청 DTO의 구역 이름과 실제 공연장 구역 매칭 시작
        Set<String> hallSectionNames = hallSections.stream()
                .map(HallSection::getSectionName)
                .collect(Collectors.toSet());

        Set<String> requestSectionNames = requestDto.sectionPrices().keySet();

        // DTO에 있는 구역이 실제 공연장에 없는 경우 예외 처리
        Set<String> invalidSections = requestSectionNames.stream()
                .filter(sectionName -> !hallSectionNames.contains(sectionName))
                .collect(Collectors.toSet());
        if (!invalidSections.isEmpty()) {
            throw new CustomException(PerformanceErrorCode.INVALID_SECTION_NAMES);
        }

        // 공연장에 있는 구역이 요청 DTO에 없을 때
        Set<String> missingSections = hallSectionNames.stream()
                .filter(sectionName -> !requestSectionNames.contains(sectionName))
                .collect(Collectors.toSet());
        if (!missingSections.isEmpty()) {
            throw new CustomException(PerformanceErrorCode.MISSING_SECTION_PRICES);
        }

        // 각 공연장 구역에 대해 공연 구역 및 좌석 생성
        for (HallSection hallSection : hallSections) {
            String sectionName = hallSection.getSectionName();
            Integer seatPrice = requestDto.sectionPrices().get(sectionName);

            // 가격이 0 이하인 경우 예외 처리
            if (seatPrice <= 0) {
                throw new CustomException(PerformanceErrorCode.INVALID_SEAT_PRICE);
            }

            // 공연 구역 생성
            PerformanceSection performanceSection = PerformanceSection.builder()
                    .performanceSession(savedSession)
                    .sectionName(sectionName)
                    .seatPrice(seatPrice)
                    .remainingSeats(hallSection.getSeats())
                    .build();

            PerformanceSection savedPerformanceSection = performanceSectionRepository.save(performanceSection);

            // 해당 구역의 모든 좌석 생성
            for (int seatNumber = 1; seatNumber <= hallSection.getSeats(); seatNumber++) {
                PerformanceSeat performanceSeat = new PerformanceSeat(savedPerformanceSection, seatNumber);
                performanceSeatRepository.save(performanceSeat);
            }
        }

        // 응답 DTO 반환
        return new CreateSessionResponseDto(savedSession.getPerformanceSessionId());
    }

    /* 공연 회차 수정 */
    @Transactional
    public CreateSessionResponseDto updateSession(Long performanceId, Long sessionId, UpdateSessionRequestDto requestDto){
        // 공연 회차 조회
        PerformanceSession session = findSessionById(sessionId);

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

    /* 공연 회차 단건 조회 */
    public ReadSessionResponseDto readSession(Long performanceId, Long sessionId){
        // 공연 회차 조회
        PerformanceSession session = performanceSessionRepository.findByPerformanceSessionIdAndDeletedAtIsNull(sessionId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.SESSION_NOT_FOUND));

        // 공연 회차가 해당 공연에 속하는지 검증
        if (!Objects.equals(session.getPerformance().getPerformanceId(), performanceId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_PERFORMANCE_AND_SESSION);
        }

        return ReadSessionResponseDto.from(session);
    }

    /* 공연 회차 전체 조회 */
    public List<ReadSessionResponseDto> readSessionList(Long performanceId){
        // 공연 ID로 공연 찾기
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNull(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        // 해당 공연의 전체 회차 조회
        List<PerformanceSession> sessions = performanceSessionRepository.findByPerformanceAndDeletedAtIsNull(performance);

        // 응답 DTO 리스트 형태로 반환
        return sessions.stream()
                .map(ReadSessionResponseDto::from)
                .toList();
    }

    /* 공연 회차 삭제 */
    @Transactional
    public Void deleteSession(Long performanceId, Long sessionId){
        // 공연 회차 조회
        PerformanceSession session = findSessionById(sessionId);

        // 공연 회차가 해당 공연에 속하는지 검증
        if (!Objects.equals(session.getPerformance().getPerformanceId(), performanceId)) {
            throw new CustomException(PerformanceErrorCode.MISMATCHED_PERFORMANCE_AND_SESSION);
        }

        // 공연 ID로 공연 찾기
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNull(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        // 티켓 오픈 시간이 이미 지났다면 삭제 불가
        if (performance.getTicketOpenAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new CustomException(PerformanceErrorCode.PERFORMANCE_ALREADY_OPENED);
        }

        // 해당 회차의 공연 구역들 조회
        List<PerformanceSection> performanceSections = performanceSectionRepository.findByPerformanceSessionAndDeletedAtIsNull(session);

        // 공연 좌석들 소프트 삭제
        for (PerformanceSection performanceSection : performanceSections) {
            List<PerformanceSeat> seats = performanceSeatRepository.findByPerformanceSectionAndDeletedAtIsNull(performanceSection);
            for (PerformanceSeat seat : seats) {
                seat.delete();
            }
        }

        // 공연 구역들 소프트 삭제
        for (PerformanceSection performanceSection : performanceSections) {
            performanceSection.delete();
        }

        // 공연 회차 소프트 삭제
        session.delete();

        return null;
    }

    // 공연 회차 ID로 검색
    private PerformanceSession findSessionById(Long sessionId){
        return performanceSessionRepository.findByPerformanceSessionIdAndDeletedAtIsNull(sessionId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.SESSION_NOT_FOUND));
    }

}
