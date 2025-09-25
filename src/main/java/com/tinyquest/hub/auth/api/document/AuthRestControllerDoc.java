package com.tinyquest.hub.auth.api.document;

import com.tinyquest.hub.auth.api.dto.request.LoginRequest;
import com.tinyquest.hub.auth.api.dto.request.LogoutAllRequest;
import com.tinyquest.hub.auth.api.dto.request.LogoutRequest;
import com.tinyquest.hub.auth.api.dto.request.RefreshRequest;
import com.tinyquest.hub.auth.api.dto.request.RevokeAccessRequest;
import com.tinyquest.hub.auth.api.dto.response.TokenResponse;
import com.tinyquest.hub.shared.response.Response;
import com.tinyquest.hub.auth.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "0-0. Auth API", description = "인증/인가")
public interface AuthRestControllerDoc {

    @Operation(
            summary = "로그인",
            description = "자격 증명을 이용해 액세스/리프레시 토큰을 발급합니다.",
            requestBody = @RequestBody(required = true, description = "로그인 요청", content = @Content(schema = @Schema(implementation = LoginRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = TokenResponse.class)))
            }
    )
    TokenResponse login(@Valid LoginRequest req, HttpServletRequest request);

    @Operation(
            summary = "토큰 갱신",
            description = "유효한 리프레시 토큰으로 토큰을 재발급합니다.",
            requestBody = @RequestBody(required = true, description = "갱신 요청", content = @Content(schema = @Schema(implementation = RefreshRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "갱신 성공", content = @Content(schema = @Schema(implementation = TokenResponse.class)))
            }
    )
    TokenResponse refresh(@Valid RefreshRequest request, HttpServletRequest httpRequest);

    @Operation(
            summary = "현재 세션 로그아웃",
            description = "현재 세션을 로그아웃 처리합니다.",
            requestBody = @RequestBody(required = true, description = "로그아웃 요청", content = @Content(schema = @Schema(implementation = LogoutRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = Response.Success.class)))
            }
    )
    Response<Void> logout(AuthPrincipal principal, @Valid LogoutRequest request);

    @Operation(
            summary = "전체 세션 로그아웃",
            description = "사용자의 모든 활성 세션을 로그아웃 처리합니다.",
            requestBody = @RequestBody(required = false, description = "전체 로그아웃 요청", content = @Content(schema = @Schema(implementation = LogoutAllRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "전체 로그아웃 성공", content = @Content(schema = @Schema(implementation = Response.Success.class)))
            }
    )
    Response<Void> logoutAll(AuthPrincipal principal, LogoutAllRequest request);

    @Operation(
            summary = "액세스 토큰 철회",
            description = "특정 액세스 토큰 JTI를 기반으로 토큰을 철회합니다.",
            requestBody = @RequestBody(required = true, description = "토큰 철회 요청", content = @Content(schema = @Schema(implementation = RevokeAccessRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "철회 성공", content = @Content(schema = @Schema(implementation = Response.Success.class)))
            }
    )
    Response<Void> revokeAccessToken(AuthPrincipal principal, @Valid RevokeAccessRequest request);
}
