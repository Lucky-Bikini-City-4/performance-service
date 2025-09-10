package com.dayaeyak.performance.domain.performance.entity;

import com.dayaeyak.performance.common.entity.BaseEntity;
import com.dayaeyak.performance.domain.cast.entity.Cast;
import com.dayaeyak.performance.domain.hall.entity.Hall;
import com.dayaeyak.performance.domain.hall.enums.MinimumAge;
import com.dayaeyak.performance.domain.performance.enums.Type;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
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
    private MinimumAge minimumAge;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(nullable = false)
    private Timestamp ticketOpen;

    @Column(nullable = false)
    private Timestamp ticketClose;

    @Column(nullable = false)
    private Boolean isActivated;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "performance_cast",
            joinColumns = @JoinColumn(name = "performance_id"),
            inverseJoinColumns = @JoinColumn(name = "cast_id")
    )
    private List<Cast> castList = new ArrayList<>();
}
