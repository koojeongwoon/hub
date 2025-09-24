package com.tinyquest.hub.user.document;

import com.tinyquest.hub.shared.response.ApiResponse;
import com.tinyquest.hub.shared.response.PageResponse;
import com.tinyquest.hub.user.api.dto.request.UserCreateRequest;
import com.tinyquest.hub.user.api.dto.request.UserSearchRequest;
import com.tinyquest.hub.user.api.dto.request.UserUpdateRequest;
import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "1-0. User API",
        description = "사용자 데이터 컨트롤러"
)
public interface UserRestControllerDoc {

    @Operation(
            summary = "사용자 상세 조회",
            description = """
                사용자 단건 조회
            """,
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "사용자번호", example = "1")
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "정상 조회",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.Success.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                       "code": null,
                                                       "message": "성공적으로 처리되었습니다.",
                                                       "data": {
                                                       },
                                                       "timestamp": "2025-04-30T15:02:38.500157+09:00"
                                                     }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.Failure.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "code": "INTERNAL_ERROR",
                                                      "message": "서버 내부 오류가 발생했습니다.",
                                                      "data": null,
                                                      "timestamp": "2025-04-30T15:00:51.092146+09:00"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    UserDetailResponse get(Long id);

    @Operation(
            summary = "사용자 목록 조회",
            description = """
                사용자 다건 조회
            """,
            parameters = {
                    @Parameter(name = "q", in = ParameterIn.QUERY, description = "검색어", example = "1"),
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지", example = "0"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "사이즈", example = "20"),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "정렬조건", example = ""),

            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "정상 조회",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.Success.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                       "code": null,
                                                       "message": "성공적으로 처리되었습니다.",
                                                       "data": [],
                                                       "timestamp": "2025-04-30T15:02:38.500157+09:00"
                                                     }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.Failure.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "code": "INTERNAL_ERROR",
                                                      "message": "서버 내부 오류가 발생했습니다.",
                                                      "data": null,
                                                      "timestamp": "2025-04-30T15:00:51.092146+09:00"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    PageResponse<UserDetailResponse> search(UserSearchRequest req, Pageable pageable);

    @Operation(
            summary = "사용자 목록 조회",
            description = """
                사용자 다건 조회
            """,
            parameters = {
                    @Parameter(name = "q", in = ParameterIn.QUERY, description = "검색어", example = "1"),
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지", example = "0"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "사이즈", example = "20"),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "정렬조건", example = ""),

            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "정상 조회",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.Success.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                       "code": null,
                                                       "message": "성공적으로 처리되었습니다.",
                                                       "data": [],
                                                       "timestamp": "2025-04-30T15:02:38.500157+09:00"
                                                     }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.Failure.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "code": "INTERNAL_ERROR",
                                                      "message": "서버 내부 오류가 발생했습니다.",
                                                      "data": null,
                                                      "timestamp": "2025-04-30T15:00:51.092146+09:00"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ApiResponse<Void> create(UserCreateRequest req);

    @Operation(
            summary = "사용자 목록 조회",
            description = """
                사용자 다건 조회
            """,
            parameters = {
                    @Parameter(name = "q", in = ParameterIn.QUERY, description = "검색어", example = "1"),
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지", example = "0"),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "사이즈", example = "20"),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "정렬조건", example = ""),

            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "정상 조회",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.Success.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                       "code": null,
                                                       "message": "성공적으로 처리되었습니다.",
                                                       "data": [],
                                                       "timestamp": "2025-04-30T15:02:38.500157+09:00"
                                                     }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.Failure.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "code": "INTERNAL_ERROR",
                                                      "message": "서버 내부 오류가 발생했습니다.",
                                                      "data": null,
                                                      "timestamp": "2025-04-30T15:00:51.092146+09:00"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ApiResponse<Void> update(Long id, UserUpdateRequest req);

    @Operation(
            summary = "사용자 목록 조회",
            description = """
                사용자 다건 조회
            """,
            requestBody = @RequestBody(
                    required = true,
                    description = "",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateRequest.class),
                            examples = @ExampleObject(
                                    name="기본 사용자 수정",
                                    value = """
                                            {
                                                
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "정상 조회",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.Success.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                       "code": null,
                                                       "message": "성공적으로 처리되었습니다.",
                                                       "data": null,
                                                       "timestamp": "2025-04-30T15:02:38.500157+09:00"
                                                     }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.Failure.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "code": "INTERNAL_ERROR",
                                                      "message": "서버 내부 오류가 발생했습니다.",
                                                      "data": null,
                                                      "timestamp": "2025-04-30T15:00:51.092146+09:00"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ApiResponse<Void> delete(Long id);
}
