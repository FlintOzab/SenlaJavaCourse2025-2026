package com.bookstore.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.List;

public class OrderDTO {
    private Integer id;
    
    @NotNull(message = "Order completion date is required")
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private Date orderCompletionDate;
    
    @NotNull(message = "Order creation date is required")
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private Date orderCreationDate;
    
    private String status;
    
    @NotEmpty(message = "Order must contain at least one book")
    private List<Integer> bookIds;
    
    private List<BookDTO> books;
    private Long totalAmount;
    
    public OrderDTO() {}
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Date getOrderCompletionDate() { return orderCompletionDate; }
    public void setOrderCompletionDate(Date orderDate) { this.orderCompletionDate = orderDate; }
    
    public Date getOrderCreationDate() { return orderCreationDate; }
    public void setOrderCreationDate(Date orderDate) { this.orderCreationDate = orderDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<Integer> getBookIds() { return bookIds; }
    public void setBookIds(List<Integer> bookIds) { this.bookIds = bookIds; }
    
    public List<BookDTO> getBooks() { return books; }
    public void setBooks(List<BookDTO> books) { this.books = books; }
    
    public Long getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Long totalAmount) { this.totalAmount = totalAmount; }
}