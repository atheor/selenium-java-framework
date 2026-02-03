package com.automation.examples.pages;

import com.automation.core.elements.Element;
import com.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * Page Object - Saucedemo Products Page
 * Demonstrates working with multiple elements and product interactions
 */
public class ProductsPage extends BasePage {
    
    // Page Elements
    private final Element pageTitle = new Element(By.className("title"), "Page Title");
    private final Element shoppingCart = new Element(By.className("shopping_cart_link"), "Shopping Cart");
    private final Element menuButton = new Element(By.id("react-burger-menu-btn"), "Menu Button");
    private final Element logoutLink = new Element(By.id("logout_sidebar_link"), "Logout Link");
    private final Element productSort = new Element(By.className("product_sort_container"), "Product Sort Dropdown");
    
    // Product elements (using indices)
    private final Element firstProductName = new Element(By.cssSelector(".inventory_item:nth-child(1) .inventory_item_name"), "First Product Name");
    private final Element firstProductPrice = new Element(By.cssSelector(".inventory_item:nth-child(1) .inventory_item_price"), "First Product Price");
    private final Element firstProductAddButton = new Element(By.cssSelector(".inventory_item:nth-child(1) .btn_inventory"), "First Product Add Button");
    
    /**
     * Verify if products page is loaded
     */
    @Override
    public boolean isPageLoaded() {
        return pageTitle.isDisplayed() && pageTitle.getText().equals("Products");
    }
    
    /**
     * Get page title text
     */
    public String getPageTitle() {
        return pageTitle.getText();
    }
    
    /**
     * Get count of products displayed
     */
    public int getProductCount() {
        Element products = new Element(By.className("inventory_item"), "Products");
        return products.getElements().size();
    }
    
    /**
     * Add first product to cart
     */
    public void addFirstProductToCart() {
        firstProductAddButton.click();
    }
    
    /**
     * Get first product name
     */
    public String getFirstProductName() {
        return firstProductName.getText();
    }
    
    /**
     * Get first product price
     */
    public String getFirstProductPrice() {
        return firstProductPrice.getText();
    }
    
    /**
     * Get shopping cart badge count
     */
    public String getCartBadgeCount() {
        Element cartBadge = new Element(By.className("shopping_cart_badge"), "Cart Badge");
        return cartBadge.getText();
    }
    
    /**
     * Check if cart badge is displayed
     */
    public boolean isCartBadgeDisplayed() {
        Element cartBadge = new Element(By.className("shopping_cart_badge"), "Cart Badge");
        return cartBadge.isDisplayed();
    }
    
    /**
     * Click shopping cart
     */
    public void clickShoppingCart() {
        shoppingCart.click();
    }
    
    /**
     * Open menu
     */
    public void openMenu() {
        menuButton.click();
    }
    
    /**
     * Click logout
     */
    public void clickLogout() {
        openMenu();
        logoutLink.waitForVisible();
        logoutLink.click();
    }
    
    /**
     * Select sort option
     */
    public void selectSortOption(String option) {
        productSort.selectByText(option);
    }
}
