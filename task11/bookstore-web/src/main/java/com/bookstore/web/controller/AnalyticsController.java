package com.bookstore.web.controller;

import com.bookstore.web.dto.DateRangeDTO;
import com.bookstore.web.dto.OrderDTO;
import com.bookstore.web.dto.RevenueStatsDTO;
import com.bookstore.web.mapper.BookstoreMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import bookstore.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import bookstore.service.Bookstore;

import java.util.List;

/**
 * REST controller for analytics and reports
 */
@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Analytics and reporting endpoints")
public class AnalyticsController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsController.class);
    
    private final Bookstore bookstore;
    private final BookstoreMapper mapper;
    
    @Autowired
    public AnalyticsController(Bookstore bookstore, BookstoreMapper mapper) {
        this.bookstore = bookstore;
        this.mapper = mapper;
    }
    
    @PostMapping("/revenue")
    @Operation(summary = "Get revenue statistics", 
              description = "Returns total revenue and completed orders count for a date range")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics"),
        @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<RevenueStatsDTO> getRevenueStatistics(@Valid @RequestBody DateRangeDTO dateRange) {
        LOGGER.debug("REST request to get revenue statistics for period: {} - {}", 
                     dateRange.getStartDate(), dateRange.getEndDate());
        
        long revenue = bookstore.getTotalRevenueInPeriod(
            dateRange.getStartDate(), dateRange.getEndDate());
        int ordersCount = bookstore.getCompletedOrdersCountInPeriod(
            dateRange.getStartDate(), dateRange.getEndDate());
        
        RevenueStatsDTO stats = new RevenueStatsDTO(revenue, ordersCount, dateRange);
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/completed-orders")
    @Operation(summary = "Get completed orders", 
              description = "Returns completed orders for a date range")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved orders"),
        @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<List<OrderDTO>> getCompletedOrders(@Valid @RequestBody DateRangeDTO dateRange) {
        LOGGER.debug("REST request to get completed orders for period: {} - {}", 
                     dateRange.getStartDate(), dateRange.getEndDate());
        
        List<Order> orders = bookstore.getCompletedOrdersInPeriod(
            dateRange.getStartDate(), dateRange.getEndDate());
        
        return ResponseEntity.ok(mapper.toOrderDTOList(orders));
    }
}