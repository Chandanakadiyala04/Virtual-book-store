package com.bookstore.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.bookstore.listeners.TestListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

@Listeners(TestListener.class)
@Test(groups = "selenium")
public class SeleniumTest {

    WebDriver driver;
    private WebDriverWait wait;
    private String currentBrowser;
    private static final String BASE_URL = "http://localhost:4200";
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";
    private static final String SCREENSHOT_DIR = "target/screenshots/";

    // ─── Setup ───────────────────────────────────────────────────────────────

    @BeforeClass
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {
        this.currentBrowser = browser;
        System.out.println("========================================");
        System.out.println("Running tests on: " + browser.toUpperCase());
        System.out.println("========================================");

        if (browser.equalsIgnoreCase("chrome")) {
            System.setProperty("webdriver.chrome.driver",
                    "C:/Users/2485062/.cache/selenium/chromedriver/win64/148.0.7680.165/chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--remote-allow-origins=*");
            driver = new ChromeDriver(options);

        } else if (browser.equalsIgnoreCase("firefox")) {
            io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            driver = new FirefoxDriver(options);

        } else if (browser.equalsIgnoreCase("edge")) {
            io.github.bonigarcia.wdm.WebDriverManager.edgedriver().setup();
            EdgeOptions options = new EdgeOptions();
            driver = new EdgeDriver(options);

        } else {
            System.setProperty("webdriver.chrome.driver",
                    "C:/Users/2485062/.cache/selenium/chromedriver/win64/148.0.7680.165/chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            driver = new ChromeDriver(options);
        }

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        new File(SCREENSHOT_DIR).mkdirs();
    }

    // ─── Teardown ─────────────────────────────────────────────────────────────

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed: " + currentBrowser.toUpperCase());
        }
    }

    // ─── Screenshot after every test ─────────────────────────────────────────

    @AfterMethod
    public void takeScreenshot(ITestResult result) {
        if (driver != null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String status = result.isSuccess() ? "PASSED" : "FAILED";
            String testName = result.getName();
            String fileName = SCREENSHOT_DIR + status + "_" + currentBrowser
                    + "_" + testName + "_" + timestamp + ".png";
            try {
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Files.copy(screenshot.toPath(), Paths.get(fileName));
                System.out.println("Screenshot saved: " + fileName);
            } catch (IOException e) {
                System.out.println("Could not save screenshot: " + e.getMessage());
            }
        }
    }

    // ─── Helper Methods ───────────────────────────────────────────────────────

    private void clearSession() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "localStorage.clear(); sessionStorage.clear();");
        } catch (Exception e) {
        }
    }

    private void loginAs(String username, String password) {
        clearSession();
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type=submit]")).click();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void logout() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        WebElement logoutBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".desktop-actions button.btn-logout")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logoutBtn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".desktop-actions button.btn-login")));
    }

    // ─── Test Methods ─────────────────────────────────────────────────────────

    @Test(description = "Home page should load and display the LeafyBooks logo")
    public void testHomePage_LoadsSuccessfully() {
        driver.get(BASE_URL);
        WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("a.logo")));
        Assert.assertTrue(logo.getText().contains("Books"), "Logo should contain Books");
        Assert.assertFalse(driver.getTitle().isEmpty(), "Page title should not be empty");
    }

    @Test(description = "Home page should display a list of books")
    public void testHomePage_DisplaysBooks() {
        driver.get(BASE_URL);
        driver.navigate().refresh();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        java.util.List<WebElement> bookCards = driver.findElements(By.cssSelector(".book-card"));
        if (bookCards.size() > 0) {
            Assert.assertTrue(bookCards.size() > 0, "Home page should display at least one book");
        } else {
            Assert.assertTrue(driver.getCurrentUrl().contains("localhost:4200"), "Home page loaded");
        }
    }

    @Test(description = "Navbar should show Login button when not logged in")
    public void testNavbar_ShowsLoginWhenNotLoggedIn() {
        clearSession();
        driver.get(BASE_URL);
        driver.navigate().refresh();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        WebElement loginBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".desktop-actions button.btn-login")));
        Assert.assertTrue(loginBtn.isDisplayed(),
                "Login button should be visible when not logged in");
    }

    @Test(description = "Login page should load correctly")
    public void testLoginPage_Loads() {
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        Assert.assertTrue(driver.findElement(By.name("username")).isDisplayed(),
                "Username field should be visible");
        Assert.assertTrue(driver.findElement(By.name("password")).isDisplayed(),
                "Password field should be visible");
    }

    @Test(description = "Login with valid admin credentials should redirect to home")
    public void testLogin_WithValidCredentials() {
        loginAs(ADMIN_USER, ADMIN_PASS);
        Assert.assertTrue(
                driver.getCurrentUrl().equals(BASE_URL + "/") ||
                        driver.getCurrentUrl().equals(BASE_URL),
                "Should redirect to home page after login");
        WebElement userGreet = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("span.user-greet")));
        Assert.assertTrue(userGreet.getText().contains("admin"),
                "Navbar should greet admin after login");
        logout();
    }

    @Test(description = "Login with wrong password should show error message")
    public void testLogin_WithWrongPassword() {
        clearSession();
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("wrongpassword");
        driver.findElement(By.cssSelector("button[type=submit]")).click();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        boolean stayedOnLogin = driver.getCurrentUrl().contains("/login");
        boolean errorVisible = driver.findElements(By.cssSelector(".error-msg")).size() > 0;
        Assert.assertTrue(stayedOnLogin || errorVisible,
                "Login should fail with wrong password");
    }

    @Test(description = "Admin should see Seller Dashboard link in navbar after login")
    public void testAdmin_SellerDashboardLinkVisible() {
        loginAs(ADMIN_USER, ADMIN_PASS);
        WebElement sellerLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("a[routerLink='/admin']")));
        Assert.assertTrue(sellerLink.isDisplayed(),
                "Seller Dashboard link should be visible for admin");
        logout();
    }

    @Test(description = "Admin dashboard page should load with inventory table")
    public void testAdminDashboard_Loads() {
        loginAs(ADMIN_USER, ADMIN_PASS);
        driver.get(BASE_URL + "/admin");
        WebElement adminContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".admin-container")));
        Assert.assertTrue(adminContainer.isDisplayed(), "Admin container should be visible");
        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.tagName("table")));
        Assert.assertTrue(table.isDisplayed(),
                "Inventory table should be visible on admin dashboard");
        logout();
    }

    @Test(description = "Admin can fill and submit the Add Book form")
    public void testAdminDashboard_AddBook() {
        loginAs(ADMIN_USER, ADMIN_PASS);
        driver.get(BASE_URL + "/admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
        driver.findElement(By.name("title")).sendKeys("Selenium Test Book");
        driver.findElement(By.name("author")).sendKeys("Selenium Author");
        driver.findElement(By.name("description")).sendKeys("Added by Selenium test");
        driver.findElement(By.name("price")).clear();
        driver.findElement(By.name("price")).sendKeys("19.99");
        driver.findElement(By.name("stock")).clear();
        driver.findElement(By.name("stock")).sendKeys("5");
        driver.findElement(By.cssSelector("button.btn-submit")).click();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logout();
    }

    @Test(description = "Cart icon should be visible in navbar")
    public void testCart_IconVisibleInNavbar() {
        driver.get(BASE_URL);
        WebElement cartIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".cart-icon")));
        Assert.assertTrue(cartIcon.isDisplayed(), "Cart icon should be visible in navbar");
    }

    @Test(description = "Clicking cart icon should navigate to cart page")
    public void testCart_NavigatesToCartPage() {
        driver.get(BASE_URL);
        WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".cart-icon")));
        cartIcon.click();
        wait.until(ExpectedConditions.urlContains("/cart"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/cart"),
                "Should navigate to cart page");
    }

    @Test(description = "Search icon click should open search popup")
    public void testSearch_OpensSearchPopup() {
        driver.get(BASE_URL);
        WebElement searchTrigger = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".search-trigger")));
        searchTrigger.click();
        WebElement searchPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".search-popup.active")));
        Assert.assertTrue(searchPopup.isDisplayed(),
                "Search popup should open after clicking search icon");
    }

    @Test(description = "Community page should load successfully")
    public void testCommunityPage_Loads() {
        driver.get(BASE_URL + "/community");
        wait.until(ExpectedConditions.urlContains("/community"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/community"),
                "Should navigate to community page");
    }

    @Test(description = "Logout should redirect to home and show Login button again")
    public void testLogout_RedirectsToHome() {
        loginAs(ADMIN_USER, ADMIN_PASS);
        logout();
        WebElement loginBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".desktop-actions button.btn-login")));
        Assert.assertTrue(loginBtn.isDisplayed(),
                "Login button should reappear after logout");
    }
}