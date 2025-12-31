package com.example.shopping.domain.dto;

import jakarta.validation.constraints.NotBlank;

public class ProductDto {
    public static class createCategory {
        @NotBlank
        private String name;
    }

    public static class createProduct {

    }
}
