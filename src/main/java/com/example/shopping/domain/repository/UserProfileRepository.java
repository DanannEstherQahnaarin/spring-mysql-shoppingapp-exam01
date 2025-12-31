package com.example.shopping.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopping.domain.entity.user.UserProfile;

public interface UserProfileRepository  extends JpaRepository<UserProfile, Long> {

}
