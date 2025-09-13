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

    @Query(value = "SELECT is_sold_out FROM performance_seats " +
            "WHERE performance_section_id = :sectionId " +
            "ORDER BY seat_number ASC", nativeQuery = true)
    List<Boolean> findIsSoldOutBySectionIdNative(@Param("sectionId") Long sectionId);

}
