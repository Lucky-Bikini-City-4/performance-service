package com.dayaeyak.performance.domain.performance.service;

import com.dayaeyak.performance.common.dto.PageInfoDto;
import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.common.exception.GlobalErrorCode;
import com.dayaeyak.performance.domain.cast.entity.Cast;
import com.dayaeyak.performance.domain.cast.exception.CastErrorCode;
import com.dayaeyak.performance.domain.cast.repository.CastRepository;
import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.hall.enums.Region;
import com.dayaeyak.performance.domain.hall.exception.HallErrorCode;
import com.dayaeyak.performance.domain.hall.repository.HallRepository;
import com.dayaeyak.performance.domain.performance.dto.request.ChangePerformanceRequestDto;
import com.dayaeyak.performance.domain.performance.dto.request.CreatePerformanceRequestDto;
import com.dayaeyak.performance.domain.performance.dto.request.UpdatePerformanceRequestDto;
import com.dayaeyak.performance.domain.performance.dto.response.CreatePerformanceResponseDto;
import com.dayaeyak.performance.domain.performance.dto.response.ReadPerformancePageResponseDto;
import com.dayaeyak.performance.domain.performance.dto.response.ReadPerformanceResponseDto;
import com.dayaeyak.performance.domain.performance.entity.Performance;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSeat;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSection;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSession;
import com.dayaeyak.performance.domain.performance.enums.Type;
import com.dayaeyak.performance.domain.performance.exception.PerformanceErrorCode;
import com.dayaeyak.performance.domain.performance.repository.PerformanceRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSeatRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSectionRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {
    private final PerformanceRepository performanceRepository;
    private final HallRepository hallRepository;
    private final CastRepository castRepository;
    private final PerformanceSessionRepository performanceSessionRepository;
    private final PerformanceSectionRepository performanceSectionRepository;
    private final PerformanceSeatRepository performanceSeatRepository;

    /* 공연 생성 */
    @Transactional
    public CreatePerformanceResponseDto createPerformance(CreatePerformanceRequestDto requestDto){
        // 공연장 ID로 공연장 객체 찾기
        Hall hall = hallRepository.findByHallIdAndDeletedAtIsNull(requestDto.hallId())
                .orElseThrow(() -> new CustomException(HallErrorCode.HALL_NOT_FOUND));

        // 출연진 목록 List로 변환
        List<String> castNames = Arrays.stream(requestDto.castList().split(",")).toList();

        // 출연진 이름으로 출연진 찾아서 리스트화
        List<Cast> casts = castNames.stream()
                .map(name -> castRepository.findByCastNameAndDeletedAtIsNull(name)
                        .orElseThrow(()->new CustomException(CastErrorCode.CAST_NOT_FOUND)))
                .toList();

        // 공연 엔티티 생성 및 저장
        Performance performance = Performance.builder()
                .sellerId(requestDto.sellerId())
                .hall(hall)
                .performanceName(requestDto.performanceName())
                .description(requestDto.description())
                .type(requestDto.type())
                .grade(requestDto.grade())
                .startDate(requestDto.startDate())
                .endDate(requestDto.endDate())
                .ticketOpenAt(Timestamp.valueOf(requestDto.ticketOpenAt()))
                .ticketCloseAt(Timestamp.valueOf(requestDto.ticketCloseAt()))
                .isActivated(requestDto.isActivated() == null || requestDto.isActivated())
                .build();
        performance.getCastList().addAll(casts);
        Performance savedPerformance = performanceRepository.save(performance);

        // 응답 DTO로 반환
        return new CreatePerformanceResponseDto(savedPerformance.getPerformanceId());
    }

    /* 공연 수정 (요청 DTO의 필드를 하나씩 확인해서 들어온 값만 수정) */
    @Transactional
    public CreatePerformanceResponseDto updatePerformance(Long performanceId, UpdatePerformanceRequestDto requestDto){
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNull(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        // 티켓 오픈 시간이 이미 지났다면 수정 불가
        if (performance.getTicketOpenAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new CustomException(PerformanceErrorCode.PERFORMANCE_ALREADY_OPENED);
        }

        // 공연명 수정
        if (requestDto.performanceName() != null && !requestDto.performanceName().isBlank()) {
            performance.updatePerformanceName(requestDto.performanceName());
        }

        // 공연장 수정
        if (requestDto.hallId() != null) {
            Hall hall = hallRepository.findById(requestDto.hallId())
                    .orElseThrow(() -> new CustomException(HallErrorCode.HALL_NOT_FOUND));
            performance.changeHall(hall);
        }

        // 출연진 수정
        if (requestDto.castList() != null && !requestDto.castList().isBlank()) {
            // 출연진 목록 List로 변환
            List<String> castNames = Arrays.stream(requestDto.castList().split(",")).toList();

            // 출연진 이름으로 출연진 찾아서 리스트화
            List<Cast> casts = castNames.stream()
                    .map(name -> castRepository.findByCastNameAndDeletedAtIsNull(name)
                            .orElseThrow(()->new CustomException(CastErrorCode.CAST_NOT_FOUND)))
                    .toList();

            // 기존 관계 정리
            for (Cast cast : performance.getCastList()) {
                cast.getPerformanceList().remove(performance);
            }

            performance.getCastList().clear();
            performance.getCastList().addAll(casts);

            // 새로운 관계 설정
            for (Cast cast : casts) {
                if (!cast.getPerformanceList().contains(performance)) {
                    cast.getPerformanceList().add(performance);
                }
            }
        }

        // 설명 수정
        if (requestDto.description() != null && !requestDto.description().isBlank()) {
            performance.updateDescription(requestDto.description());
        }

        // 타입 수정
        if (requestDto.type() != null) {
            performance.updateType(requestDto.type());
        }

        // 관람 등급 수정
        if (requestDto.grade() != null) {
            performance.updateGrade(requestDto.grade());
        }

        // 시작일/마감일 수정
        if (requestDto.startDate() != null) {
            LocalDate startDate = requestDto.startDate();

            // 종료일이 이미 설정되어 있다면 시작일이 종료일보다 이후가 되지 않도록 검증
            if (performance.getEndDate() != null && startDate.isAfter(performance.getEndDate())) {
                throw new CustomException(PerformanceErrorCode.INVALID_DATE_RANGE);
            }

            performance.updateStartDate(startDate);
        }
        if (requestDto.endDate() != null) {
            LocalDate endDate = requestDto.endDate();

            // 시작일이 이미 설정되어 있다면 종료일이 시작일보다 이전이 되지 않도록 검증
            if (performance.getStartDate() != null && endDate.isBefore(performance.getStartDate())) {
                throw new CustomException(PerformanceErrorCode.INVALID_DATE_RANGE);
            }

            performance.updateEndDate(endDate);
        }

        // 티켓 오픈/마감 수정
        if (requestDto.ticketOpenAt() != null) {
            Timestamp ticketOpenAt = Timestamp.valueOf(requestDto.ticketOpenAt());

            // 티켓 마감 시간이 이미 설정되어 있다면 오픈 시간이 마감 시간보다 이후가 되지 않도록 검증
            if (performance.getTicketCloseAt() != null && ticketOpenAt.after(performance.getTicketCloseAt())) {
                throw new CustomException(PerformanceErrorCode.INVALID_TICKET_TIME_RANGE);
            }

            performance.updateTicketOpenAt(ticketOpenAt);
        }
        if (requestDto.ticketCloseAt() != null) {
            Timestamp ticketCloseAt = Timestamp.valueOf(requestDto.ticketCloseAt());

            // 티켓 오픈 시간이 이미 설정되어 있다면 마감 시간이 오픈 시간보다 이전이 되지 않도록 검증
            if (performance.getTicketOpenAt() != null && ticketCloseAt.before(performance.getTicketOpenAt())) {
                throw new CustomException(PerformanceErrorCode.INVALID_TICKET_TIME_RANGE);
            }

            performance.updateTicketCloseAt(ticketCloseAt);
        }

        return new CreatePerformanceResponseDto(performance.getPerformanceId());
    }

    /* 공연 활성화 상태 변경 */
    @Transactional
    public Boolean changeIsActivated(Long performanceId, ChangePerformanceRequestDto requestDto) {
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNull(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        // 티켓 오픈 일시 이후, 마감 일시 이전이면 상태 변경 불가
        if (performance.getTicketOpenAt().before(new Timestamp(System.currentTimeMillis()))
            && performance.getTicketCloseAt().after(new Timestamp(System.currentTimeMillis()))) {
            throw new CustomException(PerformanceErrorCode.CANNOT_CHANGE_ACTIVATION);
        }

        performance.updateActivation(requestDto.isActivated());
        return performance.getIsActivated();
    }

    /* 공연 단건 조회 */
    public ReadPerformanceResponseDto readPerformance(Long performanceId) {
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNullAndIsActivatedIsTrue(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        return ReadPerformanceResponseDto.from(performance);
    }

    /* 공연 페이징 조회 */
    public ReadPerformancePageResponseDto readPerformanceList(int page, int size, Type type, Region region) {
        if(page < 0 || size < 0){
            throw new CustomException(GlobalErrorCode.INVALID_PAGE_OR_SIZE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "startDate"));
        Page<Performance> performances;

        if (type == null && region == null) {
            // 타입도 없고 지역도 없으면 전체 조회
            performances = performanceRepository.findByDeletedAtIsNullAndIsActivatedIsTrue(pageable);
        } else if (type != null && region == null) {
            // 타입만 있는 경우
            performances = performanceRepository.findByDeletedAtIsNullAndTypeAndIsActivatedIsTrue(pageable, type);
        } else if (type == null) {
            // 지역만 있는 경우
            performances = performanceRepository.findByDeletedAtIsNullAndIsActivatedIsTrueAndHall_Region(pageable, region);
        } else {
            // 타입과 지역 둘 다 있는 경우
            performances = performanceRepository.findByDeletedAtIsNullAndTypeAndIsActivatedIsTrueAndHall_Region(pageable, type, region);
        }

        // PageInfo 생성
        PageInfoDto pageInfo = PageInfoDto.from(performances);

        // List<DTO> 형태로 변환
        List<ReadPerformanceResponseDto> data = performances.getContent()
                .stream()
                .map(ReadPerformanceResponseDto::from)
                .toList();

        return new ReadPerformancePageResponseDto(pageInfo, data);
    }

    /* 공연 삭제 */
    @Transactional
    public Void deletePerformance(Long performanceId) {
        Performance performance = performanceRepository.findByPerformanceIdAndDeletedAtIsNull(performanceId)
                .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

        // 티켓 오픈 일시가 이미 지났다면 삭제 불가
        if (performance.getTicketOpenAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new CustomException(PerformanceErrorCode.PERFORMANCE_ALREADY_OPENED);
        }

        // 공연 삭제
        performance.delete();
        // 공연 회차 삭제
        List<PerformanceSession> sessions = performanceSessionRepository.findByPerformanceAndDeletedAtIsNull(performance);
        sessions.forEach(PerformanceSession::delete);
        // 회차 구역 삭제
        for(PerformanceSession session: sessions){
            List<PerformanceSection> sections = performanceSectionRepository.findByPerformanceSessionAndDeletedAtIsNull(session);
            sections.forEach(PerformanceSection::delete);
            // 회차 구역 좌석 삭제
            for(PerformanceSection section: sections){
                List<PerformanceSeat> seats = performanceSeatRepository.findByPerformanceSectionAndDeletedAtIsNull(section);
                seats.forEach(PerformanceSeat::delete);
            }
        }
        return null;
    }
}
