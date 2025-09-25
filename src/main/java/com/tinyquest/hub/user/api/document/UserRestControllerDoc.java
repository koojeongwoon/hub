package com.tinyquest.hub.user.api.document;

import com.tinyquest.hub.shared.response.Response;
import com.tinyquest.hub.user.api.dto.request.UserCreateRequest;
import com.tinyquest.hub.user.api.dto.request.UserSearchRequest;
import com.tinyquest.hub.user.api.dto.request.UserUpdateRequest;
import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "1-0. User API", description = "사용자 관리")
public interface UserRestControllerDoc {

    @Operation(
            summary = "내 프로필 조회",
            description = "현재 인증된 사용자의 상세 정보를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = UserDetailResponse.class)))
            }
    )
    UserDetailResponse get();

    @Operation(
            summary = "사용자 상세 조회",
            description = "사용자 번호로 사용자 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "사용자 ID", required = true, schema = @Schema(type = "integer", format = "int64"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = UserDetailResponse.class)))
            }
    )
    UserDetailResponse getById(Long id);

    @Operation(
            summary = "사용자 목록 검색",
            description = "검색 조건과 페이지 정보를 이용해 사용자 목록을 조회합니다.",
            parameters = {
                    @Parameter(name = "q", in = ParameterIn.QUERY, description = "검색 키워드", schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Page.class)))
            }
    )
    Page<UserDetailResponse> search(@Parameter(hidden = true) @Valid UserSearchRequest req,
                                    @Parameter(hidden = true) Pageable pageable);

    @Operation(
            summary = "사용자 등록",
            description = "신규 사용자를 등록합니다.",
            requestBody = @RequestBody(required = true, description = "사용자 생성 요청", content = @Content(schema = @Schema(implementation = UserCreateRequest.class))),
            responses = {
                @ApiResponse(responseCode = "200", description = "생성 성공", content = @Content(schema = @Schema(implementation = Response.Success.class)))
            }
    )
    Response<Void> create(@Valid UserCreateRequest req);

    @Operation(
            summary = "사용자 정보 수정",
            description = "현재 인증된 사용자의 정보를 수정합니다.",
            requestBody = @RequestBody(required = true, description = "사용자 수정 요청", content = @Content(schema = @Schema(implementation = UserUpdateRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(schema = @Schema(implementation = Response.Success.class)))
            }
    )
    Response<Void> update(@Valid UserUpdateRequest req);

    @Operation(
            summary = "사용자 삭제",
            description = "현재 인증된 사용자를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(schema = @Schema(implementation = Response.Success.class)))
            }
    )
    Response<Void> delete();
}
