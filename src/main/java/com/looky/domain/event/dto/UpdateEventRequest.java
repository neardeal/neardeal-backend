package com.looky.domain.event.dto;

import com.looky.domain.event.entity.EventStatus;
import com.looky.domain.event.entity.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "이벤트 수정 요청")
public class UpdateEventRequest {

    @Schema(description = "이벤트 제목", example = "2026 봄 플리마켓 (수정)")
    private String title;

    @Schema(description = "이벤트 설명")
    private String description;

    @Schema(description = "이벤트 타입 목록")
    private List<EventType> eventTypes;

    @Schema(description = "위도")
    private Double latitude;

    @Schema(description = "경도")
    private Double longitude;

    @Schema(description = "이벤트 시작일시")
    private LocalDateTime startDateTime;

    @Schema(description = "이벤트 종료일시")
    private LocalDateTime endDateTime;

    @Schema(description = "이벤트 상태", example = "LIVE")
    private EventStatus status;
}
