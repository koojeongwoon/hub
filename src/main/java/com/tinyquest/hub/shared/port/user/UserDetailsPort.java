package com.tinyquest.hub.shared.port.user;

import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;

import java.util.Optional;

public interface UserDetailsPort {

    Optional<UserDTO> findByEmail(String email);

}
