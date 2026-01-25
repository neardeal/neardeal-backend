package com.looky.domain.store.controller;

import com.looky.common.response.CommonResponse;
import com.looky.domain.store.dto.BizVerificationRequest;
import com.looky.domain.store.dto.BizVerificationResponse;
import com.looky.domain.store.service.StoreVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Store", description = "상점 심사 API")
@RestController
@RequestMapping("/api/stores/audit")
@RequiredArgsConstructor
public class StoreVerificationController {

    private final StoreVerificationService storeVerificationService;

    @Operation(summary = "[점주] 사업자등록번호 진위 확인", description = "사업자등록번호의 유효성을 검증합니다.")
    @PostMapping("/biz-reg-no")
    public ResponseEntity<CommonResponse<BizVerificationResponse>> verifyBizRegNo(@RequestBody BizVerificationRequest request) {
        BizVerificationResponse response = storeVerificationService.verifyBizRegNo(request);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
