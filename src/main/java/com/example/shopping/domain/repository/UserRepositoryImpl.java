package com.example.shopping.domain.repository;

import static com.example.shopping.domain.entity.user.QRole.role;
import static com.example.shopping.domain.entity.user.QUser.user;
import static com.example.shopping.domain.entity.user.QUserProfile.userProfile;

import java.util.List;

import com.example.shopping.domain.dto.AdminDto;
import com.querydsl.core.types.Projections;
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

    @Override
    public List<AdminDto.UserResponse> findAllUsers() {
        return queryFactory
                .select(Projections.fields(AdminDto.UserResponse.class,
                        user.userId,
                        user.loginId,
                        userProfile.name, // UserProfile 조인 필요
                        user.email,
                        user.status.stringValue().as("status"),
                        user.createdAt.as("joinedAt")
                ))
                .from(user)
                .leftJoin(userProfile).on(user.userId.eq(userProfile.userId)) // 1:1 관계 조인
                .orderBy(user.userId.desc())
                .fetch();
    }

}
