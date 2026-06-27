package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.exception.ApiException;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Page<BookDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<BookDTO> searchBooks(String query, Pageable pageable) {
        return bookRepository.searchBooks(query, pageable).map(this::toDTO);
    }

    public Page<BookDTO> getBooksByGenre(String genre, Pageable pageable) {
        return bookRepository.findByGenreIgnoreCase(genre, pageable).map(this::toDTO);
    }

    public BookDTO getBookById(Long id) {
        return toDTO(findOrThrow(id));
    }

    public BookDTO createBook(BookDTO dto) {
        Book book = toEntity(dto);
        return toDTO(bookRepository.save(book));
    }

    public BookDTO updateBook(Long id, BookDTO dto) {
        Book book = findOrThrow(id);
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setGenre(dto.getGenre());
        book.setIsbn(dto.getIsbn());
        book.setPrice(dto.getPrice());
        book.setDescription(dto.getDescription());
        book.setStockQuantity(dto.getStockQuantity());
        book.setImageUrl(dto.getImageUrl());
        return toDTO(bookRepository.save(book));
    }

    public void deleteBook(Long id) {
        findOrThrow(id);
        bookRepository.deleteById(id);
    }

    private Book findOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Book not found with id: " + id));
    }

    private BookDTO toDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setGenre(book.getGenre());
        dto.setIsbn(book.getIsbn());
        dto.setPrice(book.getPrice());
        dto.setDescription(book.getDescription());
        dto.setStockQuantity(book.getStockQuantity());
        dto.setImageUrl(book.getImageUrl());
        return dto;
    }

    private Book toEntity(BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setGenre(dto.getGenre());
        book.setIsbn(dto.getIsbn());
        book.setPrice(dto.getPrice());
        book.setDescription(dto.getDescription());
        book.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);
        book.setImageUrl(dto.getImageUrl());
        return book;
    }
}
