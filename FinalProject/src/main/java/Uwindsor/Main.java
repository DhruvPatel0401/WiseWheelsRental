package Uwindsor;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to Wise Wheels Rentals!");

        // Get location from user
        String location = getLocation();

        // Get start date from user
        LocalDate startDate = getStartDate();

        // Get end date from user
        LocalDate endDate = getEndDate(startDate);

        // Call web crawler class to fetch data based on inputs
        WebCrawler crawler = new WebCrawler(location, startDate, endDate);
    }

    private static String getLocation() {
        String location;
        while (true) {
            System.out.print("Enter a pick-up location: ");
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
            System.out.println("Did you mean one of the following?");
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

            System.out.print("Enter start date (yyyy-MM-dd) for All Urls (Press Enter for default): ");
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

            System.out.print("Enter end date (yyyy-MM-dd) for All Urls (Press Enter for default): ");
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
}
