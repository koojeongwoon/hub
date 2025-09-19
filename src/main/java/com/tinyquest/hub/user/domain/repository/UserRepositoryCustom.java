package com.tinyquest.hub.user.domain.repository;

import com.tinyquest.hub.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<User> search(String keyword, Pageable pageable);
}
