package Uwindsor;
import java.util.Scanner;
import java.util.Set;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to Wise Wheels Rentals!");
        
        while (true) {
            displayMenu();
            String choice = scanner.nextLine().toLowerCase();
            
            switch (choice) {
	            case "1":
	                performWebCrawler();
	                break;
	            case "2":
	                performTop10CheapestDeals();
	                break;
	            case "3":
	                searchForCar();
	            case "4":
	            	searchHistory();
	                break;
	            case "5":
	            	invertedIndex();
	                break;
	            case "exit":
	                System.out.println("Exiting program. Thank you for using Wise Wheels Rentals!");
	                return;
	            default:
	                System.out.println("Invalid choice. Please select a valid option or type 'exit' to quit.");
            }
        }
    }
    
    private static void displayMenu() {
        System.out.println("\nSelect an option:");
        System.out.println("1. Web Crawler");
        System.out.println("2. Find Top 10 Cheapest Deals");
        System.out.println("3. Search for Car in Location");
        System.out.println("4. Add a keyWord into the History");
        System.out.println("5. Search for keyWord into the Inverted Indexing");
        System.out.println("Type 'exit' to quit");
        System.out.print("Enter your choice: ");
    }

    private static void performWebCrawler() {
    	// Get location from user
        String location = getLocation();

        // Get start date from user20
        LocalDate startDate = getStartDate();

        // Get end date from user
        LocalDate endDate = getEndDate(startDate);

        // Call web crawler class to fetch data based on inputs
        WebCrawler crawler = new WebCrawler(location, startDate, endDate);
        crawler.startCrawling(location, startDate, endDate);
    }

    private static void performTop10CheapestDeals() {
    	
    }

    private static void searchForCar() {
        // Implement searching for car in location functionality
        System.out.println("Searching for Car in Location...");
    }
    
    private static String getLocation() {
        String location;
        while (true) {
            System.out.print("\nEnter a pick-up location: ");
            location = scanner.nextLine();

            // Check if the location contains any special characters
            if (!location.matches("^[a-zA-Z]+$")) {
                System.out.println("Special characters and Numbers are not allowed. Please enter a valid location.");
            } else {
                System.out.println("Valid location. Proceeding...");
                break;
            }
        }

        List<String> nearestWords = WordCompletion.findNearestWords(location);

        if (!nearestWords.isEmpty()) {
            System.out.println("\nDid you mean one of the following?");
            for (String word : nearestWords) {
                System.out.println("- " + word);
            }

            System.out.print("(Type the correct word or 'no' to enter a new one): ");
            String userResponse = scanner.nextLine();

            if (!userResponse.equalsIgnoreCase("no") && nearestWords.contains(userResponse)) {
                System.out.println("Location set to: " + userResponse);
                return userResponse;
            } else {
                System.out.println("Please enter the correct spelling of the location.");
                return getLocation();
            }
        } else {
            return location;
        }
    }

    private static LocalDate getStartDate() {
        LocalDate startDate;
        while (true) {
            startDate = LocalDate.now().plusDays(1); // Set default start date as tomorrow

            System.out.print("\nEnter start date (yyyy-MM-dd) for All Urls (Press Enter for default): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Defaulting to tomorrow's date: " + startDate);
                break;
            }

            try {
                startDate = LocalDate.parse(input);
                if (startDate.isBefore(LocalDate.now().plusDays(1))) { // Ensure start date is not today or before
                    System.out.println("Invalid date. Please enter a future date.");
                } else {
                    System.out.println("Valid date format. Proceeding...");
                    break;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please enter a valid date.");
            }
        }
        return startDate;
    }

    private static LocalDate getEndDate(LocalDate startDate) {
        LocalDate endDate;
        while (true) {
            endDate = startDate.plusDays(1); // Set default end date as day after tomorrow

            System.out.print("\nEnter end date (yyyy-MM-dd) for All Urls (Press Enter for default): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Defaulting to day after tomorrow's date: " + endDate);
                break;
            }

            try {
                endDate = LocalDate.parse(input);
                if (endDate.isBefore(LocalDate.now().plusDays(2))) { // Ensure end date is not before day after tomorrow
                    System.out.println("Invalid date. Please enter a future date.");
                } else if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
                    System.out.println("End date should be greater than the start date. Please enter a valid end date.");
                } else {
                    System.out.println("Valid date format. Proceeding...");
                    break;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please enter a valid date.");
            }
        }
        return endDate;
    }
    
    private static void searchHistory() {
        SearchFrequencyMap searchFrequencyMap = new SearchFrequencyMap("src/main/resources/CarRentalData/SearchKeywordHistory.txt");
        String keyword;

        System.out.println("Enter a keyword (or type 'quit' to exit):");
//        while (true) {
//            keyword = scanner.nextLine();
//            if (keyword.equalsIgnoreCase("quit")) {
//                break;
//            } else {
//                searchFrequencyMap.updateSearchFrequency(keyword);
//            }
//        }
        keyword = scanner.nextLine();
        searchFrequencyMap.updateSearchFrequency(keyword);
        
        searchFrequencyMap.displaySearchFrequency();
    }
    
    private static void invertedIndex() {
        InvertedIndex invertedIndex = new InvertedIndex();
        File directory = new File("src/main/resources/CarRentalData");
        String outputFilePath = "src/main/resources/CarRentalData/InvertedIndexTable.txt";

        invertedIndex.indexFiles(directory);

        System.out.print("Enter keywords to search (separated by spaces): ");
        String inputKeywords = scanner.nextLine();

        String[] keywords = inputKeywords.split("\\s+");

        Set<String> filesWithKeywords = invertedIndex.searchMultipleKeywords(keywords);
        if (!filesWithKeywords.isEmpty()) {
            System.out.println("Files containing the keywords:");
            for (String fileName : filesWithKeywords) {
                System.out.println(fileName);
            }
        } else {
            System.out.println("No files found containing the specified keywords.");
        }
    }
    
}
