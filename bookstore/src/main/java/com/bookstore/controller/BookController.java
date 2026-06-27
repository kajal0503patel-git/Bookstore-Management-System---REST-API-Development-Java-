package com.bookstore.controller;

import com.bookstore.dto.BookDTO;
import com.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Browse and manage books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "List all books (supports ?search=query&genre=fiction&page=0&size=10)")
    public ResponseEntity<Page<BookDTO>> getBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            @PageableDefault(size = 10) Pageable pageable) {

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(bookService.searchBooks(search, pageable));
        }
        if (genre != null && !genre.isBlank()) {
            return ResponseEntity.ok(bookService.getBooksByGenre(genre, pageable));
        }
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single book")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    @Operation(summary = "Add a book (admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book (admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book (admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
