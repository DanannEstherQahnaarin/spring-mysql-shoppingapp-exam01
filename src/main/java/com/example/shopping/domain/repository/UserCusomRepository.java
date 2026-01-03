package com.example.shopping.domain.repository;

import java.util.List;

import com.example.shopping.domain.dto.AdminDto;

public interface UserCusomRepository {
    boolean isAdmin(Long userId);

    List<AdminDto.UserResponse> findAllUsers();
}
