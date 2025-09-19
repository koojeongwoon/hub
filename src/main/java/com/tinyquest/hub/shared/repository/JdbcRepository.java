package com.tinyquest.hub.shared.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JdbcClient 기반 공통 Repository 베이스 클래스.
 * @param <T> DTO 또는 Record 타입
 */
@Slf4j
@RequiredArgsConstructor
public abstract class JdbcRepository<T> {

    protected final JdbcClient jdbcClient;

    /**
     * 테이블명
     */
    protected abstract String tableName();

    /**
     * 매핑 대상 클래스 (DTO/Record)
     */
    protected abstract Class<T> mappedClass();

    /**
     * 전체 조회
     */
    public List<T> findAll() {
        String sql = "SELECT * FROM " + tableName();
        return jdbcClient.sql(sql)
                .query(mappedClass())
                .list();
    }

    /**
     * PK 단건 조회 (id라는 컬럼이 있다고 가정)
     */
    public Optional<T> findById(Object id) {
        String sql = "SELECT * FROM " + tableName() + " WHERE id = :id";
        return jdbcClient.sql(sql)
                .param("id", id)                  // ← named param
                .query(mappedClass())
                .optional();
    }

    public List<T> findWhereNamed(String whereClause, Map<String, ?> params) {
        String sql = "SELECT * FROM " + tableName() + " WHERE " + whereClause;
        return jdbcClient.sql(sql)
                .params(params)                   // ← Map 기반(named)
                .query(mappedClass())
                .list();
    }

    public int executeUpdateNamed(String sql, Map<String, ?> params) {
        return jdbcClient.sql(sql)
                .params(params)                   // ← Map 기반(named)
                .update();
    }

    /**
     * 단순 카운트
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + tableName();
        return jdbcClient.sql(sql)
                .query(Long.class)
                .single();
    }

    // 조건부 조회 (positional ? 파라미터)
    public List<T> findWhere(String whereClause, Object... params) {
        String sql = "SELECT * FROM " + tableName() + " WHERE " + whereClause;
        return jdbcClient.sql(sql)
                .params(params)                   // ← 한 번에 바인딩
                .query(mappedClass())
                .list();
    }

    // 업데이트/삭제/삽입
    public int executeUpdate(String sql, Object... params) {
        return jdbcClient.sql(sql)
                .params(params)                   // ← 한 번에 바인딩
                .update();
    }
}
