package Uwindsor;

import java.io.*;
import java.util.*;

public class InvertedIndex {
	 private static final Set<String> EXCLUDED_KEYWORDS = new HashSet<>(Arrays.asList(
	            "is", "an", "the", "Car", "Name", "Type", "Max", "Passengers", "Size", "Price", 
	            "per", "Day", "Total", "Vendor", "Rating", "No", "no", "ratings", "Available", "at"
	    ));
	 
	 private static final String outputFilePath= "src/main/resources/CarRentalData/InvertedIndexTable.txt";
	 private static final String outputFilePathData = "src/main/resources/CarRentalData";

    public void indexFiles(File directory) {
        if (!directory.isDirectory()) {
            return;
        }

        Map<String, Set<String>> invertedIndex = new HashMap<>();

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                indexFiles(file);
            } else {
                // Skip files with specific names
                if (shouldSkipFile(file)) {
                    continue;
                }
                indexFile(file, invertedIndex);
            }
        }

        writeIndexToFile(invertedIndex, outputFilePath);
    }

    private boolean shouldSkipFile(File file) {
        String fileName = file.getName();
        return fileName.equals("InvertedIndexTable.txt") || fileName.equals("SearchKeywordHistory.txt");
    }

    private void indexFile(File file, Map<String, Set<String>> invertedIndex) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (!EXCLUDED_KEYWORDS.contains(word)) {
                        invertedIndex.computeIfAbsent(word, k -> new HashSet<>()).add(file.getName());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> search(String keyword, String inputFilePath) {
    	
        Map<String, Set<String>> invertedIndex = readIndexFromFile(inputFilePath);
        String lowercaseKeyword = keyword.toLowerCase(Locale.ENGLISH); // Convert keyword to lowercase
        for (Map.Entry<String, Set<String>> entry : invertedIndex.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(lowercaseKeyword)) { // Case-insensitive comparison
                return entry.getValue(); // Return files corresponding to matched keyword
            }
        }
        return Collections.emptySet();
    }

    public Set<String> searchMultipleKeywords(String[] keywords) {
        Map<String, Set<String>> invertedIndex = readIndexFromFile(outputFilePath);
        Set<String> resultFiles = new HashSet<>();

        for (String keyword : keywords) {
            String lowercaseKeyword = keyword.toLowerCase(Locale.ENGLISH); // Convert keyword to lowercase
            for (Map.Entry<String, Set<String>> entry : invertedIndex.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(lowercaseKeyword)) { // Case-insensitive comparison
                    resultFiles.addAll(entry.getValue()); // Add files corresponding to matched keyword
                    break;
                }
            }
        }
        return resultFiles;
    }

    private void writeIndexToFile(Map<String, Set<String>> invertedIndex, String outputFilePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
            for (Map.Entry<String, Set<String>> entry : invertedIndex.entrySet()) {
                String keyword = entry.getKey();
                String files = String.join(",", entry.getValue()); // Join filenames with commas
                writer.println(keyword + ":" + files); // Separate keyword and filenames with a colon
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Set<String>> readIndexFromFile(String inputFilePath) {
        Map<String, Set<String>> invertedIndex = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":"); // Split by colon
                String keyword = parts[0];
                String[] files = parts[1].split(","); // Split filenames by comma
                Set<String> fileSet = new HashSet<>(Arrays.asList(files));
                invertedIndex.put(keyword, fileSet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return invertedIndex;
    }
    public static void searchAndPrintMultipleKeywords(String[] keywords) {
        Map<String, Set<String>> invertedIndex = readIndexFromFile(outputFilePath);

        for (String keyword : keywords) {
            String lowercaseKeyword = keyword.toLowerCase(Locale.ENGLISH); // Convert keyword to lowercase
            for (Map.Entry<String, Set<String>> entry : invertedIndex.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(lowercaseKeyword)) { // Case-insensitive comparison
                    Set<String> files = entry.getValue();
                    for (String file : files) {
                        printFileContent(outputFilePathData + "/" + file); // Add file path before printing
                    }
                    break;
                }
            }
        }
    }
    public static void printFileContent(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            System.out.println("Content of file: " + fileName);
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("----------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 
 
}
