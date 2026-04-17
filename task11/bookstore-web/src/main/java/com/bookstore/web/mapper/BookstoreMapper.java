package com.bookstore.web.mapper;

import com.bookstore.web.dto.BookDTO;
import com.bookstore.web.dto.OrderDTO;
import bookstore.model.Book;
import bookstore.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookstoreMapper {
    
    public BookDTO toBookDTO(Book book) {
        if (book == null) return null;
        
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPrice(book.getPrice());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setArrivalDate(book.getArrivalDate());
        dto.setDescription(book.getDescription());
        dto.setStatus(book.getStatus() != null ? book.getStatus().name() : null);
        
        return dto;
    }
    
    public List<BookDTO> toBookDTOList(List<Book> books) {
        return books.stream()
                .map(this::toBookDTO)
                .collect(Collectors.toList());
    }
    
    public Book toBookEntity(BookDTO dto) {
        if (dto == null) return null;
        
        Book book = new Book(
            dto.getIsbn(),
            dto.getTitle(),
            dto.getAuthor(),
            dto.getPrice(),
            dto.getPublicationDate(),
            dto.getArrivalDate(),
            dto.getDescription()
        );
        
        if (dto.getId() != null) {
            book.setId(dto.getId());
        }
        
        if (dto.getStatus() != null) {
            book.setStatus(Book.BookStatus.valueOf(dto.getStatus()));
        }
        
        return book;
    }
    
    public OrderDTO toOrderDTO(Order order) {
        if (order == null) return null;
        
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderCompletionDate(order.getCompletionDate());
        dto.setOrderCreationDate(order.getCreationDate());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        
        if (order.getBooks() != null) {
            dto.setBooks(toBookDTOList(order.getBooks()));
            dto.setTotalAmount(order.calculateTotalPrice());
        }
        
        return dto;
    }
    
    public List<OrderDTO> toOrderDTOList(List<Order> orders) {
        return orders.stream()
                .map(this::toOrderDTO)
                .collect(Collectors.toList());
    }
}
