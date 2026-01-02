package com.example.shopping.domain.repository;

import static com.example.shopping.domain.entity.user.QRole.role;
import static com.example.shopping.domain.entity.user.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class UserRepositoryImpl implements UserCusomRepository {

    JPAQueryFactory queryFactory;

    @Override
    public boolean isAdmin(Long userId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(user)
                .join(user.roles, role) // User 엔티티의 roles 필드를 통해 조인
                .where(user.userId.eq(userId)
                        .and(role.roleType.eq("ADMIN"))) // role_type이 ADMIN인지 확인
                .fetchFirst();

        return fetchOne != null;
    }

}
