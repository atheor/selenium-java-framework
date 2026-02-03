package com.automation.cucumber;

import io.cucumber.java.Scenario;

import java.util.HashMap;
import java.util.Map;

/**
 * Context class to share data between step definitions within a scenario.
 * Uses ThreadLocal to support parallel execution.
 */
public class ScenarioContext {
    
    private static final ThreadLocal<Map<String, Object>> context = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Scenario> scenario = new ThreadLocal<>();
    
    /**
     * Store data in the context
     */
    public static void set(String key, Object value) {
        context.get().put(key, value);
    }
    
    /**
     * Retrieve data from the context
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) context.get().get(key);
    }
    
    /**
     * Retrieve data with default value
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, T defaultValue) {
        return (T) context.get().getOrDefault(key, defaultValue);
    }
    
    /**
     * Check if key exists in context
     */
    public static boolean containsKey(String key) {
        return context.get().containsKey(key);
    }
    
    /**
     * Remove data from context
     */
    public static void remove(String key) {
        context.get().remove(key);
    }
    
    /**
     * Clear all context data
     */
    public static void clear() {
        context.get().clear();
        scenario.remove();
    }
    
    /**
     * Set the current scenario
     */
    public static void setScenario(Scenario currentScenario) {
        scenario.set(currentScenario);
    }
    
    /**
     * Get the current scenario
     */
    public static Scenario getScenario() {
        return scenario.get();
    }
}
