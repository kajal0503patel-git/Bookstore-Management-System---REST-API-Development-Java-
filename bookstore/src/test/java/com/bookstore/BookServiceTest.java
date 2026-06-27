package com.bookstore;

import com.bookstore.dto.BookDTO;
import com.bookstore.exception.ApiException;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = new Book();
        sampleBook.setId(1L);
        sampleBook.setTitle("Clean Code");
        sampleBook.setAuthor("Robert C. Martin");
        sampleBook.setPrice(new BigDecimal("29.99"));
        sampleBook.setStockQuantity(10);
    }

    @Test
    void getBookById_shouldReturnBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        BookDTO result = bookService.getBookById(1L);

        assertThat(result.getTitle()).isEqualTo("Clean Code");
        assertThat(result.getPrice()).isEqualByComparingTo("29.99");
    }

    @Test
    void getBookById_shouldThrow_whenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Book not found");
    }

    @Test
    void getAllBooks_shouldReturnPaginatedResults() {
        var pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(sampleBook)));

        var result = bookService.getAllBooks(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void deleteBook_shouldThrow_whenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(99L))
                .isInstanceOf(ApiException.class);
    }
}
