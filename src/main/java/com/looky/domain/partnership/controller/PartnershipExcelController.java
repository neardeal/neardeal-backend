package com.looky.domain.partnership.controller;

import com.looky.common.response.CommonResponse;
import com.looky.common.response.SwaggerErrorResponse;
import com.looky.domain.partnership.service.PartnershipExcelService;
import com.looky.security.details.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Partnership Excel", description = "제휴 엑셀 관리 API")
@RestController
@RequestMapping("/api/partnerships")
@RequiredArgsConstructor
public class PartnershipExcelController {

    private final PartnershipExcelService partnershipExcelService;

    @Operation(summary = "[관리자] 제휴 등록 템플릿 다운로드", description = "특정 대학의 상점 리스트가 포함된 엑셀 템플릿을 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class)))
    })
    @GetMapping("/template")
    public ResponseEntity<byte[]> exportPartnershipTemplate(
            @Parameter(description = "대상 대학 ID") @RequestParam Long universityId,
            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws IOException {
        byte[] excelContent = partnershipExcelService.exportPartnershipTemplate(universityId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "partnership_template.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelContent);
    }

    @Operation(summary = "[학생회/관리자] 제휴 엑셀 업로드", description = "엑셀 파일을 업로드하여 제휴 정보를 일괄 등록/수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "데이터 검증 실패 (에러 메시지 포함)", content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = SwaggerErrorResponse.class)))
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<Void>> uploadPartnershipData(
            @Parameter(description = "엑셀 파일") @RequestPart("file") MultipartFile file,
            @Parameter(description = "대상 조직 ID (관리자용)") @RequestParam(required = false) Long organizationId,
            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        partnershipExcelService.importPartnershipData(file, principalDetails.getUser(), organizationId);
        return ResponseEntity.ok(CommonResponse.success(null));
    }
}
