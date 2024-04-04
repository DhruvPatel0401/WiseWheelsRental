package Uwindsor;

import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        displayWiseWheelsRental();
        System.out.println("Get ready to drive into your next adventure with Wise Wheels Rentals!");

        while (true) {
            displayMenu();
            String choice = scanner.nextLine().toLowerCase();

            switch (choice) {
                case "1":
                    performWebCrawler();
                    break;
                case "2":
                    performPageRanking();
                    break;
                case "3":
                    searchForCar();
                    break;
                case "4":
                    searchHistory();
                    break;
                case "5":
                    Searchwordcount();
                    break;

                case "exit":
                    System.out.println("Exiting program. Thank you for using Wise Wheels Rentals!");
                    displayWiseWheelsRental();
                    return;
                default:
                    System.out.println("Invalid choice. Please select a valid option or type 'exit' to quit.");
            }
        }
    }

    public static void displayWiseWheelsRental() {
        System.out.println("\r\n"
                + "╦ ╦┬┌─┐┌─┐  ╦ ╦┬ ┬┌─┐┌─┐┬  ┌─┐  ╦═╗┌─┐┌┐┌┌┬┐┌─┐┬  \r\n"
                + "║║║│└─┐├┤   ║║║├─┤├┤ ├┤ │  └─┐  ╠╦╝├┤ │││ │ ├─┤│  \r\n"
                + "╚╩╝┴└─┘└─┘  ╚╩╝┴ ┴└─┘└─┘┴─┘└─┘  ╩╚═└─┘┘└┘ ┴ ┴ ┴┴─┘\r\n"
                + "");
    }

    private static void displayMenu() {
        System.out.println("\n*************************************");
        System.out.println("             𝕄𝕒𝕚𝕟 𝕄𝕖𝕟𝕦                ");
        System.out.println("*************************************");
        System.out.println("1. Get Latest Car Details");
        System.out.println("2. To simplify your experience, choose this option to effortlessly rank your data");
        System.out.println("3. Search for Car");
        System.out.println("4. Display the History");
        System.out.println("5. Search for the car to verify see its frequency count");
        System.out.println("Type 'exit' to quit");
        System.out.print("What would you like to do next? Enter your choice: ");
    }

    private static void performWebCrawler() {
        // Get location from user
        String location = getLocation();

        // Get start date from user20
        LocalDate startDate = getStartDate();

        // Get end date from user
        LocalDate endDate = getEndDate(startDate);

        System.out.println("Hang tight! We’re fetching the most recent data for you.");

        // Call web crawler class to fetch data based on inputs
        WebCrawler crawler = new WebCrawler(location.toLowerCase(), startDate, endDate);
        crawler.startCrawling(location, startDate, endDate);

        System.out.println("Appreciate your patience! The most up-to-date data is now ready for you.");

        CreateInvertedIndexTable();
    }

    private static void performPageRanking() {
        while (true) {
            System.out.println("\nReady to rank? Please choose your preferred criteria:");
            System.out.println("1. Car Type");
            System.out.println("2. Car Model");
            System.out.println("3. To go back to main menu");

            String keyword;
            // Read user input
            String preferenceInput = scanner.nextLine();

            // Handle user preference
            switch (preferenceInput) {
                case "1":
                    keyword = "Car Size";
                    break;
                case "2":
                    keyword = "Car Name";
                    break;
                case "3":
                    return;
                default:
                    System.out.println(
                            "Invalid input. Please enter either '1' for Vehicle Type, '2' for Vehicle Model, or '3' to go back to the main menu.");
                    continue;
            }

            try {
                PageRanking.displayPageRanking("src/main/resources/CarRentalData", keyword);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("There seems to be an error with the data files");
            }
        }
    }

    private static void searchForCar() {
        // Implement searching for car in location functionality
        SearchFrequencyMap searchFrequencyMap = new SearchFrequencyMap(
                "src/main/resources/SearchKeywordHistory.txt");
        InvertedIndex invertedIndex = new InvertedIndex();
        System.out.print("\nEnter a Car name: ");
        String searchData = scanner.nextLine();
        String[] keywords = searchData.split("\\s+");
        for (String keyword : keywords) {
            try {
                if (keyword.matches("[0-9]+")) {
                    throw new IllegalArgumentException("Only numbers are not allowed.");
                }

                Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\s]");
                Matcher matcher = pattern.matcher(keyword);
                if (matcher.find()) {
                    throw new IllegalArgumentException("Special character is not allowed.");
                }

                searchFrequencyMap.dataUpdateInTheFile(keyword);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }
        }
        invertedIndex.printBeforeSearchingData(keywords);
    }

    private static String getLocation() {
        String location;
        while (true) {
            System.out.print("\nEnter a pick-up location: ");
            location = scanner.nextLine();

            // Check if the location contains only alphabetic characters, spaces, and
            // hyphens
            if (!location.matches("^[a-zA-Z\\s-]+$")) {
                System.out.println("Special characters and numbers are not allowed. Please enter a valid location.");
            } else {
                break;
            }
        }

        try {
            List<String> nearestWords = WordCompletion.findNearestWords(location);
            if (!nearestWords.isEmpty()) {
                nearestWords.remove(location.toLowerCase());
                if (nearestWords.isEmpty()) {
                    System.out.println("Location set to: " + location);
                    return location;
                }

                System.out.println("\nDid you mean one of the following?");
                for (String word : nearestWords) {
                    System.out.println("\u001B[1m" + "- " + word);
                }
                System.out.print("\u001B[0m");

                System.out.print("Type the correct word or 'no' to enter a new one: ");
                String userResponse = scanner.nextLine();

                if (!userResponse.equalsIgnoreCase("no") && nearestWords.contains(userResponse.toLowerCase())) {
                    System.out.println("Location set to: " + userResponse);
                    return userResponse;
                } else {
                    System.out.println("Incorrect location. Please enter the location again.");
                    return getLocation();
                }
            } else {
                List<String> correctedLocation = SpellChecker.correctedWord(location,
                        "src/main/resources/locations.txt");
                if (!correctedLocation.equals(location)) {
                    System.out.println("\nDid you mean one of the following?");
                    for (String word : correctedLocation) {
                        System.out.println("\u001B[1m" + "- " + word);
                    }
                    System.out.print("\u001B[0m");

                    System.out.print("Type the correct word or 'no' to enter a new one: ");
                    String userResponse = scanner.nextLine();

                    if (!userResponse.equalsIgnoreCase("no")
                            && correctedLocation.contains(userResponse.toLowerCase())) {
                        System.out.println("Location set to: " + userResponse);
                        return userResponse;
                    } else {
                        System.out.println("Incorrect location. Please enter the location again.");
                        return getLocation();
                    }

                } else {
                    System.out.println("Incorrect location. Please try again.");
                    return getLocation();
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while processing your request: " + e.getMessage());
            return getLocation();
        }
    }

    private static LocalDate getStartDate() {
        LocalDate startDate;
        while (true) {
            startDate = LocalDate.now().plusDays(1); // Set default start date as tomorrow

            System.out.print("\nEnter pick-up date (yyyy-MM-dd) (Press Enter for default): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Pick-up Date set to: " + startDate);
                break;
            }

            try {
                startDate = DateValidator.validateDateInput(input);
                System.out.println("Valid pick-up date . Proceeding...");
                break;
            } catch (DateTimeParseException e) {
                System.out.println(e.getMessage());
            }
        }
        return startDate;
    }

    private static LocalDate getEndDate(LocalDate startDate) {
        LocalDate endDate;
        while (true) {
            endDate = startDate.plusDays(1); // Set default end date as day after tomorrow

            System.out.print("\nEnter drop-off date (yyyy-MM-dd) (Press enter for default): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Drop-off Date set to: " + endDate);
                break;
            }

            try {
                endDate = DateValidator.validateDateInput(input);
                if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
                    throw new DateTimeParseException(
                            "Drop-off date should be greater than the Pick-up date. Please enter a valid drop-off date.",
                            input, 0);
                }
                System.out.println("Valid drop-off date. Proceeding...");
                break;
            } catch (DateTimeParseException e) {
                System.out.println(e.getMessage());
            }
        }
        return endDate;
    }

    private static void searchHistory() {
        SearchFrequencyMap searchFrequencyMap = new SearchFrequencyMap(
                "src/main/resources/SearchKeywordHistory.txt");
        searchFrequencyMap.filedataDisplaying();
    }

    private static void invertedIndex() {
        InvertedIndex invertedIndex = new InvertedIndex();
        System.out.print("Enter keywords to search (separated by spaces): ");
        String inputKeywords = scanner.nextLine();

        String[] keywords = inputKeywords.split("\\s+");

        Set<String> filesWithKeywords = invertedIndex.multipledatasearch(keywords);
        if (!filesWithKeywords.isEmpty()) {
            System.out.println("Files containing the keywords:");
            for (String fileName : filesWithKeywords) {
                System.out.println(fileName);
            }
        } else {
            System.out.println("No files found containing the specified keywords.");
        }
    }

    private static void CreateInvertedIndexTable() {
        InvertedIndex invertedIndex = new InvertedIndex();
        File directory = new File("src/main/resources/CarRentalData");
        invertedIndex.DataIndexFile(directory);
    }

    private static void Searchwordcount() throws IOException {
        Searchwordcount countdata = new Searchwordcount();

        String[] searchWords = countdata.getInputWordsFromUser();
        Map<String, Integer> fileWordCountMap = countdata.searchWordsInFiles(searchWords);
        countdata.printMatchingFiles(fileWordCountMap);
    }
}
