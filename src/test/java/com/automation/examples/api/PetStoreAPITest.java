package com.automation.examples.api;

import com.automation.core.api.APIClient;
import com.automation.core.api.APIResponse;
import com.automation.tests.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * API Test - PetStore API Testing
 * Demonstrates REST API testing capabilities using Swagger PetStore
 * https://petstore.swagger.io/
 */
public class PetStoreAPITest extends BaseTest {
    
    private APIClient apiClient;
    private Long petId;
    
    @BeforeClass
    public void setupAPI() {
        apiClient = new APIClient();
        apiClient.setBaseUrl("https://petstore.swagger.io/v2");
        petId = System.currentTimeMillis(); // Use timestamp as unique pet ID
    }
    
    @Test(description = "Create a new pet in the store", priority = 1)
    public void testCreatePet() {
        logInfo("Creating new pet with ID: " + petId);
        
        // Create request body
        Map<String, Object> petData = new HashMap<>();
        petData.put("id", petId);
        petData.put("name", "Buddy");
        petData.put("status", "available");
        
        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);
        category.put("name", "Dogs");
        petData.put("category", category);
        
        String[] photoUrls = {"https://example.com/buddy.jpg"};
        petData.put("photoUrls", photoUrls);
        
        // Send POST request
        APIResponse response = apiClient.post("/pet", petData);
        
        // Verify response
        assertThat(response.getStatusCode())
            .as("Status code should be 200")
            .isEqualTo(200);
        
        assertThat(response.isJson())
            .as("Response should be JSON")
            .isTrue();
        
        assertThat(response.getBody())
            .as("Response should contain pet name")
            .contains("Buddy");
        
        logPass("Pet created successfully");
    }
    
    @Test(description = "Get pet by ID", priority = 2, dependsOnMethods = "testCreatePet")
    public void testGetPet() {
        logInfo("Getting pet with ID: " + petId);
        
        // Send GET request
        APIResponse response = apiClient.get("/pet/" + petId);
        
        // Verify response
        assertThat(response.isSuccessful())
            .as("Request should be successful")
            .isTrue();
        
        assertThat(response.getStatusCode())
            .as("Status code should be 200")
            .isEqualTo(200);
        
        assertThat(response.getBody())
            .as("Response should contain pet name 'Buddy'")
            .contains("Buddy")
            .contains("available");
        
        logPass("Pet retrieved successfully");
    }
    
    @Test(description = "Update pet information", priority = 3, dependsOnMethods = "testGetPet")
    public void testUpdatePet() {
        logInfo("Updating pet with ID: " + petId);
        
        // Create updated pet data
        Map<String, Object> petData = new HashMap<>();
        petData.put("id", petId);
        petData.put("name", "Buddy Updated");
        petData.put("status", "sold");
        
        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);
        category.put("name", "Dogs");
        petData.put("category", category);
        
        String[] photoUrls = {"https://example.com/buddy-updated.jpg"};
        petData.put("photoUrls", photoUrls);
        
        // Send PUT request
        APIResponse response = apiClient.put("/pet", petData);
        
        // Verify response
        assertThat(response.isSuccessful())
            .as("Update request should be successful")
            .isTrue();
        
        assertThat(response.getBody())
            .as("Response should contain updated name")
            .contains("Buddy Updated")
            .contains("sold");
        
        logPass("Pet updated successfully");
    }
    
    @Test(description = "Find pets by status", priority = 4)
    public void testFindPetsByStatus() {
        logInfo("Finding pets by status: available");
        
        // Send GET request with query parameter
        APIResponse response = apiClient.get("/pet/findByStatus?status=available");
        
        // Verify response
        assertThat(response.isSuccessful())
            .as("Request should be successful")
            .isTrue();
        
        assertThat(response.isJson())
            .as("Response should be JSON array")
            .isTrue();
        
        assertThat(response.getBody())
            .as("Response should contain pets")
            .contains("available");
        
        logPass("Pets found by status successfully");
    }
    
    @Test(description = "Delete a pet", priority = 5, dependsOnMethods = "testUpdatePet")
    public void testDeletePet() {
        logInfo("Deleting pet with ID: " + petId);
        
        // Send DELETE request
        APIResponse response = apiClient.delete("/pet/" + petId);
        
        // Verify response
        assertThat(response.isSuccessful())
            .as("Delete request should be successful")
            .isTrue();
        
        logPass("Pet deleted successfully");
        
        // Verify pet is deleted by trying to get it
        APIResponse getResponse = apiClient.get("/pet/" + petId);
        assertThat(getResponse.getStatusCode())
            .as("Pet should not be found after deletion")
            .isEqualTo(404);
        
        logPass("Verified pet no longer exists");
    }
    
    @Test(description = "Verify 404 for non-existent pet", priority = 6)
    public void testGetNonExistentPet() {
        logInfo("Attempting to get non-existent pet");
        
        Long nonExistentId = 999999999L;
        APIResponse response = apiClient.get("/pet/" + nonExistentId);
        
        // Verify 404 response
        assertThat(response.getStatusCode())
            .as("Status code should be 404 for non-existent pet")
            .isEqualTo(404);
        
        logPass("404 error verified for non-existent pet");
    }
    
    @Test(description = "Get store inventory", priority = 7)
    public void testGetStoreInventory() {
        logInfo("Getting store inventory");
        
        // Send GET request
        APIResponse response = apiClient.get("/store/inventory");
        
        // Verify response
        assertThat(response.isSuccessful())
            .as("Request should be successful")
            .isTrue();
        
        assertThat(response.isJson())
            .as("Response should be JSON")
            .isTrue();
        
        logPass("Store inventory retrieved successfully");
    }
}
