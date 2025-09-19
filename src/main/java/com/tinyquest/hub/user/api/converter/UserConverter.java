package com.tinyquest.hub.user.api.converter;

import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;
import com.tinyquest.hub.user.api.dto.response.UserResponse;
import com.tinyquest.hub.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter {
    UserResponse toResponse(User user);

    List<UserResponse> toResponse(List<User> users);

    UserDetailResponse toDetailResponse(User user);
}
