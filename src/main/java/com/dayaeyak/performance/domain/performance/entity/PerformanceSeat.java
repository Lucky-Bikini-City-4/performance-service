package com.dayaeyak.performance.domain.performance.entity;

import com.dayaeyak.performance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "performance_seats")
public class PerformanceSeat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long performanceSeatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_section_id", nullable = false)
    private PerformanceSection performanceSection;

    @Column(nullable = false)
    private Integer seatNumber;

    @Column(nullable = false)
    private Boolean isSoldOut;

    public PerformanceSeat(PerformanceSection performanceSection, Integer seatNumber) {
        this.performanceSection = performanceSection;
        this.seatNumber = seatNumber;
        this.isSoldOut = false;
    }

    public void sellOut() {
        this.isSoldOut = true;
    }

    public void reopen(){
        this.isSoldOut = false;
    }


}
