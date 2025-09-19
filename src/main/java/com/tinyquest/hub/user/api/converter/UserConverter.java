package com.tinyquest.hub.user.api.converter;

import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;
import com.tinyquest.hub.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter {
    UserDetailResponse toResponse(User user);

    UserDetailResponse toDetailResponse(User user);
}
