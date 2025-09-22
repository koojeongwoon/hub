package com.tinyquest.hub.user.service;

import com.tinyquest.hub.shared.port.user.UserDTO;
import com.tinyquest.hub.shared.port.user.UserDetailsPort;
import com.tinyquest.hub.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsPortImpl implements UserDetailsPort {
    private final UserRepository repo; // JPA/MyBatis 등 내부 리포지토리

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return repo.findByEmail(email)
                .map(u -> new UserDTO(u.getId(), u.getEmail(), u.getPassword(), u.isEnabled()));
    }
}