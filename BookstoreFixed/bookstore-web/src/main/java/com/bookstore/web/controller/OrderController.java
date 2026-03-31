package com.bookstore.web.controller;

import com.bookstore.web.dto.ErrorResponse;
import com.bookstore.web.dto.OrderDTO;
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
import bookstore.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bookstore.service.Bookstore;

import java.util.List;

/**
 * REST controller for order management
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
    
    private final Bookstore bookstore;
    private final BookstoreMapper mapper;
    
    @Autowired
    public OrderController(Bookstore bookstore, BookstoreMapper mapper) {
        this.bookstore = bookstore;
        this.mapper = mapper;
    }
    
    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns a list of all orders")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        LOGGER.debug("REST request to get all orders");
        List<Order> orders = bookstore.getAllOrders();
        return ResponseEntity.ok(mapper.toOrderDTOList(orders));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Returns detailed information about a specific order")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Integer id) throws EntityNotFoundException {
        LOGGER.debug("REST request to get order by ID: {}", id);
        Order order = bookstore.findOrderById(id).orElse(null);
        if (order == null) {
            throw new EntityNotFoundException("Order not found with ID: " + id);
        }
        return ResponseEntity.ok(mapper.toOrderDTO(order));
    }
    
    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order with the specified books")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "One or more books not found")
    })
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) throws BookstoreException {
        LOGGER.debug("REST request to create order with {} books", 
                     orderDTO.getBookIds() != null ? orderDTO.getBookIds().size() : 0);
        
        Order order = bookstore.createOrder(orderDTO.getBookIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toOrderDTO(order));
    }
    
    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order", description = "Cancels an existing order")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Order cannot be cancelled")
    })
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "Order ID to cancel", required = true)
            @PathVariable Integer id) throws BookstoreException {
        LOGGER.debug("REST request to cancel order with ID: {}", id);
        bookstore.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/complete")
    @Operation(summary = "Complete an order", description = "Marks an order as completed")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Order completed successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Order cannot be completed")
    })
    public ResponseEntity<Void> completeOrder(
            @Parameter(description = "Order ID to complete", required = true)
            @PathVariable Integer id) throws BookstoreException {
        LOGGER.debug("REST request to complete order with ID: {}", id);
        bookstore.completeOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an existing order")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status")
    })
    public ResponseEntity<Void> updateOrderStatus(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Integer id,
            @Parameter(description = "New status (NEW, COMPLETED, CANCELLED)", required = true)
            @RequestParam String status) throws BookstoreException {
        LOGGER.debug("REST request to update order {} status to: {}", id, status);
        
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        bookstore.updateOrderStatus(id, orderStatus);
        return ResponseEntity.noContent().build();
    }
}