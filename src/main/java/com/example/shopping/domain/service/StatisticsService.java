package com.example.shopping.domain.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopping.domain.dto.StatDto;
import com.example.shopping.domain.exception.BusinessException;
import com.example.shopping.domain.exception.ErrorCode;
import com.example.shopping.domain.repository.StatisticsRepository;
import com.example.shopping.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository; // 권한 체크용

    // 관리자 권한 체크 후 통계 반환
    @Transactional(readOnly = true)
    public List<StatDto.DailySales> getDailySales(Long userId) {
        checkAdmin(userId);
        
        // 최근 30일간의 통계
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        return statisticsRepository.findDailySales(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<StatDto.CategorySales> getCategorySales(Long userId) {
        checkAdmin(userId);
        return statisticsRepository.findCategorySales();
    }
    
    private void checkAdmin(Long userId) {
        if (!userRepository.isAdmin(userId)) {
            throw new BusinessException(ErrorCode.ADMIN_PERMISSION_REQUIRED);
        }
    }
}