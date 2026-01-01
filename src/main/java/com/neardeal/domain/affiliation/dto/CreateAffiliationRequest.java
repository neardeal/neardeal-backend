package com.neardeal.domain.affiliation.dto;

import com.neardeal.domain.affiliation.entity.Affiliation;
import com.neardeal.domain.affiliation.entity.AffiliationCategory;
import com.neardeal.domain.affiliation.entity.University;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateAffiliationRequest {

    @NotNull(message = "카테고리는 필수입니다.")
    private AffiliationCategory category;

    @NotBlank(message = "소속 이름은 필수입니다.")
    private String name;

    private LocalDateTime expiresAt;

    public Affiliation toEntity(University university) {
        return Affiliation.builder()
                .university(university)
                .category(this.category)
                .name(this.name)
                .expiresAt(this.expiresAt)
                .build();
    }
}
