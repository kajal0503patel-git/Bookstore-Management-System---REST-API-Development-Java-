package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    private String genre;

    @Column(unique = true)
    private String isbn;

    @NotNull
    @Min(0)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Min(0)
    private Integer stockQuantity = 0;

    private String imageUrl;
}
