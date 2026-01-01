package com.neardeal.domain.affiliation.service;

import com.neardeal.common.exception.CustomException;
import com.neardeal.common.exception.ErrorCode;
import com.neardeal.domain.affiliation.dto.AffiliationResponse;
import com.neardeal.domain.affiliation.dto.CreateAffiliationRequest;
import com.neardeal.domain.affiliation.dto.UpdateAffiliationRequest;
import com.neardeal.domain.affiliation.entity.Affiliation;
import com.neardeal.domain.affiliation.entity.University;
import com.neardeal.domain.affiliation.entity.UserAffiliation;
import com.neardeal.domain.affiliation.repository.AffiliationRepository;
import com.neardeal.domain.affiliation.repository.UniversityRepository;
import com.neardeal.domain.affiliation.repository.UserAffiliationRepository;
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
public class AffiliationService {

        private final AffiliationRepository affiliationRepository;
        private final UniversityRepository universityRepository;
        private final UserAffiliationRepository userAffiliationRepository;
        private final UserRepository userRepository;

        // --- 공통 ---
        public List<AffiliationResponse> getAffiliations(Long universityId) {
                University university = universityRepository.findById(universityId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "대학을 찾을 수 없습니다."));

                return affiliationRepository.findAll().stream()
                                .filter(a -> a.getUniversity().getId().equals(universityId))
                                .map(AffiliationResponse::from)
                                .collect(Collectors.toList());
        }

        // --- 관리자 ---

        @Transactional
        public Long createAffiliation(Long universityId, CreateAffiliationRequest request) {
                University university = universityRepository.findById(universityId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "대학을 찾을 수 없습니다."));

                Affiliation affiliation = request.toEntity(university);
                Affiliation savedAffiliation = affiliationRepository.save(affiliation);
                return savedAffiliation.getId();
        }

        @Transactional
        public void updateAffiliation(Long affiliationId, UpdateAffiliationRequest request) {
                Affiliation affiliation = affiliationRepository.findById(affiliationId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "소속을 찾을 수 없습니다."));

                affiliation.update(request.getCategory(), request.getName(), request.getExpiresAt());
        }

        @Transactional
        public void deleteAffiliation(Long affiliationId) {
                Affiliation affiliation = affiliationRepository.findById(affiliationId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "소속을 찾을 수 없습니다."));

                affiliationRepository.delete(affiliation);
        }

        // --- 학생 ---

        @Transactional
        public void joinAffiliation(Long affiliationId, User user) {
                Affiliation affiliation = affiliationRepository.findById(affiliationId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "소속을 찾을 수 없습니다."));

                User currentUser = userRepository.findById(user.getId())
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                if (userAffiliationRepository.existsByUserAndAffiliation(currentUser, affiliation)) {
                        throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 가입된 소속입니다.");
                }

                UserAffiliation userAffiliation = UserAffiliation.builder()
                                .user(currentUser)
                                .affiliation(affiliation)
                                .build();

                userAffiliationRepository.save(userAffiliation);
        }

        @Transactional
        public void leaveAffiliation(Long affiliationId, User user) {
                Affiliation affiliation = affiliationRepository.findById(affiliationId)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "소속을 찾을 수 없습니다."));

                User currentUser = userRepository.findById(user.getId())
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                UserAffiliation userAffiliation = userAffiliationRepository
                                .findByUserAndAffiliation(currentUser, affiliation)
                                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "가입되지 않은 소속입니다."));

                userAffiliationRepository.delete(userAffiliation);
        }
}
