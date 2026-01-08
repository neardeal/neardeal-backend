package com.neardeal.domain.organization.service;

import com.neardeal.common.exception.CustomException;
import com.neardeal.common.exception.ErrorCode;
import com.neardeal.domain.organization.dto.OrganizationResponse;
import com.neardeal.domain.organization.dto.CreateOrganizationRequest;
import com.neardeal.domain.organization.dto.UpdateOrganizationRequest;
import com.neardeal.domain.organization.entity.Organization;
import com.neardeal.domain.organization.entity.University;
import com.neardeal.domain.organization.entity.UserOrganization;
import com.neardeal.domain.organization.repository.OrganizationRepository;
import com.neardeal.domain.organization.repository.UniversityRepository;
import com.neardeal.domain.organization.repository.UserOrganizationRepository;
import com.neardeal.domain.user.entity.User;
import com.neardeal.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationService {

        private final OrganizationRepository organizationRepository;
        private final UniversityRepository universityRepository;
        private final UserOrganizationRepository userOrganizationRepository;
        private final UserRepository userRepository;

        // --- 공통 ---
        public List<OrganizationResponse> getOrganizations(Long universityId) {
                University university = universityRepository.findById(universityId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "대학을 찾을 수 없습니다."));

                return organizationRepository.findAll().stream()
                                .filter(a -> a.getUniversity().getId().equals(universityId))
                                .map(OrganizationResponse::from)
                                .collect(Collectors.toList());
        }

        // --- 관리자 ---

        @Transactional
        public Long createOrganization(Long universityId, CreateOrganizationRequest request) {
                University university = universityRepository.findById(universityId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "대학을 찾을 수 없습니다."));

                Organization organization = request.toEntity(university);
                Organization savedOrganization = organizationRepository.save(organization);
                return savedOrganization.getId();
        }

        @Transactional
        public void updateOrganization(Long organizationId, UpdateOrganizationRequest request) {
                Organization organization = organizationRepository.findById(organizationId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "소속을 찾을 수 없습니다."));

                organization.update(request.getCategory(), request.getName(), request.getExpiresAt());
        }

        @Transactional
        public void deleteOrganization(Long organizationId) {
                Organization organization = organizationRepository.findById(organizationId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "소속을 찾을 수 없습니다."));

                organizationRepository.delete(organization);
        }

        // --- 학생 ---

        @Transactional
        public void joinOrganization(Long organizationId, User user) {
                Organization organization = organizationRepository.findById(organizationId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "소속을 찾을 수 없습니다."));

                User currentUser = userRepository.findById(user.getId())
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                if (userOrganizationRepository.existsByUserAndOrganization(currentUser, organization)) {
                        throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 가입된 소속입니다.");
                }

                UserOrganization userOrganization = UserOrganization.builder()
                                .user(currentUser)
                                .organization(organization)
                                .build();

                userOrganizationRepository.save(userOrganization);
        }

        @Transactional
        public void leaveOrganization(Long organizationId, User user) {
                Organization organization = organizationRepository.findById(organizationId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "소속을 찾을 수 없습니다."));

                User currentUser = userRepository.findById(user.getId())
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                UserOrganization userOrganization = userOrganizationRepository
                                .findByUserAndOrganization(currentUser, organization)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "가입되지 않은 소속입니다."));

                userOrganizationRepository.delete(userOrganization);
        }
}
