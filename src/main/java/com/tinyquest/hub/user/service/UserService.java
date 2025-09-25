package com.tinyquest.hub.user.service;

import com.tinyquest.hub.shared.constants.ErrorCode;
import com.tinyquest.hub.shared.error.BusinessException;
import com.tinyquest.hub.shared.port.auth.command.UserRoleAssignmentPort;
import com.tinyquest.hub.user.api.converter.UserConverter;
import com.tinyquest.hub.user.api.dto.request.UserCreateRequest;
import com.tinyquest.hub.user.api.dto.request.UserSearchRequest;
import com.tinyquest.hub.user.api.dto.request.UserUpdateRequest;
import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;
import com.tinyquest.hub.user.domain.entity.User;
import com.tinyquest.hub.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    // private final UserJdbcRepository jdbcRepo;
    private final UserRepository repo;
    private final UserConverter converter;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final UserRoleAssignmentPort userRoleAssignmentPort;

    @Transactional(readOnly = true)
    public UserDetailResponse get(Long id) {
        var u = repo.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_4001));
        // var u2 = jdbcRepo.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_4001));
        return converter.toDetailResponse(u);
    }

    @Transactional(readOnly = true)
    public Page<UserDetailResponse> search(UserSearchRequest req, Pageable pageable) {
        Page<User> userPage = repo.search(req.q(), pageable);
        return userPage.map(converter::toResponse);
    }

    @Transactional
    public void create(UserCreateRequest req) {
        if (repo.findByEmail(req.email()).isPresent()) {
            throw new BusinessException(ErrorCode.USER_VALIDATION_1002);
        }
        String encodedPassword = passwordEncoder.encode(req.password());
        User saved = repo.save(User.of(req.email(), encodedPassword, req.name(), req.age(), clock.instant()));
        userRoleAssignmentPort.assignDefaultRoles(saved.getId());
    }

    @Transactional
    public void update(Long id, UserUpdateRequest req) {
        var u = repo.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_4001));
        u.change(req.name(), req.age());
    }

    @Transactional
    public void delete(Long id) {
        userRoleAssignmentPort.updateRoles(id, Set.of());
        repo.deleteById(id);
    }

    @Transactional
    public void updateRoles(Long id, Set<String> roleCodes) {
        if (!repo.existsById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND_4001);
        }
        userRoleAssignmentPort.updateRoles(id, roleCodes);
    }
}
