package com.dayaeyak.performance.domain.cast.service;

import com.dayaeyak.performance.common.dto.PageInfoDto;
import com.dayaeyak.performance.common.exception.CustomException;
import com.dayaeyak.performance.common.exception.GlobalErrorCode;
import com.dayaeyak.performance.domain.cast.dto.request.CreateCastRequestDto;
import com.dayaeyak.performance.domain.cast.dto.response.CreateCastResponseDto;
import com.dayaeyak.performance.domain.cast.dto.response.ReadCastPageResponseDto;
import com.dayaeyak.performance.domain.cast.dto.response.ReadCastResponseDto;
import com.dayaeyak.performance.domain.cast.entity.Cast;
import com.dayaeyak.performance.domain.cast.exception.CastErrorCode;
import com.dayaeyak.performance.domain.cast.repository.CastRepository;
import com.dayaeyak.performance.domain.performance.entity.Performance;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CastService {
    private final CastRepository castRepository;

    /*출연진 생성*/
    @Transactional
    public CreateCastResponseDto createCast(CreateCastRequestDto requestDto) {
        // 출연진 이름 중복 검색
        if(castRepository.existsByCastNameAndDeletedAtIsNull(requestDto.castName())){
            throw new CustomException(CastErrorCode.CAST_NAME_DUPLICATED);
        }

        // 출연진 엔티티 생성 및 저장
        Cast cast = new Cast(requestDto.castName());
        Cast savedCast = castRepository.save(cast);

        // 응답 DTO 생성 및 반환
        return new CreateCastResponseDto(savedCast.getCastId(), savedCast.getCastName());
    }

    /*출연진 수정*/
    @Transactional
    public CreateCastResponseDto updateCast(Long castId, CreateCastRequestDto requestDto) {
        // 기존 출연진 조회
        Cast existingCast = castRepository.findByCastIdAndDeletedAtIsNull(castId)
                .orElseThrow(() -> new CustomException(CastErrorCode.CAST_NOT_FOUND));

        // 출연진 이름 중복 검색 (자신 제외)
        if(castRepository.existsByCastNameAndCastIdNotAndDeletedAtIsNull(requestDto.castName(), castId)){
            throw new CustomException(CastErrorCode.CAST_NAME_DUPLICATED);
        }

        // 출연진 정보 수정
        existingCast.update(requestDto.castName());

        return new CreateCastResponseDto(existingCast.getCastId(), existingCast.getCastName());
    }

    /* 출연진 단건 조회 */
    public ReadCastResponseDto readCast(Long castId) {
        // 기존 출연진 조회
        Cast cast = castRepository.findByCastIdAndDeletedAtIsNull(castId)
                .orElseThrow(() -> new CustomException(CastErrorCode.CAST_NOT_FOUND));

        // 공연 ID만 추출하여 리스트화
        List<Long> performanceIdList = cast.getPerformanceList()
                .stream()
                .filter(p -> p.getDeletedAt() == null)
                .map(Performance::getPerformanceId)
                .toList();

        // 응답 DTO로 반환
        return new ReadCastResponseDto(cast.getCastId(), cast.getCastName(), performanceIdList);
    }

    /* 출연진 페이징 조회 */
    public ReadCastPageResponseDto readCastList(int page, int size) {
        if(page < 0 || size < 0){
            throw new CustomException(GlobalErrorCode.INVALID_PAGE_OR_SIZE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Cast> castPage = castRepository.findByDeletedAtIsNull(pageable);

        // PageInfo 생성
        PageInfoDto pageInfo = new PageInfoDto(
                castPage.getNumber() + 1, // 0-base -> 1-base
                castPage.getSize(),
                castPage.getTotalElements(),
                castPage.getTotalPages(),
                castPage.isLast()
        );

        List<ReadCastResponseDto> data = castPage.getContent()
                .stream()
                .map(ReadCastResponseDto::from)
                .toList();

        return new ReadCastPageResponseDto(pageInfo, data);
    }

    /* 출연진 삭제 */
    @Transactional
    public Void deleteCast(Long castId) {
        Cast cast = castRepository.findByCastIdAndDeletedAtIsNull(castId)
                .orElseThrow(() -> new CustomException(CastErrorCode.CAST_NOT_FOUND));

        // 관련 공연이 있는지 확인, 있으면 예외 처리
        boolean hasPerformances = cast.getPerformanceList()
                .stream()
                .anyMatch(p -> p.getDeletedAt() == null);
        if (hasPerformances) {
            throw new CustomException(CastErrorCode.CAST_LINKED_PERFORMANCE);
        }

        cast.delete();
        return null;

    }
}
