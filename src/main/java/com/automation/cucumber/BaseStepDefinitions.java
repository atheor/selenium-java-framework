package com.automation.cucumber;

import com.automation.core.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for step definitions.
 * Provides common functionality and access to shared resources.
 */
@Slf4j
public abstract class BaseStepDefinitions {
    
    protected ConfigManager config;
    protected ScenarioContext context;
    
    public BaseStepDefinitions() {
        this.config = ConfigManager.getInstance();
        log.debug("Initialized step definition: {}", this.getClass().getSimpleName());
    }
    
    /**
     * Log a step execution
     */
    protected void logStep(String stepDescription) {
        log.info("Step: {}", stepDescription);
    }
    
    /**
     * Store data in scenario context
     */
    protected void storeInContext(String key, Object value) {
        ScenarioContext.set(key, value);
        log.debug("Stored in context: {} = {}", key, value);
    }
    
    /**
     * Retrieve data from scenario context
     */
    protected <T> T getFromContext(String key) {
        T value = ScenarioContext.get(key);
        log.debug("Retrieved from context: {} = {}", key, value);
        return value;
    }
    
    /**
     * Retrieve data from scenario context with default value
     */
    protected <T> T getFromContext(String key, T defaultValue) {
        return ScenarioContext.get(key, defaultValue);
    }
}
