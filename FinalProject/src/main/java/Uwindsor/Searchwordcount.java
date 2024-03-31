package Uwindsor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Searchwordcount {
     // Define the directory path where your crawled data files are stored
    private static final String DIRECTORY_PATH = "C:\\Users\\Aditi\\Documents\\WiseWheelsRental\\FinalProject\\src\\main\\resources\\CarRentalData";

    public static void main(String[] args) throws IOException {
        // Read the words to search from the user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter words to search (separated by spaces): ");
        String wordsToSearch = scanner.nextLine();
        String[] searchWords = wordsToSearch.split("\\s+");

        // Map to store the count of matching words found in each file
        Map<String, Integer> fileWordCountMap = new HashMap<>();

        // Read data from each crawled data file in the directory
        for (int i = 1; ; i++) {
            String filePath = DIRECTORY_PATH + "/CarRentals" + i + ".txt"; // Assume files are named CarRentals1.txt, CarRentals2.txt, etc.
            List<String> lines = readLinesFromFile(filePath);
            if (lines.isEmpty()) {
                // No more files found
                break;
            }
            int count = getCountOfMatchingWords(lines, searchWords);
            if (count > 0) {
                fileWordCountMap.put("CarRentals" + i + ".txt", count);
            }
        }

        // Print the crawled text files with matching data
        if (fileWordCountMap.isEmpty()) {
            System.out.println("No crawled text files contain the provided data.");
        } else {
            System.out.println("Crawled text files with matching data:");
            for (Map.Entry<String, Integer> entry : fileWordCountMap.entrySet()) {
                System.out.println(entry.getKey() + " - count " + entry.getValue());
            }
        }
    }

    // Method to read lines from a file and return them as a list
    private static List<String> readLinesFromFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            // Print error if file cannot be read
            e.printStackTrace();
        }
        return lines;
    }

    // Method to count matching words in the lines against the search words
    private static int getCountOfMatchingWords(List<String> lines, String[] searchWords) {
        int count = 0;
        for (String word : searchWords) {
            for (String line : lines) {
                if (line.contains(word)) {
                    count++;
                    break; // Exit the inner loop once a match is found in the line
                }
            }
        }
        return count;
    }
}


