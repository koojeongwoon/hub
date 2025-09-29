package com.tinyquest.hub.event.api.document;

import com.tinyquest.hub.event.api.dto.request.EventCreateRequest;
import com.tinyquest.hub.event.api.dto.request.EventParticipationRequest;
import com.tinyquest.hub.event.api.dto.request.EventPublishRequest;
import com.tinyquest.hub.event.api.dto.request.EventWinnerRequest;
import com.tinyquest.hub.event.api.dto.response.EventEntryResponse;
import com.tinyquest.hub.event.api.dto.response.EventResponse;
import com.tinyquest.hub.event.api.dto.response.EventWinnerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "5-0. Event API", description = "행사 관리 및 당첨 처리")
public interface EventRestControllerDoc {

    @Operation(
            summary = "행사 생성",
            description = "행사 기본 정보를 등록합니다.",
            requestBody = @RequestBody(required = true, description = "행사 생성 요청", content = @Content(schema = @Schema(implementation = EventCreateRequest.class))),
            responses = @ApiResponse(responseCode = "201", description = "생성 성공", content = @Content(schema = @Schema(implementation = EventResponse.class)))
    )
    ResponseEntity<EventResponse> create(@Valid EventCreateRequest request);

    @Operation(
            summary = "행사 공개",
            description = "특정 행사를 공개 상태로 전환하고 알림을 발송합니다.",
            parameters = @Parameter(name = "eventId", in = ParameterIn.PATH, description = "행사 ID", required = true, schema = @Schema(type = "integer", format = "int64")),
            requestBody = @RequestBody(required = true, description = "행사 공개 요청", content = @Content(schema = @Schema(implementation = EventPublishRequest.class))),
            responses = @ApiResponse(responseCode = "200", description = "공개 성공", content = @Content(schema = @Schema(implementation = EventResponse.class)))
    )
    EventResponse publish(Long eventId, @Valid EventPublishRequest request);

    @Operation(
            summary = "행사 참여 등록",
            description = "사용자의 행사 참여를 기록합니다.",
            parameters = @Parameter(name = "eventId", in = ParameterIn.PATH, description = "행사 ID", required = true, schema = @Schema(type = "integer", format = "int64")),
            requestBody = @RequestBody(required = true, description = "참여 요청", content = @Content(schema = @Schema(implementation = EventParticipationRequest.class))),
            responses = @ApiResponse(responseCode = "200", description = "참여 완료", content = @Content(schema = @Schema(implementation = EventEntryResponse.class)))
    )
    EventEntryResponse participate(Long eventId, @Valid EventParticipationRequest request);

    @Operation(
            summary = "행사 당첨자 등록",
            description = "추첨 결과를 저장하고 알림을 발송합니다.",
            parameters = @Parameter(name = "eventId", in = ParameterIn.PATH, description = "행사 ID", required = true, schema = @Schema(type = "integer", format = "int64")),
            requestBody = @RequestBody(required = true, description = "당첨자 등록 요청", content = @Content(schema = @Schema(implementation = EventWinnerRequest.class))),
            responses = @ApiResponse(responseCode = "200", description = "등록 완료", content = @Content(schema = @Schema(implementation = EventWinnerResponse.class)))
    )
    EventWinnerResponse selectWinner(Long eventId, @Valid EventWinnerRequest request);
}
