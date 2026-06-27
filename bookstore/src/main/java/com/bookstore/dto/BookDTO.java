package com.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookDTO {
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    private String genre;
    private String isbn;

    @NotNull
    @Min(0)
    private BigDecimal price;

    private String description;

    @Min(0)
    private Integer stockQuantity;

    private String imageUrl;
}
