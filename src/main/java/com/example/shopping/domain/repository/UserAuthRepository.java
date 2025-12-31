package com.example.shopping.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopping.domain.entity.user.UserAuth;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

}
