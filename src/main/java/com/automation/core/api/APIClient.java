package com.automation.core.api;

import com.automation.core.config.ConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP Client for API testing with built-in retry logic and response handling.
 * Supports all standard HTTP methods with JSON serialization/deserialization.
 */
@Slf4j
public class APIClient {
    
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ConfigManager config;
    private final Map<String, String> defaultHeaders;
    private String baseUrl;
    
    public APIClient() {
        this.config = ConfigManager.getInstance();
        this.baseUrl = config.getApiBaseUrl();
        this.objectMapper = new ObjectMapper();
        this.defaultHeaders = new HashMap<>();
        
        // Configure HTTP client with timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.of(config.getApiTimeout(), TimeUnit.MILLISECONDS))
                .setResponseTimeout(Timeout.of(config.getApiTimeout(), TimeUnit.MILLISECONDS))
                .build();
        
        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        
        // Set default headers
        defaultHeaders.put("Content-Type", "application/json");
        defaultHeaders.put("Accept", "application/json");
        
        log.info("API Client initialized with base URL: {}", baseUrl);
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        log.info("Base URL updated to: {}", baseUrl);
    }
    
    public void addDefaultHeader(String key, String value) {
        defaultHeaders.put(key, value);
        log.debug("Added default header: {} = {}", key, value);
    }
    
    public void removeDefaultHeader(String key) {
        defaultHeaders.remove(key);
        log.debug("Removed default header: {}", key);
    }
    
    public void clearDefaultHeaders() {
        defaultHeaders.clear();
        log.debug("Cleared all default headers");
    }
    
    /**
     * Execute GET request
     */
    public APIResponse get(String endpoint) {
        return get(endpoint, new HashMap<>());
    }
    
    public APIResponse get(String endpoint, Map<String, String> headers) {
        String url = buildUrl(endpoint);
        HttpGet request = new HttpGet(url);
        setHeaders(request, headers);
        
        log.info("GET request to: {}", url);
        return executeRequest(request);
    }
    
    /**
     * Execute POST request
     */
    public APIResponse post(String endpoint, Object body) {
        return post(endpoint, body, new HashMap<>());
    }
    
    public APIResponse post(String endpoint, Object body, Map<String, String> headers) {
        String url = buildUrl(endpoint);
        HttpPost request = new HttpPost(url);
        setHeaders(request, headers);
        setBody(request, body);
        
        log.info("POST request to: {}", url);
        return executeRequest(request);
    }
    
    /**
     * Execute PUT request
     */
    public APIResponse put(String endpoint, Object body) {
        return put(endpoint, body, new HashMap<>());
    }
    
    public APIResponse put(String endpoint, Object body, Map<String, String> headers) {
        String url = buildUrl(endpoint);
        HttpPut request = new HttpPut(url);
        setHeaders(request, headers);
        setBody(request, body);
        
        log.info("PUT request to: {}", url);
        return executeRequest(request);
    }
    
    /**
     * Execute PATCH request
     */
    public APIResponse patch(String endpoint, Object body) {
        return patch(endpoint, body, new HashMap<>());
    }
    
    public APIResponse patch(String endpoint, Object body, Map<String, String> headers) {
        String url = buildUrl(endpoint);
        HttpPatch request = new HttpPatch(url);
        setHeaders(request, headers);
        setBody(request, body);
        
        log.info("PATCH request to: {}", url);
        return executeRequest(request);
    }
    
    /**
     * Execute DELETE request
     */
    public APIResponse delete(String endpoint) {
        return delete(endpoint, new HashMap<>());
    }
    
    public APIResponse delete(String endpoint, Map<String, String> headers) {
        String url = buildUrl(endpoint);
        HttpDelete request = new HttpDelete(url);
        setHeaders(request, headers);
        
        log.info("DELETE request to: {}", url);
        return executeRequest(request);
    }
    
    /**
     * Execute request with retry logic
     */
    private APIResponse executeRequest(HttpUriRequestBase request) {
        int retryCount = config.getApiRetryCount();
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= retryCount; attempt++) {
            try {
                log.debug("Attempt {}/{} for request: {}", attempt, retryCount, request.getUri());
                
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    return buildResponse(response);
                }
            } catch (IOException | org.apache.hc.core5.http.ParseException | java.net.URISyntaxException e) {
                lastException = e;
                log.warn("Request failed on attempt {}/{}: {}", attempt, retryCount, e.getMessage());
                
                if (attempt < retryCount) {
                    try {
                        Thread.sleep(1000 * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        try {
            log.error("All {} attempts failed for request: {}", retryCount, request.getUri());
        } catch (java.net.URISyntaxException e) {
            log.error("All {} attempts failed for request", retryCount);
        }
        throw new RuntimeException("Request failed after " + retryCount + " attempts", lastException);
    }
    
    private APIResponse buildResponse(CloseableHttpResponse response) throws IOException, org.apache.hc.core5.http.ParseException {
        int statusCode = response.getCode();
        String statusMessage = response.getReasonPhrase();
        
        HttpEntity entity = response.getEntity();
        String body = entity != null ? EntityUtils.toString(entity) : "";
        
        Map<String, String> headers = new HashMap<>();
        response.headerIterator().forEachRemaining(header -> 
            headers.put(header.getName(), header.getValue())
        );
        
        log.info("Response received - Status: {}, Body length: {}", statusCode, body.length());
        
        return new APIResponse(statusCode, statusMessage, body, headers);
    }
    
    private String buildUrl(String endpoint) {
        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            return endpoint;
        }
        
        String url = baseUrl;
        if (!baseUrl.endsWith("/") && !endpoint.startsWith("/")) {
            url += "/";
        }
        url += endpoint;
        
        return url;
    }
    
    private void setHeaders(HttpUriRequestBase request, Map<String, String> additionalHeaders) {
        // Add default headers
        defaultHeaders.forEach(request::setHeader);
        
        // Add/override with additional headers
        additionalHeaders.forEach(request::setHeader);
    }
    
    private void setBody(HttpUriRequestBase request, Object body) {
        try {
            String jsonBody;
            if (body instanceof String) {
                jsonBody = (String) body;
            } else {
                jsonBody = objectMapper.writeValueAsString(body);
            }
            
            StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            
            if (request instanceof HttpPost) {
                ((HttpPost) request).setEntity(entity);
            } else if (request instanceof HttpPut) {
                ((HttpPut) request).setEntity(entity);
            } else if (request instanceof HttpPatch) {
                ((HttpPatch) request).setEntity(entity);
            }
            
            log.debug("Request body set: {}", jsonBody);
        } catch (Exception e) {
            log.error("Error serializing request body: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize request body", e);
        }
    }
    
    public void close() {
        try {
            httpClient.close();
            log.info("API Client closed");
        } catch (IOException e) {
            log.error("Error closing API Client: {}", e.getMessage());
        }
    }
}
