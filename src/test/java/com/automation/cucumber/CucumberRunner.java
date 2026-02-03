package com.automation.cucumber;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG runner for Cucumber tests.
 * Enables parallel execution at scenario level.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.automation.cucumber", "com.automation.examples.stepdefinitions"},
        plugin = {
                "pretty",
                "html:test-output/cucumber-reports/cucumber.html",
                "json:test-output/cucumber-reports/cucumber.json",
                "junit:test-output/cucumber-reports/cucumber.xml"
        },
        monochrome = true,
        dryRun = false
)
public class CucumberRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Enable parallel execution of scenarios
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
