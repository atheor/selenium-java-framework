package com.automation.core.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * API Response wrapper with convenient methods for validation and data extraction.
 */
@Slf4j
@Getter
public class APIResponse {
    
    private final int statusCode;
    private final String statusMessage;
    private final String body;
    private final Map<String, String> headers;
    private final ObjectMapper objectMapper;
    
    public APIResponse(int statusCode, String statusMessage, String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.body = body;
        this.headers = headers;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Check if response is successful (2xx status code)
     */
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
    
    /**
     * Check if response is client error (4xx status code)
     */
    public boolean isClientError() {
        return statusCode >= 400 && statusCode < 500;
    }
    
    /**
     * Check if response is server error (5xx status code)
     */
    public boolean isServerError() {
        return statusCode >= 500 && statusCode < 600;
    }
    
    /**
     * Get response body as a specific type
     */
    public <T> T getBodyAs(Class<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (Exception e) {
            log.error("Error deserializing response body: {}", e.getMessage());
            throw new RuntimeException("Failed to deserialize response body", e);
        }
    }
    
    /**
     * Get header value by name (case-insensitive)
     */
    public String getHeader(String headerName) {
        return headers.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(headerName))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Check if header exists
     */
    public boolean hasHeader(String headerName) {
        return getHeader(headerName) != null;
    }
    
    /**
     * Get content type header
     */
    public String getContentType() {
        return getHeader("Content-Type");
    }
    
    /**
     * Check if response is JSON
     */
    public boolean isJson() {
        String contentType = getContentType();
        return contentType != null && contentType.contains("application/json");
    }
    
    /**
     * Check if response is XML
     */
    public boolean isXml() {
        String contentType = getContentType();
        return contentType != null && 
               (contentType.contains("application/xml") || contentType.contains("text/xml"));
    }
    
    /**
     * Get response time from header if available
     */
    public String getResponseTime() {
        return getHeader("X-Response-Time");
    }
    
    @Override
    public String toString() {
        return String.format("APIResponse{statusCode=%d, statusMessage='%s', bodyLength=%d}", 
                           statusCode, statusMessage, body != null ? body.length() : 0);
    }
}
