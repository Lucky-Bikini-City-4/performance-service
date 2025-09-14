package com.dayaeyak.performance.domain.performance.entity;

import com.dayaeyak.performance.common.entity.BaseEntity;
import com.dayaeyak.performance.domain.cast.entity.Cast;
import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.performance.enums.Grade;
import com.dayaeyak.performance.domain.performance.enums.Type;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "performances")
public class Performance extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long performanceId;

    @Column(nullable = false)
    private Long sellerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(length = 100, nullable = false)
    private String performanceName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private Grade grade;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate endDate;

    @Column(nullable = false)
    private Timestamp ticketOpenAt;

    @Column(nullable = false)
    private Timestamp ticketCloseAt;

    @Column(nullable = false)
    private Boolean isActivated;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "performance_cast",
            joinColumns = @JoinColumn(name = "performance_id"),
            inverseJoinColumns = @JoinColumn(name = "cast_id")
    )
    private List<Cast> castList = new ArrayList<>();

    public void delete() {
        this.deletedAt = LocalDateTime.now();

        // 각 출연진의 공연 목록에서 공연(자기 자신) 삭제
        for (Cast cast : new ArrayList<>(castList)) {
            cast.getPerformanceList().remove(this);
        }
        // 출연진 리스트를 비워서 혼란 방지
        castList.clear();
    }

    @Builder
    public Performance(Long sellerId, Hall hall, String performanceName, String description, Type type, Grade grade,
                       LocalDate startDate, LocalDate endDate, Timestamp ticketOpenAt, Timestamp ticketCloseAt, Boolean isActivated) {
        this.sellerId = sellerId;
        this.hall = hall;
        this.performanceName = performanceName;
        this.description = description;
        this.type = type;
        this.grade = grade;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ticketOpenAt = ticketOpenAt;
        this.ticketCloseAt = ticketCloseAt;
        this.isActivated = isActivated;
    }

    public void updatePerformanceName(String name) {
        this.performanceName = name;
    }

    public void changeHall(Hall hall) {
        this.hall = hall;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateType(Type type) {
        this.type = type;
    }

    public void updateGrade(Grade grade) {
        this.grade = grade;
    }

    public void updateStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void updateEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void updateTicketOpenAt(Timestamp ticketOpenAt) {
        this.ticketOpenAt = ticketOpenAt;
    }

    public void updateTicketCloseAt(Timestamp ticketCloseAt) {
        this.ticketCloseAt = ticketCloseAt;
    }

    public void updateActivation(Boolean activation) {
        this.isActivated = activation;
    }
}
