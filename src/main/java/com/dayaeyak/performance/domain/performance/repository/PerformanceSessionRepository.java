package com.dayaeyak.performance.domain.performance.repository;

import com.dayaeyak.performance.domain.performance.entity.PerformanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerformanceSessionRepository extends JpaRepository<PerformanceSession, Long> {
    Optional<PerformanceSession> findByPerformanceSessionIdAndDeletedAtIsNull(Long sessionId);
}
