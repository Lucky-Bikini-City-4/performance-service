package com.dayaeyak.performance.domain.performance.repository;

import com.dayaeyak.performance.domain.performance.entity.PerformanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceSessionRepository extends JpaRepository<PerformanceSession, Long> {
}
