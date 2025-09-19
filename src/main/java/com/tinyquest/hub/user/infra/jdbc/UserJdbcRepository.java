package com.tinyquest.hub.user.infra.jdbc;

import com.tinyquest.hub.shared.repository.JdbcRepository;
import com.tinyquest.hub.user.api.dto.response.UserDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class UserJdbcRepository extends JdbcRepository<UserDetailResponse> {

    public UserJdbcRepository(JdbcClient jdbcClient) {
        super(jdbcClient);
    }

    @Override
    protected String tableName() {
        return "users";
    }

    @Override
    protected Class<UserDetailResponse> mappedClass() {
        return UserDetailResponse.class;
    }

    // 필요한 커스텀 쿼리 메서드 추가
    public int deactivate(Long userId) {
        return executeUpdate("UPDATE users SET active = false WHERE id = ?", userId);
    }

    // String sql = SqlLoader.fromClasspath("promotion_item_upsert.sql");
}
