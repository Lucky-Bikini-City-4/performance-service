package com.dayaeyak.performance.domain.performance.entity;

import com.dayaeyak.performance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "performance_sessions")
public class PerformanceSession extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long performanceSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    public PerformanceSession(Performance performance, LocalDate date, LocalTime time) {
        this.performance = performance;
        this.date = date;
        this.time = time;
    }

    public void update(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }
}
