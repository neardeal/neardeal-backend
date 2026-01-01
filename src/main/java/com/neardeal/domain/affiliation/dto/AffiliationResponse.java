package com.neardeal.domain.affiliation.dto;

import com.neardeal.domain.affiliation.entity.Affiliation;
import com.neardeal.domain.affiliation.entity.AffiliationCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AffiliationResponse {
    private Long id;
    private Long universityId;
    private String universityName;
    private AffiliationCategory category;
    private String name;
    private LocalDateTime expiresAt;

    public static AffiliationResponse from(Affiliation affiliation) {
        return AffiliationResponse.builder()
                .id(affiliation.getId())
                .universityId(affiliation.getUniversity().getId())
                .universityName(affiliation.getUniversity().getName())
                .category(affiliation.getCategory())
                .name(affiliation.getName())
                .expiresAt(affiliation.getExpiresAt())
                .build();
    }
}
