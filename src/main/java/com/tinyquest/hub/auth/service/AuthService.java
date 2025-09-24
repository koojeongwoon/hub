package com.tinyquest.hub.auth.service;

import com.tinyquest.hub.auth.security.JwtProvider;
import com.tinyquest.hub.shared.constants.ErrorCode;
import com.tinyquest.hub.shared.error.BusinessException;
import com.tinyquest.hub.shared.port.user.UserDetailsPort;
import com.tinyquest.hub.auth.api.dto.request.LoginRequest;
import com.tinyquest.hub.auth.api.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsPort userDetailsPort;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public TokenResponse login(LoginRequest req) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        req.email(),
//                        req.password()
//                )
//        );
//
//        String token = jwtProvider.generateToken(authentication);
        // return new TokenResponse(token);

        var u = userDetailsPort.findByEmail(req.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_4001));

        if (!u.enabled() || !passwordEncoder.matches(req.password(), u.passwordHash())) {
            throw new BusinessException(ErrorCode.USER_VALIDATION_1001);
        }

        return new TokenResponse(jwtProvider.generateToken(u.id(), u.username())); // accessToken 발급
    }
}
