package com.dayaeyak.performance.domain.hall.entity;

import com.dayaeyak.performance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hall_sections")
public class HallSection extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long hallSectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(length = 50, nullable = false)
    private String sectionName;

    @Column(nullable = false)
    private Integer seats;

    @Builder
    public HallSection(Hall hall, String sectionName, Integer seats) {
        this.hall = hall;
        this.sectionName = sectionName;
        this.seats = seats;
    }

    public void update(String sectionName, Integer seats) {
        this.sectionName = sectionName;
        this.seats = seats;
    }
}
