package com.tinyquest.hub.event.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EventCreateRequest(
        @NotBlank(message = "행사 이름은 필수입니다.")
        String name,
        @NotBlank(message = "행사 유형은 필수입니다.")
        String type
) {}
