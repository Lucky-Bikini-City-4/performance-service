package com.dayaeyak.performance.domain.performance.repository;

import com.dayaeyak.performance.domain.performance.entity.PerformanceSeat;
import com.dayaeyak.performance.domain.performance.entity.PerformanceSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceSeatRepository extends JpaRepository<PerformanceSeat, Long> {
    List<PerformanceSeat> findByPerformanceSectionAndDeletedAtIsNull(PerformanceSection performanceSection);
}
