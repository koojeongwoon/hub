package com.tinyquest.hub.notification.api.document;

import com.tinyquest.hub.notification.api.dto.response.NotificationLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "5-2. Notification API", description = "알림 로그 조회")
public interface NotificationRestControllerDoc {

    @Operation(
            summary = "알림 로그 최신 조회",
            description = "최근 생성된 알림 로그를 반환합니다.",
            parameters = @Parameter(name = "size", in = ParameterIn.QUERY, description = "조회할 최대 건수 (1~100)", schema = @Schema(type = "integer", format = "int32", defaultValue = "20")),
            responses = @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationLogResponse.class))))
    )
    List<NotificationLogResponse> latest(int size);
}
