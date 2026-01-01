package com.neardeal.domain.affiliation.dto;

import com.neardeal.domain.affiliation.entity.University;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UniversityResponse {
    private Long id;
    private String name;
    private String emailDomain;

    public static UniversityResponse from(University university) {
        return UniversityResponse.builder()
                .id(university.getId())
                .name(university.getName())
                .emailDomain(university.getEmailDomain())
                .build();
    }
}
