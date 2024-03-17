package Uwindsor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class WebCrawler {
    private WebDriver driver;
    private WebDriverWait wait;

    public WebCrawler(String location, LocalDate startDate, LocalDate endDate) {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        startCrawling(location, startDate, endDate);
    }

    private void startCrawling(String location, LocalDate startDate, LocalDate endDate) {
        try {
            // Construct the URL based on input parameters
            String url = constructURL(location, startDate, endDate);
            driver.get(url);

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

            if (carOfferCards.isEmpty()) {
                System.out.println("No cars available for the day");
            } else {
                JSONArray jsonArray = new JSONArray();

                for (WebElement carOfferCard : carOfferCards) {
                    JSONObject jsonObject = new JSONObject();

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


                    jsonObject.put("Car Type", carType);
                    jsonObject.put("Car Capacity", carCapacity);
                    jsonObject.put("Car Size", carSize);
                    jsonObject.put("Car Name", carName);
                    jsonObject.put("Car PickUp Location", carPickUp);
                    jsonObject.put("Car Price per day", carPrice);
                    jsonObject.put("Total Car Price", carTotalPrice);
                    jsonObject.put("Vendor", vendor);
                    jsonObject.put("Ratings", ratingText);

                    jsonArray.put(jsonObject);
                }

                // Write JSON array to file
                try (FileWriter file = new FileWriter("src/main/resources/output.json")) {
                    file.write(jsonArray.toString());
                    System.out.println("Successfully wrote JSON object to file");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            driver.quit();
        }
    }

    private String constructURL(String location, LocalDate startDate, LocalDate endDate) {
        // Format LocalDate to String
        String startDateStr = startDate.format(DateTimeFormatter.ofPattern("M/dd/yyyy"));
        String endDateStr = endDate.format(DateTimeFormatter.ofPattern("M/dd/yyyy"));

        // Construct the URL with dynamic parameters
        return "https://www.carrentals.com/carsearch?date1=" + startDateStr + "&date2=" + endDateStr + "&locn=" + location + "&rfrr=Homepage";
    }
}
