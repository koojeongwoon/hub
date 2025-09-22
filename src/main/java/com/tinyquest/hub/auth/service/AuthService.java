package com.tinyquest.hub.auth.service;

import com.tinyquest.hub.auth.security.JwtProvider;
import com.tinyquest.hub.shared.port.user.UserDetailsPort;
import com.tinyquest.hub.auth.api.dto.request.LoginRequest;
import com.tinyquest.hub.auth.api.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsPort userDetailsPort;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public TokenResponse login(LoginRequest req) {
        var u = userDetailsPort.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!u.enabled() || !passwordEncoder.matches(req.password(), u.passwordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new TokenResponse(jwtProvider.generateToken(u.id(), u.username())); // accessToken 발급
    }
}
