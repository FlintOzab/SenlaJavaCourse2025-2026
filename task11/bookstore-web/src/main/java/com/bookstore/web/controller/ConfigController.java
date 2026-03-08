package com.bookstore.web.controller;

import bookstore.config.BookstoreConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for configuration management
 */
@RestController
@RequestMapping("/api/config")
@Tag(name = "Configuration", description = "Configuration management endpoints")
public class ConfigController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);
    
    private final BookstoreConfig config;
    
    @Autowired
    public ConfigController(BookstoreConfig config) {
        this.config = config;
    }
    
    @GetMapping
    @Operation(summary = "Get configuration", description = "Returns current application configuration")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved configuration")
    public ResponseEntity<Map<String, Object>> getConfig() {
        LOGGER.debug("REST request to get configuration");
        
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("staleMonthsThreshold", config.getStaleMonthsThreshold());
        configMap.put("autoFulfillRequests", config.isAutoFulfillRequests());
        configMap.put("exportDirectory", config.getExportDirectory());
        
        return ResponseEntity.ok(configMap);
    }
    
    @PatchMapping("/stale-threshold")
    @Operation(summary = "Update stale threshold", 
              description = "Updates the threshold for stale books (in months)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Threshold updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid threshold value")
    })
    public ResponseEntity<Map<String, Object>> updateStaleThreshold(
            @Parameter(description = "New threshold in months", required = true)
            @RequestParam int threshold) {
        LOGGER.debug("REST request to update stale threshold to: {}", threshold);
        
        config.setStaleMonthsThreshold(threshold);
        
        Map<String, Object> response = new HashMap<>();
        response.put("staleMonthsThreshold", config.getStaleMonthsThreshold());
        response.put("message", "Threshold updated successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/auto-fulfill")
    @Operation(summary = "Toggle auto-fulfill", 
              description = "Enables or disables automatic request fulfillment")
    @ApiResponse(responseCode = "200", description = "Setting updated successfully")
    public ResponseEntity<Map<String, Object>> toggleAutoFulfill(
            @Parameter(description = "Enable or disable", required = true)
            @RequestParam boolean enabled) {
        LOGGER.debug("REST request to set auto-fulfill to: {}", enabled);
        
        config.setAutoFulfillRequests(enabled);
        
        Map<String, Object> response = new HashMap<>();
        response.put("autoFulfillRequests", config.isAutoFulfillRequests());
        response.put("message", "Auto-fulfill setting updated successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/export-directory")
    @Operation(summary = "Update export directory", 
              description = "Updates the directory for CSV exports")
    @ApiResponse(responseCode = "200", description = "Directory updated successfully")
    public ResponseEntity<Map<String, Object>> updateExportDirectory(
            @Parameter(description = "New export directory path", required = true)
            @RequestParam String directory) {
        LOGGER.debug("REST request to update export directory to: {}", directory);
        
        config.setExportDirectory(directory);
        
        Map<String, Object> response = new HashMap<>();
        response.put("exportDirectory", config.getExportDirectory());
        response.put("message", "Export directory updated successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/save")
    @Operation(summary = "Save configuration", description = "Saves current configuration to file")
    @ApiResponse(responseCode = "200", description = "Configuration saved successfully")
    public ResponseEntity<Map<String, String>> saveConfig() {
        LOGGER.debug("REST request to save configuration");
        
        config.savePropertiesToFile();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Configuration saved successfully");
        
        return ResponseEntity.ok(response);
    }
}