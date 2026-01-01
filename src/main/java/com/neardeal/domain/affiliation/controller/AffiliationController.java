package com.neardeal.domain.affiliation.controller;

import com.neardeal.common.response.CommonResponse;
import com.neardeal.domain.affiliation.dto.AffiliationResponse;
import com.neardeal.domain.affiliation.dto.CreateAffiliationRequest;
import com.neardeal.domain.affiliation.dto.UpdateAffiliationRequest;
import com.neardeal.domain.affiliation.service.AffiliationService;
import com.neardeal.security.details.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Affiliation", description = "대학 소속 관리 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AffiliationController {

    private final AffiliationService affiliationService;

    // --- 공통 ---

    @Operation(summary = "[공통] 특정 대학의 소속 목록 조회", description = "대학의 모든 소속을 조회합니다.")
    @GetMapping("/universities/{universityId}/affiliations")
    public ResponseEntity<CommonResponse<List<AffiliationResponse>>> getAffiliations(
            @PathVariable Long universityId) {
        List<AffiliationResponse> responses = affiliationService.getAffiliations(universityId);
        return ResponseEntity.ok(CommonResponse.success(responses));
    }

    // --- 관리자 ---

    @Operation(summary = "[관리자] 특정 대학에 소속 등록", description = "대학에 새로운 소속(단과대, 학과 등)을 등록합니다.")
    @PostMapping("/universities/{universityId}/affiliations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<Long>> createAffiliation(
            @PathVariable Long universityId,
            @RequestBody @Valid CreateAffiliationRequest request) {
        Long affiliationId = affiliationService.createAffiliation(universityId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(affiliationId));
    }

    @Operation(summary = "[관리자] 소속 수정", description = "소속 정보를 수정합니다.")
    @PatchMapping("/affiliations/{affiliationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<Void>> updateAffiliation(
            @PathVariable Long affiliationId,
            @RequestBody @Valid UpdateAffiliationRequest request) {
        affiliationService.updateAffiliation(affiliationId, request);
        return ResponseEntity.ok(CommonResponse.success(null));
    }

    @Operation(summary = "[관리자] 소속 삭제", description = "소속을 삭제합니다.")
    @DeleteMapping("/affiliations/{affiliationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<Void>> deleteAffiliation(@PathVariable Long affiliationId) {
        affiliationService.deleteAffiliation(affiliationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(CommonResponse.success(null));
    }

    // --- 학생 ---

    @Operation(summary = "[학생] 소속 가입", description = "학생이 특정 소속에 가입합니다.")
    @PostMapping("/affiliations/{affiliationId}/membership")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CommonResponse<Void>> joinAffiliation(
            @PathVariable Long affiliationId,
            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails) {
        affiliationService.joinAffiliation(affiliationId, principalDetails.getUser());
        return ResponseEntity.ok(CommonResponse.success(null));
    }

    @Operation(summary = "[학생] 소속 탈퇴", description = "학생이 특정 소속에서 탈퇴합니다.")
    @DeleteMapping("/affiliations/{affiliationId}/membership")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CommonResponse<Void>> leaveAffiliation(
            @PathVariable Long affiliationId,
            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails) {
        affiliationService.leaveAffiliation(affiliationId, principalDetails.getUser());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(CommonResponse.success(null));
    }
}
