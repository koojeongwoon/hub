package com.tinyquest.hub.auth.api.document;

import com.tinyquest.hub.auth.api.dto.response.UserResourceAccessResponse;
import com.tinyquest.hub.auth.security.AuthPrincipal;
import com.tinyquest.hub.shared.permission.ResourceType;
import com.tinyquest.hub.shared.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Set;

@Tag(name = "0-1. Auth Metadata API", description = "권한/리소스 메타데이터")
public interface MetadataRestControllerDoc {

    @Operation(
            summary = "로그인한 사용자의 리소스 권한 조회",
            description = "사용자가 접근 가능한 메뉴/페이지/기능/요소를 반환합니다.",
            parameters = {
                    @Parameter(name = "scopes", in = ParameterIn.QUERY, description = "필터링할 리소스 타입",
                            array = @ArraySchema(schema = @Schema(implementation = ResourceType.class)),
                            required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "권한 조회 성공",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    schema = @Schema(implementation = UserResourceAccessResponse.class)
                            ))
            }
    )
    Response<UserResourceAccessResponse> getMyResources(AuthPrincipal principal, Set<ResourceType> scopes);
}
