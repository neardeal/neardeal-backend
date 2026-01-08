package com.neardeal.domain.organization.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrganizationCategory {
    COLLEGE, // 단과대학
    DEPARTMENT, // 학과
    CLUB, // 동아리
    STUDENT_COUNCIL, // 학생회
    ETC // 기타
}
