# Example Tests Overview

This document describes the example tests included in the framework, demonstrating UI automation with Saucedemo and API testing with PetStore.

## UI Tests - Saucedemo.com

### Location
- **Tests**: `src/test/java/com/automation/examples/tests/SauceDemoTest.java`
- **Pages**: `src/test/java/com/automation/examples/pages/LoginPage.java`, `ProductsPage.java`
- **Workflows**: `src/test/java/com/automation/examples/workflows/LoginWorkflow.java`
- **Cucumber**: `src/test/resources/features/Login.feature`
- **Steps**: `src/test/java/com/automation/examples/stepdefinitions/LoginStepDefinitions.java`

### Test Scenarios

#### 1. testSuccessfulLogin
- **Description**: Verifies successful login with valid credentials
- **Username**: `standard_user`
- **Password**: `secret_sauce`
- **Validation**: 
  - User is redirected to products page
  - Page title displays "Products"

#### 2. testInvalidLogin
- **Description**: Verifies error message for invalid credentials
- **Username**: `invalid_user`
- **Password**: `wrong_password`
- **Validation**: 
  - Error message is displayed
  - Message contains "Username and password do not match"

#### 3. testLockedOutUser
- **Description**: Verifies locked out user cannot login
- **Username**: `locked_out_user`
- **Password**: `secret_sauce`
- **Validation**: 
  - Error message is displayed
  - Message contains "locked out"

#### 4. testProductCount
- **Description**: Verifies correct number of products are displayed
- **Validation**: 6 products are shown on the products page

#### 5. testAddToCart
- **Description**: Verifies adding product to shopping cart
- **Validation**: 
  - Cart badge appears after adding product
  - Cart count shows "1"

## API Tests - PetStore Swagger API

### Location
- **Tests**: `src/test/java/com/automation/examples/api/PetStoreAPITest.java`
- **Base URL**: `https://petstore.swagger.io/v2`

### Test Scenarios

#### 1. testCreatePet
- **Method**: POST
- **Endpoint**: `/pet`
- **Description**: Creates a new pet with ID, name, category, and status
- **Validation**: 
  - Status code 200
  - Response is JSON
  - Response contains pet name "Buddy"

#### 2. testGetPet
- **Method**: GET
- **Endpoint**: `/pet/{petId}`
- **Description**: Retrieves pet by ID
- **Validation**: 
  - Status code 200
  - Response contains pet details
  - Pet name and status match created pet

#### 3. testUpdatePet
- **Method**: PUT
- **Endpoint**: `/pet`
- **Description**: Updates pet information
- **Validation**: 
  - Status code 200
  - Response contains updated name "Buddy Updated"
  - Status changed to "sold"

#### 4. testFindPetsByStatus
- **Method**: GET
- **Endpoint**: `/pet/findByStatus?status=available`
- **Description**: Finds all pets with "available" status
- **Validation**: 
  - Status code 200
  - Response is JSON array
  - Contains pets with "available" status

#### 5. testDeletePet
- **Method**: DELETE
- **Endpoint**: `/pet/{petId}`
- **Description**: Deletes a pet and verifies deletion
- **Validation**: 
  - Delete returns status code 200
  - Subsequent GET returns 404 (not found)

#### 6. testGetNonExistentPet
- **Method**: GET
- **Endpoint**: `/pet/999999999`
- **Description**: Verifies 404 for non-existent pet
- **Validation**: Status code 404

#### 7. testGetStoreInventory
- **Method**: GET
- **Endpoint**: `/store/inventory`
- **Description**: Retrieves store inventory
- **Validation**: 
  - Status code 200
  - Response is JSON object

## Running the Tests

### Run All Tests
```bash
mvn test
```

### Run UI Tests Only
```bash
mvn test -Dtest=SauceDemoTest
```

### Run API Tests Only
```bash
mvn test -Dtest=PetStoreAPITest
```

### Run Cucumber Tests
```bash
mvn test -Dtest=CucumberRunner
```

## Test Results (Last Run)

✅ **All 12 tests passed**
- UI Tests: 5/5 passed
- API Tests: 7/7 passed
- Total Time: ~12 seconds

## Framework Features Demonstrated

### UI Tests Demonstrate:
- ✅ 3-layer architecture (Test → Workflow → Page)
- ✅ Element wrapper with auto-waiting
- ✅ Multi-browser support (Chrome default)
- ✅ Page Object Model
- ✅ Workflow orchestration
- ✅ ExtentReports integration
- ✅ Screenshot capture on failure
- ✅ Cucumber/BDD support

### API Tests Demonstrate:
- ✅ REST API testing (GET, POST, PUT, DELETE)
- ✅ Request/Response handling
- ✅ JSON validation
- ✅ Status code assertions
- ✅ API retry logic
- ✅ Response body validation
- ✅ Query parameter handling

## Adding Your Own Tests

### For UI Tests:
1. Create page objects in `src/test/java/com/automation/pages/`
2. Create workflows in `src/test/java/com/automation/workflows/`
3. Create tests in `src/test/java/com/automation/tests/`
4. Tests should only call workflow methods (never page objects directly)

### For API Tests:
1. Create test class extending `BaseTest`
2. Initialize `APIClient` with base URL
3. Use HTTP methods: `get()`, `post()`, `put()`, `patch()`, `delete()`
4. Validate responses using AssertJ assertions

## Configuration

Update `src/main/resources/config.properties` to customize:
- Browser settings
- Timeouts
- URLs
- Screenshot options
- Report settings
