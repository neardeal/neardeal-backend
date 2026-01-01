package com.neardeal.domain.affiliation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.neardeal.common.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Affiliation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "affiliation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AffiliationCategory category; // 단과대학, 학과, 동아리 등

    @Column(nullable = false)
    private String name; // 공과대학, 소프트웨어공학과 등

    private LocalDateTime expiresAt; // 제휴 만료 시점

    @Builder
    public Affiliation(University university, AffiliationCategory category, String name, LocalDateTime expiresAt) {
        this.university = university;
        this.category = category;
        this.name = name;
        this.expiresAt = expiresAt;
    }

    public void update(AffiliationCategory category, String name, LocalDateTime expiresAt) {
        this.category = category;
        this.name = name;
        this.expiresAt = expiresAt;
    }
}
