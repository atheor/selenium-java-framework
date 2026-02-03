package com.automation.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * Utility class for wait operations and retries.
 */
@Slf4j
public class WaitUtil {
    
    /**
     * Wait for condition with custom timeout and polling
     */
    public static <T> T waitFor(Callable<T> condition, Duration timeout, Duration pollingInterval) {
        Instant endTime = Instant.now().plus(timeout);
        Exception lastException = null;
        
        while (Instant.now().isBefore(endTime)) {
            try {
                T result = condition.call();
                if (result != null && (!(result instanceof Boolean) || (Boolean) result)) {
                    return result;
                }
            } catch (Exception e) {
                lastException = e;
            }
            
            try {
                Thread.sleep(pollingInterval.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Wait interrupted", e);
            }
        }
        
        throw new RuntimeException("Condition not met within timeout", lastException);
    }
    
    /**
     * Wait for condition with default polling (500ms)
     */
    public static <T> T waitFor(Callable<T> condition, Duration timeout) {
        return waitFor(condition, timeout, Duration.ofMillis(500));
    }
    
    /**
     * Retry operation with exponential backoff
     */
    public static <T> T retry(Callable<T> operation, int maxAttempts, Duration initialDelay) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return operation.call();
            } catch (Exception e) {
                lastException = e;
                log.warn("Attempt {}/{} failed: {}", attempt, maxAttempts, e.getMessage());
                
                if (attempt < maxAttempts) {
                    long delay = initialDelay.toMillis() * (long) Math.pow(2, attempt - 1);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }
        
        throw new RuntimeException("Operation failed after " + maxAttempts + " attempts", lastException);
    }
    
    /**
     * Retry with default 3 attempts and 1 second initial delay
     */
    public static <T> T retry(Callable<T> operation) {
        return retry(operation, 3, Duration.ofSeconds(1));
    }
}
