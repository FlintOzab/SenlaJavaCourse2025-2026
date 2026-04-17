package com.bookstore.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import bookstore.exception.BookstoreException;
import bookstore.service.Bookstore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for CSV import/export
 */
@RestController
@RequestMapping("/api/csv")
@Tag(name = "CSV Import/Export", description = "Endpoints for CSV data import and export")
public class CSVController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVController.class);
    
    private final Bookstore bookstore;
    
    @Autowired
    public CSVController(Bookstore bookstore) {
        this.bookstore = bookstore;
    }
    
    @GetMapping("/export/books")
    @Operation(summary = "Export books to CSV", 
              description = "Exports all books to a CSV file and returns it for download")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CSV file generated successfully"),
        @ApiResponse(responseCode = "500", description = "Error generating CSV file")
    })
    public ResponseEntity<InputStreamResource> exportBooksToCSV() throws IOException, BookstoreException {
        LOGGER.debug("REST request to export books to CSV");
        
        String fileName = "books_export_" + System.currentTimeMillis() + ".csv";
        String filePath = "exports/" + fileName;
        
        // Create exports directory if it doesn't exist
        Files.createDirectories(Paths.get("exports"));
        
        bookstore.exportBooksToCSV(filePath);
        
        File file = new File(filePath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
    
    @PostMapping(value = "/import/books", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import books from CSV", 
              description = "Imports books from an uploaded CSV file")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Books imported successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid CSV file"),
        @ApiResponse(responseCode = "500", description = "Error importing books")
    })
    public ResponseEntity<Map<String, Object>> importBooksFromCSV(
            @Parameter(description = "CSV file to upload", required = true)
            @RequestParam("file") MultipartFile file) throws IOException, BookstoreException {
        LOGGER.debug("REST request to import books from CSV, filename: {}", file.getOriginalFilename());
        
        Path tempFilePath = Files.createTempFile("import_", ".csv");
        file.transferTo(tempFilePath.toFile());
        
        bookstore.importBooksFromCSV(tempFilePath.toString());
        
        // Clean up temp file
        Files.deleteIfExists(tempFilePath);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Books imported successfully");
        response.put("filename", file.getOriginalFilename());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/export/orders")
    @Operation(summary = "Export orders to CSV", 
              description = "Exports all orders to a CSV file and returns it for download")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CSV file generated successfully"),
        @ApiResponse(responseCode = "500", description = "Error generating CSV file")
    })
    public ResponseEntity<InputStreamResource> exportOrdersToCSV() throws IOException, BookstoreException {
        LOGGER.debug("REST request to export orders to CSV");
        
        String fileName = "orders_export_" + System.currentTimeMillis() + ".csv";
        String filePath = "exports/" + fileName;
        
        // Create exports directory if it doesn't exist
        Files.createDirectories(Paths.get("exports"));
        
        bookstore.exportOrdersToCSV(filePath);
        
        File file = new File(filePath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
    
    @PostMapping(value = "/import/orders", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import orders from CSV", 
              description = "Imports orders from an uploaded CSV file")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orders imported successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid CSV file"),
        @ApiResponse(responseCode = "500", description = "Error importing orders")
    })
    public ResponseEntity<Map<String, Object>> importOrdersFromCSV(
            @Parameter(description = "CSV file to upload", required = true)
            @RequestParam("file") MultipartFile file) throws IOException, BookstoreException {
        LOGGER.debug("REST request to import orders from CSV, filename: {}", file.getOriginalFilename());
        
        // Save uploaded file temporarily
        Path tempFilePath = Files.createTempFile("import_orders_", ".csv");
        file.transferTo(tempFilePath.toFile());
        
        bookstore.importOrdersFromCSV(tempFilePath.toString());
        
        // Clean up temp file
        Files.deleteIfExists(tempFilePath);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Orders imported successfully");
        response.put("filename", file.getOriginalFilename());
        
        return ResponseEntity.ok(response);
    }
}