package com.tinyquest.hub.promotion.api.document;

import com.tinyquest.hub.promotion.api.dto.request.PromotionActivateRequest;
import com.tinyquest.hub.promotion.api.dto.request.PromotionBenefitRequest;
import com.tinyquest.hub.promotion.api.dto.request.PromotionCouponIssueRequest;
import com.tinyquest.hub.promotion.api.dto.request.PromotionCreateRequest;
import com.tinyquest.hub.promotion.api.dto.response.PromotionBenefitResponse;
import com.tinyquest.hub.promotion.api.dto.response.PromotionCouponResponse;
import com.tinyquest.hub.promotion.api.dto.response.PromotionResponse;
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

@Tag(name = "5-1. Promotion API", description = "프로모션 생성 및 혜택 처리")
public interface PromotionRestControllerDoc {

    @Operation(
            summary = "프로모션 생성",
            description = "새로운 프로모션을 등록합니다.",
            requestBody = @RequestBody(required = true, description = "프로모션 생성 요청", content = @Content(schema = @Schema(implementation = PromotionCreateRequest.class))),
            responses = @ApiResponse(responseCode = "201", description = "생성 성공", content = @Content(schema = @Schema(implementation = PromotionResponse.class)))
    )
    ResponseEntity<PromotionResponse> create(@Valid PromotionCreateRequest request);

    @Operation(
            summary = "프로모션 활성화",
            description = "지정된 프로모션을 활성화하고 알림을 발송합니다.",
            parameters = @Parameter(name = "promotionId", in = ParameterIn.PATH, required = true, description = "프로모션 ID", schema = @Schema(type = "integer", format = "int64")),
            requestBody = @RequestBody(required = true, description = "활성화 요청", content = @Content(schema = @Schema(implementation = PromotionActivateRequest.class))),
            responses = @ApiResponse(responseCode = "200", description = "활성화 성공", content = @Content(schema = @Schema(implementation = PromotionResponse.class)))
    )
    PromotionResponse activate(Long promotionId, @Valid PromotionActivateRequest request);

    @Operation(
            summary = "쿠폰 발급",
            description = "프로모션에 속한 쿠폰을 발급합니다.",
            parameters = @Parameter(name = "promotionId", in = ParameterIn.PATH, required = true, description = "프로모션 ID", schema = @Schema(type = "integer", format = "int64")),
            requestBody = @RequestBody(required = true, description = "쿠폰 발급 요청", content = @Content(schema = @Schema(implementation = PromotionCouponIssueRequest.class))),
            responses = @ApiResponse(responseCode = "200", description = "발급 완료", content = @Content(schema = @Schema(implementation = PromotionCouponResponse.class)))
    )
    PromotionCouponResponse issueCoupon(Long promotionId, @Valid PromotionCouponIssueRequest request);

    @Operation(
            summary = "혜택 적용",
            description = "주문에 대한 혜택 적용 내역을 기록합니다.",
            parameters = @Parameter(name = "promotionId", in = ParameterIn.PATH, required = true, description = "프로모션 ID", schema = @Schema(type = "integer", format = "int64")),
            requestBody = @RequestBody(required = true, description = "혜택 적용 요청", content = @Content(schema = @Schema(implementation = PromotionBenefitRequest.class))),
            responses = @ApiResponse(responseCode = "200", description = "적용 완료", content = @Content(schema = @Schema(implementation = PromotionBenefitResponse.class)))
    )
    PromotionBenefitResponse applyBenefit(Long promotionId, @Valid PromotionBenefitRequest request);
}
