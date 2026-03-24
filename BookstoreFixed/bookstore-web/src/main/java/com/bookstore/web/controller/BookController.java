package com.bookstore.web.controller;

import com.bookstore.web.dto.BookDTO;
import com.bookstore.web.dto.ErrorResponse;
import com.bookstore.web.mapper.BookstoreMapper;
import bookstore.exception.BookstoreException;
import bookstore.exception.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import bookstore.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bookstore.service.Bookstore;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for book management
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);
    
    private final Bookstore bookstore;
    private final BookstoreMapper mapper;
    
    @Autowired
    public BookController(Bookstore bookstore, BookstoreMapper mapper) {
        this.bookstore = bookstore;
        this.mapper = mapper;
    }
    
    @GetMapping
    @Operation(summary = "Get all books", description = "Returns a list of all books in the inventory")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved books")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        LOGGER.debug("REST request to get all books");
        List<Book> books = bookstore.getAllBooks();
        return ResponseEntity.ok(mapper.toBookDTOList(books));
    }
    
    @GetMapping("/{isbn}")
    @Operation(summary = "Get book by ISBN", description = "Returns detailed information about a specific book")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book found"),
        @ApiResponse(responseCode = "404", description = "Book not found", 
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BookDTO> getBookByIsbn(
            @Parameter(description = "ISBN of the book", required = true)
            @PathVariable String isbn) throws EntityNotFoundException {
        LOGGER.debug("REST request to get book by ISBN: {}", isbn);
        Optional<Book> book = bookstore.findBookByIsbn(isbn);
        if (book == null || book.isEmpty()) {
            throw new EntityNotFoundException("Book not found with ISBN: " + isbn);
        }
        return ResponseEntity.ok(mapper.toBookDTO(book.get()));
    }
    
    @PostMapping
    @Operation(summary = "Add a new book", description = "Creates a new book in the inventory")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Book created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Book with this ISBN already exists")
    })
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO bookDTO) throws BookstoreException {
        LOGGER.debug("REST request to add book: {}", bookDTO.getIsbn());
        Book book = mapper.toBookEntity(bookDTO);
        bookstore.addBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toBookDTO(book));
    }
    
    @DeleteMapping("/{isbn}")
    @Operation(summary = "Write off a book", description = "Marks a book as written off (removed from inventory)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Book written off successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Void> writeOffBook(
            @Parameter(description = "ISBN of the book to write off", required = true)
            @PathVariable String isbn) throws BookstoreException {
        LOGGER.debug("REST request to write off book with ISBN: {}", isbn);
        bookstore.writeOffBook(isbn);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/stale")
    @Operation(summary = "Get stale books", description = "Returns books that haven't been sold for a long time")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stale books")
    public ResponseEntity<List<BookDTO>> getStaleBooks() {
        LOGGER.debug("REST request to get stale books");
        List<Book> staleBooks = bookstore.getOldBooks();
        return ResponseEntity.ok(mapper.toBookDTOList(staleBooks));
    }
}