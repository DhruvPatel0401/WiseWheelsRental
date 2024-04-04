package Uwindsor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class WebCrawler {
	private final WebDriver driver;
    private final WebDriverWait wait;
    private final String location;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public WebCrawler(String location, LocalDate startDate, LocalDate endDate) {
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        
        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("--headless");
        String user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36";
        chromeOptions.addArguments("user-agent=" + user_agent);

        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        driver = new ChromeDriver(chromeOptions);
        driver.manage().window().setSize(new Dimension(1080, 800));
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void startCrawling(String location, LocalDate startDate, LocalDate endDate) {
        try {
        	// Delete all previous files before crawling starts
        	deletePreviousFiles();
        	
            String[] websites = {"carRentals", "expedia", "orbitz"};
            for (String website : websites) {
                String url = constructURL(website, location, startDate, endDate);
                driver.get(url);
                Thread.sleep(5000); // Adding a wait for page load (adjust if necessary)

                switch (website) {
                    case "carRentals":
                        extractCarDetailsCarRentals();
                        break;
                    case "expedia":
                        extractCarDetailsExpedia();
                        break;
                    case "orbitz":
                        extractCarDetailsOrbitz();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
    
    private void deletePreviousFiles() {
        File folder = new File("src/main/resources/CarRentalData");
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }
    
    private String constructURL(String website, String location, LocalDate startDate, LocalDate endDate) {
        // Construct URL based on the website and input parameters
        String encodedLocation = location.replace(" ", "%20");
        String startDateStr = startDate.toString();
        String endDateStr = endDate.toString();

        switch (website) {
            case "carRentals":
                return String.format("https://www.carrentals.com/carsearch?locn=%s&date1=%s&date2=%s&", encodedLocation, startDateStr, endDateStr);
            case "expedia":
                return String.format("https://www.expedia.com/carsearch?locn=%s&date1=%s&date2=%s", encodedLocation, startDateStr, endDateStr);
            case "orbitz":
                return String.format("https://www.orbitz.com/carsearch?locn=%s&date1=%s&date2=%s", encodedLocation, startDateStr, endDateStr);
            default:
                throw new IllegalArgumentException("Unsupported website: " + website);
        }
    }

    private void extractCarDetailsCarRentals() {
    	// Click the "Load More" button until it's not found
        while (true) {
            try {
                WebElement showMoreButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("paginationShowMoreBtn")));
                showMoreButton.click();
            } catch (Exception e) {
                break; // Exit the loop if no more button found
            }
        }

        List<WebElement> carOfferCards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='offer-cards-list']//li[@class='offer-card-desktop']")));
        int carCount = 1;
        
        if (carOfferCards.isEmpty()) {
            System.out.println("No cars available for the day");
        } else {

            for (WebElement carOfferCard : carOfferCards) {

                WebElement carTypeElement = carOfferCard.findElement(By.cssSelector("span.uitk-spacing.text-attribute.uitk-spacing-padding-inlinestart-two.uitk-spacing-padding-inlineend-three[aria-hidden='false']"));
                String carType = carTypeElement.getText().trim();

                WebElement carCapacityElement = carOfferCard.findElement(By.cssSelector("span.uitk-spacing.text-attribute.uitk-spacing-padding-inlinestart-two.uitk-spacing-padding-inlineend-three"));
                String carCapacity = carCapacityElement.getText().trim();
                
                WebElement carPriceElement = carOfferCard.findElement(By.cssSelector("span.per-day-price"));
                String carPrice = carPriceElement.getText().trim();
                
                WebElement carTotalPriceElement = carOfferCard.findElement(By.cssSelector("span.total-price"));
                String carTotalPrice = carTotalPriceElement.getText().trim();

                WebElement carSizeElement = carOfferCard.findElement(By.xpath(".//h3[@aria-label]"));
                String carSize = carSizeElement.getText().trim();

                WebElement carNameElement = carOfferCard.findElement(By.cssSelector("div.uitk-text.uitk-type-300.uitk-text-default-theme.uitk-spacing.uitk-spacing-margin-blockend-one"));
                String carName = carNameElement.getText().trim();
                carName = carName.replace(" or similar", "");

                WebElement carPickUpElement = carOfferCard.findElement(By.cssSelector("div#location-sub-info"));
                String carPickUp = carPickUpElement.getText().trim();
                carPickUp = carPickUp.replace("\n", ", ");
                
                WebElement vendorElement = carOfferCard.findElement(By.cssSelector("img.vendor-logo"));
                String vendor = vendorElement.getAttribute("alt");
                
                WebElement ratingElement;
                String ratingText;

                try {
                    ratingElement = carOfferCard.findElement(By.cssSelector("div.uitk-spacing.vendor-logo-container span.uitk-text.text-attribute"));
                    ratingText = ratingElement.getText().trim().split(" ")[0];
                } catch (NoSuchElementException e) {
                    ratingText = "No ratings";
                }
            
	            try (FileWriter file = new FileWriter("src/main/resources/CarRentalData/CarRentals" + carCount + ".txt")) {
	            	// Write car details to the file
	                file.write("Car Name: " + carName + "\n");
	                file.write("Car Type: " + carType + "\n");
	                file.write("Max Passengers: " + carCapacity + "\n");
	                file.write("Car Size: " + carSize + "\n");
	                file.write("Car Price per Day: " + carPrice + "\n");
	                file.write("Car Total Price: " + carTotalPrice + "\n");
	                file.write("Vendor Name: " + vendor + "\n");
	                file.write("Ratings: " + ratingText + "\n");
	                file.write("Available at: expedia.com" + "\n");

	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	
	            carCount++;
            }
        }
    }
    
    private void extractCarDetailsExpedia() {
    	// Click the "Load More" button until it's not found
        while (true) {
            try {
                WebElement showMoreButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("paginationShowMoreBtn")));
                showMoreButton.click();
            } catch (Exception e) {
                break; // Exit the loop if no more button found
            }
        }

        List<WebElement> carOfferCards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='offer-cards-list']//li[@class='offer-card-desktop']")));
        int carCount = 1;
        
        if (carOfferCards.isEmpty()) {
            System.out.println("No cars available for the day");
        } else {

            for (WebElement carOfferCard : carOfferCards) {

                WebElement carTypeElement = carOfferCard.findElement(By.cssSelector("span.uitk-spacing.text-attribute.uitk-spacing-padding-inlinestart-two.uitk-spacing-padding-inlineend-three[aria-hidden='false']"));
                String carType = carTypeElement.getText().trim();

                WebElement carCapacityElement = carOfferCard.findElement(By.cssSelector("span.uitk-spacing.text-attribute.uitk-spacing-padding-inlinestart-two.uitk-spacing-padding-inlineend-three"));
                String carCapacity = carCapacityElement.getText().trim();
                
                WebElement carPriceElement = carOfferCard.findElement(By.cssSelector("span.per-day-price"));
                String carPrice = carPriceElement.getText().trim();
                
                WebElement carTotalPriceElement = carOfferCard.findElement(By.cssSelector("span.total-price"));
                String carTotalPrice = carTotalPriceElement.getText().trim();

                WebElement carSizeElement = carOfferCard.findElement(By.xpath(".//h3[@aria-label]"));
                String carSize = carSizeElement.getText().trim();

                WebElement carNameElement = carOfferCard.findElement(By.cssSelector("div.uitk-text.uitk-type-300.uitk-text-default-theme.uitk-spacing.uitk-spacing-margin-blockend-one"));
                String carName = carNameElement.getText().trim();
                carName = carName.replace(" or similar", "");

                WebElement carPickUpElement = carOfferCard.findElement(By.cssSelector("div#location-sub-info"));
                String carPickUp = carPickUpElement.getText().trim();
                carPickUp = carPickUp.replace("\n", ", ");
                
                WebElement vendorElement = carOfferCard.findElement(By.cssSelector("img.vendor-logo"));
                String vendor = vendorElement.getAttribute("alt");
                
                WebElement ratingElement;
                String ratingText;

                try {
                    ratingElement = carOfferCard.findElement(By.cssSelector("div.uitk-spacing.vendor-logo-container span.uitk-text.text-attribute"));
                    ratingText = ratingElement.getText().trim().split(" ")[0];
                } catch (NoSuchElementException e) {
                    ratingText = "No ratings";
                }
            
	            try (FileWriter file = new FileWriter("src/main/resources/CarRentalData/Expedia" + carCount + ".txt")) {
	            	// Write car details to the file
	                file.write("Car Name: " + carName + "\n");
	                file.write("Car Type: " + carType + "\n");
	                file.write("Max Passengers: " + carCapacity + "\n");
	                file.write("Car Size: " + carSize + "\n");
	                file.write("Car Price per Day: " + carPrice + "\n");
	                file.write("Car Total Price: " + carTotalPrice + "\n");
	                file.write("Vendor Name: " + vendor + "\n");
	                file.write("Ratings: " + ratingText + "\n");
	                file.write("Available at: expedia.com" + "\n");

	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	
	            carCount++;
            }
        }
    }

    private void extractCarDetailsOrbitz() {
    	// Click the "Load More" button until it's not found
        while (true) {
            try {
                WebElement showMoreButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("paginationShowMoreBtn")));
                showMoreButton.click();
            } catch (Exception e) {
                break; // Exit the loop if no more button found
            }
        }

        List<WebElement> carOfferCards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='offer-cards-list']//li[@class='offer-card-desktop']")));
        int carCount = 1;
        
        if (carOfferCards.isEmpty()) {
            System.out.println("No cars available for the day");
        } else {

            for (WebElement carOfferCard : carOfferCards) {

                WebElement carTypeElement = carOfferCard.findElement(By.cssSelector("span.uitk-spacing.text-attribute.uitk-spacing-padding-inlinestart-two.uitk-spacing-padding-inlineend-three[aria-hidden='false']"));
                String carType = carTypeElement.getText().trim();

                WebElement carCapacityElement = carOfferCard.findElement(By.cssSelector("span.uitk-spacing.text-attribute.uitk-spacing-padding-inlinestart-two.uitk-spacing-padding-inlineend-three"));
                String carCapacity = carCapacityElement.getText().trim();
                
                WebElement carPriceElement = carOfferCard.findElement(By.cssSelector("span.per-day-price"));
                String carPrice = carPriceElement.getText().trim();
                
                WebElement carTotalPriceElement = carOfferCard.findElement(By.cssSelector("span.total-price"));
                String carTotalPrice = carTotalPriceElement.getText().trim();

                WebElement carSizeElement = carOfferCard.findElement(By.xpath(".//h3[@aria-label]"));
                String carSize = carSizeElement.getText().trim();

                WebElement carNameElement = carOfferCard.findElement(By.cssSelector("div.uitk-text.uitk-type-300.uitk-text-default-theme.uitk-spacing.uitk-spacing-margin-blockend-one"));
                String carName = carNameElement.getText().trim();
                carName = carName.replace(" or similar", "");

                WebElement carPickUpElement = carOfferCard.findElement(By.cssSelector("div#location-sub-info"));
                String carPickUp = carPickUpElement.getText().trim();
                carPickUp = carPickUp.replace("\n", ", ");
                
                WebElement vendorElement = carOfferCard.findElement(By.cssSelector("img.vendor-logo"));
                String vendor = vendorElement.getAttribute("alt");
                
                WebElement ratingElement;
                String ratingText;

                try {
                    ratingElement = carOfferCard.findElement(By.cssSelector("div.uitk-spacing.vendor-logo-container span.uitk-text.text-attribute"));
                    ratingText = ratingElement.getText().trim().split(" ")[0];
                } catch (NoSuchElementException e) {
                    ratingText = "No ratings";
                }
            
	            try (FileWriter file = new FileWriter("src/main/resources/CarRentalData/Orbitz" + carCount + ".txt")) {
	            	// Write car details to the file
	                file.write("Car Name: " + carName + "\n");
	                file.write("Car Type: " + carType + "\n");
	                file.write("Max Passengers: " + carCapacity + "\n");
	                file.write("Car Size: " + carSize + "\n");
	                file.write("Car Price per Day: " + carPrice + "\n");
	                file.write("Car Total Price: " + carTotalPrice + "\n");
	                file.write("Vendor Name: " + vendor + "\n");
	                file.write("Ratings: " + ratingText + "\n");
	                file.write("Available at: orbitz.com" + "\n");

	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	
	            carCount++;
            }
        }
    }
    
}
