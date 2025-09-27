package com.dayaeyak.performance.domain.performance.repository;

import com.dayaeyak.performance.domain.performance.entity.PerformanceSeat;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PerformanceSeatRepository extends JpaRepository<PerformanceSeat, Long> {
    List<PerformanceSeat> findByPerformanceSectionAndDeletedAtIsNull(PerformanceSection performanceSection);
    Optional<PerformanceSeat> findByPerformanceSeatIdAndDeletedAtIsNull(Long performanceSeatId);
    List<PerformanceSeat> findAllByPerformanceSeatIdInAndDeletedAtIsNull(List<Long> seatIds);

    @Query(value = "SELECT ps.performance_seat_id, ps.is_sold_out " +
            "FROM performance_seats ps " +
            "WHERE ps.performance_section_id = :sectionId " +
            "ORDER BY ps.performance_seat_id ASC", nativeQuery = true)
    List<Object[]> findSeatIdAndIsSoldOutBySectionIdNative(
            @Param("sectionId") Long sectionId
    );
}
