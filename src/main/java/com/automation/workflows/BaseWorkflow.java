package com.automation.workflows;

import com.automation.core.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Base Workflow class that all workflows should extend.
 * Workflows contain business logic and orchestrate interactions between multiple pages.
 * Workflows are the middle layer between tests and page objects.
 * Tests should only call workflow methods, never page object methods directly.
 */
@Slf4j
public abstract class BaseWorkflow {
    
    protected ConfigManager config;
    
    public BaseWorkflow() {
        this.config = ConfigManager.getInstance();
        log.debug("Initialized workflow: {}", this.getClass().getSimpleName());
    }
    
    /**
     * Helper method to log workflow steps
     */
    protected void logStep(String stepDescription) {
        log.info("Workflow Step: {}", stepDescription);
    }
}
