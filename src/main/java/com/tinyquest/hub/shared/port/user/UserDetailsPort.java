package com.tinyquest.hub.shared.port.user;

import java.util.Optional;

public interface UserDetailsPort {

    Optional<UserDTO> findByEmail(String email);

}
