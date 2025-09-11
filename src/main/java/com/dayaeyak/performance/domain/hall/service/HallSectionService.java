package com.dayaeyak.performance.domain.hall.service;

import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.domain.hall.dto.request.CreateHallSectionDto;
import com.dayaeyak.performance.domain.hall.dto.response.UpdateHallSectionResponseDto;
import com.dayaeyak.performance.domain.hall.entity.HallSection;
import com.dayaeyak.performance.domain.hall.exception.HallErrorCode;
import com.dayaeyak.performance.domain.hall.repository.HallRepository;
import com.dayaeyak.performance.domain.hall.repository.HallSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HallSectionService {
    private final HallSectionRepository hallSectionRepository;
    private final HallRepository hallRepository;

    /* 공연장 구역 수정 */
    @Transactional
    public UpdateHallSectionResponseDto updateHallSection(Long hallId, Long hallSectionId, CreateHallSectionDto requestDto) {
        // 구역 ID로 조회
        HallSection hallSection = hallSectionRepository.findByHallSectionIdAndDeletedAtIsNull(hallSectionId)
                .orElseThrow(() -> new CustomException(HallErrorCode.HALL_SECTION_NOT_FOUND));

        // 조회된 구역의 공연장 ID와 입력된 hallId 비교
        if (!hallSection.getHall().getHallId().equals(hallId)) {
            throw new CustomException(HallErrorCode.HALL_ID_MISMATCH);
        }

        // 같은 공연장 내 중복되는 이름이 있는지 확인 (자기 자신 제외)
        if (hallSectionRepository.existsByHall_HallIdAndSectionNameAndHallSectionIdNotAndDeletedAtIsNull(
                hallId, requestDto.sectionName(), hallSectionId)) {
            throw new CustomException(HallErrorCode.HALL_SECTION_NAME_DUPLICATED);
        }

        // 구역 정보 수정
        hallSection.update(requestDto.sectionName(), requestDto.seats(), requestDto.seatPrice());

        return new UpdateHallSectionResponseDto(hallSectionId);
    }
}
