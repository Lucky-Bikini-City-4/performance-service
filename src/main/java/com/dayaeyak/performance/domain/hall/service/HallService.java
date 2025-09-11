package com.dayaeyak.performance.domain.hall.service;

import com.dayaeyak.performance.common.dto.PageInfoDto;
import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.common.exception.GlobalErrorCode;
import com.dayaeyak.performance.domain.hall.dto.request.CreateHallRequestDto;
import com.dayaeyak.performance.domain.hall.dto.request.CreateHallSectionDto;
import com.dayaeyak.performance.domain.hall.dto.request.UpdateHallRequestDto;
import com.dayaeyak.performance.domain.hall.dto.response.CreateHallResponseDto;
import com.dayaeyak.performance.domain.hall.dto.response.ReadHallPageResponseDto;
import com.dayaeyak.performance.domain.hall.dto.response.ReadHallResponseDto;
import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.hall.entity.HallSection;
import com.dayaeyak.performance.domain.hall.enums.Region;
import com.dayaeyak.performance.domain.hall.exception.HallErrorCode;
import com.dayaeyak.performance.domain.hall.repository.HallRepository;
import com.dayaeyak.performance.domain.hall.repository.HallSectionRepository;
import com.dayaeyak.performance.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HallService {
    private final HallRepository hallRepository;
    private final HallSectionRepository hallSectionRepository;
    private final PerformanceRepository performanceRepository;

    /* 공연장 생성 */
    @Transactional
    public CreateHallResponseDto createHall(CreateHallRequestDto requestDto) {
        // 공연장 이름 중복 검색
        if (hallRepository.existsByHallNameAndDeletedAtIsNull(requestDto.hallName())) {
            throw new CustomException(HallErrorCode.HALL_NAME_DUPLICATED);
        }

        // 공연장 엔티티 생성 및 저장
        Hall hall = Hall.builder()
                .hallName(requestDto.hallName())
                .address(requestDto.address())
                .region(requestDto.region())
                .capacity(requestDto.capacity())
                .build();
        Hall savedHall = hallRepository.save(hall);

        // 공연장 구역 엔티티 리스트 생성 및 저장
        List<HallSection> hallSections = createHallSections(requestDto.sections(), hall);
        List<HallSection> savedHallSections = hallSectionRepository.saveAll(hallSections);

        // 응답 DTO 생성 및 반환
        return new CreateHallResponseDto(savedHall.getHallId());
    }

    /* 공연장 수정 */
    @Transactional
    public CreateHallResponseDto updateHall(Long hallId, UpdateHallRequestDto requestDto) {
        // 기존 공연장 조회
        Hall existingHall = hallRepository.findByHallIdAndDeletedAtIsNull(hallId)
                .orElseThrow(() -> new CustomException(HallErrorCode.HALL_NOT_FOUND));

        // 공연장 이름 중복 검색 (자기 자신 제외)
        if (hallRepository.existsByHallNameAndHallIdNotAndDeletedAtIsNull(requestDto.hallName(), hallId)) {
            throw new CustomException(HallErrorCode.HALL_NAME_DUPLICATED);
        }

        // 관련 공연이 있는지 확인
        if(performanceRepository.existsByHallAndEndDateGreaterThanEqualAndDeletedAtIsNull(existingHall, LocalDate.now())){
            throw new CustomException(HallErrorCode.CANNOT_UPDATE_HALL);
        }

        // 공연장 정보 수정
        existingHall.update(requestDto.hallName(), requestDto.address(), requestDto.region(), requestDto.capacity());

        // 구역 정보가 있으면 기존 구역 삭제 후 새로 저장
        if(requestDto.sections() != null && !requestDto.sections().isEmpty()) {
            hallSectionRepository.deleteByHall(existingHall);

            // 공연장 구역 엔티티 리스트 생성 및 저장
            List<HallSection> hallSections = createHallSections(requestDto.sections(), existingHall);
            hallSectionRepository.saveAll(hallSections);
        }

        return new CreateHallResponseDto(existingHall.getHallId());
    }

    /* 공연장 단건 조회 */
    public ReadHallResponseDto readHall(Long hallId) {
        Hall hall = hallRepository.findByHallIdAndDeletedAtIsNull(hallId)
                .orElseThrow(() -> new CustomException(HallErrorCode.HALL_NOT_FOUND));

        return ReadHallResponseDto.from(hall);
    }

    /* 공연장 페이징 조회 */
    public ReadHallPageResponseDto readHallList(int page, int size, Region region) {
        if(page < 0 || size < 0){
            throw new CustomException(GlobalErrorCode.INVALID_PAGE_OR_SIZE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Hall> halls;

        // 공연장 지역별 검색, 지역이 없을 경우 전체에서 검색
        if(region == null){
            halls = hallRepository.findByDeletedAtIsNull(pageable);
        } else{
            halls = hallRepository.findByDeletedAtIsNullAndRegion(pageable, region);
        }

        // PageInfo 생성
        PageInfoDto pageInfo = new PageInfoDto(
                halls.getNumber() + 1, // 0-base -> 1-base
                halls.getSize(),
                halls.getTotalElements(),
                halls.getTotalPages(),
                halls.isLast()
        );

        List<ReadHallResponseDto> data = halls.getContent()
                .stream()
                .map(ReadHallResponseDto::from)
                .toList();

        return new ReadHallPageResponseDto(pageInfo, data);
    }

    /* 공연장 삭제 */
    @Transactional
    public Void deleteHall(Long hallId) {
        Hall hall = hallRepository.findByHallIdAndDeletedAtIsNull(hallId)
                .orElseThrow(() -> new CustomException(HallErrorCode.HALL_NOT_FOUND));

        // 관련 공연이 있는지 확인
        if(performanceRepository.existsByHallAndDeletedAtIsNull(hall)){
            throw new CustomException(HallErrorCode.CANNOT_DELETE_HALL);
        }

        // Hall, HallSection Soft Delete
        hall.delete();
        List<HallSection> sections = hallSectionRepository.findByHallAndDeletedAtIsNull(hall);
        sections.forEach(HallSection::delete);

        return null;
    }

    /* 공연장 구역 엔티티 리스트 생성 및 반환 메서드 */
    private List<HallSection> createHallSections(List<CreateHallSectionDto> requestDto, Hall hall) {
        return requestDto.stream()
                .map(sectionDto -> HallSection.builder()
                        .hall(hall)
                        .sectionName(sectionDto.sectionName())
                        .seats(sectionDto.seats())
                        .seatPrice(sectionDto.seatPrice())
                        .build())
                .toList();
    }
}
