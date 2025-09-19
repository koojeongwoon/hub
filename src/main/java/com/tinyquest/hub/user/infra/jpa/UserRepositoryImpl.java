package com.tinyquest.hub.user.infra.jpa;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tinyquest.hub.user.domain.entity.User;
import com.tinyquest.hub.user.domain.repository.UserRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tinyquest.hub.user.domain.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    // private final JdbcClient jdbcClient;
    private final JPAQueryFactory query;

    @Override
    public Page<User> search(String keyword, Pageable pageable) {

        List<User> content = query.selectFrom(user)
                .where(user.name.containsIgnoreCase(keyword)
                        .or(user.email.containsIgnoreCase(keyword)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = query.select(user.count())
                .from(user)
                .where(user.name.containsIgnoreCase(keyword)
                        .or(user.email.containsIgnoreCase(keyword)));

        Long total = countQuery.fetchOne();

        return new PageImpl<>(
                content,
                pageable,
                total != null ? total : 0L
        );
    }
}