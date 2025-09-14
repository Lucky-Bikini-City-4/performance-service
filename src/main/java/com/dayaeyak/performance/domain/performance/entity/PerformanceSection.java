package com.dayaeyak.performance.domain.performance.entity;

import com.dayaeyak.performance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "performance_sections")
public class PerformanceSection extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long performanceSectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_session_id", nullable = false)
    private PerformanceSession performanceSession;

    @Column(length = 50, nullable = false)
    private String sectionName;

    @Column(nullable = false)
    private Integer seatPrice;

    @Column(nullable = false)
    private Integer remainingSeats;

    @Builder
    public PerformanceSection(PerformanceSession performanceSession, String sectionName, Integer seatPrice, Integer remainingSeats) {
        this.performanceSession = performanceSession;
        this.sectionName = sectionName;
        this.seatPrice = seatPrice;
        this.remainingSeats = remainingSeats;
    }

    public void update(String sectionName, Integer seatPrice) {
        this.sectionName = sectionName;
        this.seatPrice = seatPrice;
    }

    public void decreaseRemainingSeats() {
        if(this.remainingSeats <= 0) {
            throw new IllegalStateException("남은 좌석 수는 0보다 작을 수 없습니다.");
        }
        this.remainingSeats--;
    }

    public void increaseRemainingSeats() {
        this.remainingSeats++;
    }
}
