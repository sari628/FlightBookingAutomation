package com.flightbooking;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;

public class FlightBookingTest {
    
    private static final String APP_URL = "https://blazedemo.com/";
    private static final int WAIT_TIMEOUT = 10;
    
    private WebDriver driver;
    private WebDriverWait wait;
    private Random random;
    
    public static void main(String[] args) {
        FlightBookingTest automation = new FlightBookingTest();
        automation.runFlightBookingAutomation();
    }
    
    public void runFlightBookingAutomation() {
        try {
            setupDriver();
            navigateToApp();
            selectDepartureAndDestination();
            searchForFlights();
            selectFlight();
            fillPassengerDetails();
            completePurchase();
            verifyBookingConfirmation();
            
            System.out.println("[SUCCESS] Flight booking automation completed successfully!");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Error during flight booking automation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }
    
    private void setupDriver() {
        System.out.println("[SETUP] Setting up WebDriver...");
        WebDriverManager.chromedriver().setup();
      
        
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT));
        random = new Random();
        
        System.out.println("[SUCCESS] WebDriver setup completed");
    }
    
    private void navigateToApp() {
        System.out.println("[NAVIGATE] Navigating to Simple Travel Agency application...");
        
        driver.get(APP_URL);
        
        // Wait for page to load
        wait.until(ExpectedConditions.titleContains("BlazeDemo"));
        
        System.out.println("[SUCCESS] Successfully navigated to the application");
    }
    
    private void selectDepartureAndDestination() {
        System.out.println("[SELECT] Selecting departure and destination cities...");
        
        // Get departure dropdown
        WebElement departureDropdown = driver.findElement(By.name("fromPort"));
        Select departureSelect = new Select(departureDropdown);
        
        // Get destination dropdown
        WebElement destinationDropdown = driver.findElement(By.name("toPort"));
        Select destinationSelect = new Select(destinationDropdown);
        
        // Get all options
        List<WebElement> departureOptions = departureSelect.getOptions();
        List<WebElement> destinationOptions = destinationSelect.getOptions();
        
        // Select random departure city (excluding first option which is placeholder)
        int departureIndex = random.nextInt(departureOptions.size() - 1) + 1;
        String departureCity = departureOptions.get(departureIndex).getText();
        departureSelect.selectByIndex(departureIndex);
        
        // Select random destination city (excluding first option and departure city)
        int destinationIndex;
        String destinationCity;
        do {
            destinationIndex = random.nextInt(destinationOptions.size() - 1) + 1;
            destinationCity = destinationOptions.get(destinationIndex).getText();
        } while (destinationCity.equals(departureCity));
        
        destinationSelect.selectByIndex(destinationIndex);
        
        System.out.println("[SUCCESS] Selected departure: " + departureCity + ", destination: " + destinationCity);
    }
    
    private void searchForFlights() {
        System.out.println("[SEARCH] Searching for flights...");
        
        // Click Find Flights button
        WebElement findFlightsButton = driver.findElement(By.cssSelector("input[type='submit'][value='Find Flights']"));
        findFlightsButton.click();
        
        // Wait for flight results page
        wait.until(ExpectedConditions.titleContains("BlazeDemo - reserve"));
        
        // Verify flights are displayed
        List<WebElement> flights = driver.findElements(By.cssSelector("table tbody tr"));
        
        if (flights.isEmpty()) {
            throw new RuntimeException("No flights found for the selected route");
        }
        
        System.out.println("[SUCCESS] Found " + flights.size() + " available flights");
    }
    
    private void selectFlight() {
        System.out.println("[FLIGHT] Selecting a flight...");
        
        // Get all flight rows
        List<WebElement> flights = driver.findElements(By.cssSelector("table tbody tr"));
        
        // Select random flight
        int flightIndex = random.nextInt(flights.size());
        WebElement selectedFlight = flights.get(flightIndex);
        
        // Click "Choose This Flight" button for the selected flight
        WebElement chooseButton = selectedFlight.findElement(By.cssSelector("input[type='submit']"));
        chooseButton.click();
        
        // Wait for purchase page
        wait.until(ExpectedConditions.titleContains("BlazeDemo Purchase"));
        
        // Verify purchase page is displayed
        WebElement purchaseHeader = driver.findElement(By.tagName("h2"));
        if (!purchaseHeader.getText().contains("Your flight from")) {
            throw new RuntimeException("Purchase page not loaded correctly");
        }
        
        System.out.println("[SUCCESS] Flight selected and purchase page loaded");
    }
    
    private void fillPassengerDetails() {
        System.out.println("[DETAILS] Filling passenger and payment details...");
        
        // Personal Details
        driver.findElement(By.id("inputName")).sendKeys("Akhila");
        driver.findElement(By.id("address")).sendKeys("123 Main Street, New York, NY 10001");
        driver.findElement(By.id("city")).sendKeys("New York");
        driver.findElement(By.id("state")).sendKeys("NY");
        driver.findElement(By.id("zipCode")).sendKeys("10001");
        
        // Payment Details
        driver.findElement(By.id("creditCardNumber")).sendKeys("4111111111111111");
        driver.findElement(By.id("creditCardMonth")).sendKeys("12");
        driver.findElement(By.id("creditCardYear")).sendKeys("2027");
        driver.findElement(By.id("nameOnCard")).sendKeys("Akhila");
        
        // Remember checkbox
        WebElement rememberCheckbox = driver.findElement(By.id("rememberMe"));
        if (!rememberCheckbox.isSelected()) {
            rememberCheckbox.click();
        }
        
        System.out.println("[SUCCESS] Passenger and payment details filled");
    }
    
    private void completePurchase() {
        System.out.println("[PURCHASE] Completing flight purchase...");
        
        // Scroll to purchase button
        WebElement purchaseButton = driver.findElement(By.cssSelector("input[type='submit'][value='Purchase Flight']"));
        
        
        // Click Purchase Flight button
        purchaseButton.click();
        
        // Wait for confirmation page
        wait.until(ExpectedConditions.titleContains("BlazeDemo Confirmation"));
        
        System.out.println("[SUCCESS] Purchase completed");
    }
    
    private void verifyBookingConfirmation() {
        System.out.println("[VERIFY] Verifying booking confirmation...");
        
        // Wait a moment for page to fully load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Check for confirmation header
        WebElement confirmationHeader = driver.findElement(By.tagName("h1"));
        String headerText = confirmationHeader.getText();
        
        if (!headerText.contains("Thank you for your purchase")) {
            throw new RuntimeException("Booking confirmation not received. Expected: 'Thank you for your purchase', Found: " + headerText);
        }
        
        // Check for confirmation table
        WebElement confirmationTable = driver.findElement(By.cssSelector("table.table"));
        
        // Extract and display booking details
        List<WebElement> rows = confirmationTable.findElements(By.tagName("tr"));
        System.out.println("[INFO] Booking Confirmation Details:");
        System.out.println("==================================================");
        
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() == 2) {
                String label = cells.get(0).getText();
                String value = cells.get(1).getText();
                System.out.println(label + ": " + value);
            }
        }
        
        System.out.println("==================================================");
        System.out.println("[SUCCESS] Booking confirmation verified successfully");
    }
    

    private void cleanup() {
        System.out.println("[CLEANUP] Cleaning up resources...");
        
        if (driver != null) {
            try {
                Thread.sleep(3000); // Wait 3 seconds before closing to see final result
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error during cleanup: " + e.getMessage());
            }
        }
        
        System.out.println("[SUCCESS] Cleanup completed");
    }
}

