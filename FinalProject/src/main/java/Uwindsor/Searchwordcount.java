package Uwindsor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class Searchwordcount {
    // path to the crawled data output files
    private static final String DIRECTORY_PATH = "src\\main\\resources\\InvertedIndexTable.txt";

    // get user input words separated by spaces using scanner class.
    // Used getters below which will return an Array.
    public String[] getInputWordsFromUser()
    {
        //taking user input using scanner class
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter words to search (separated by spaces): ");
        String input = scanner.nextLine();
        if (input.trim().isEmpty()) 
        {
            throw new IllegalArgumentException("Input should not be empty");
        }
        return input.split("\\s+");
    }

    // below method will search the input words taken from user and search it in filee and count occurrences
    // returns a Map containing filename and their count 
    public  Map<String, Integer> searchWordsInFiles(String[] searchWords) throws IOException {
        Map<String, Integer> fileWordCountMap = new HashMap<>();
        InvertedIndex invertedIndex = new InvertedIndex(); // create object for inverted index class
        try {
            Set<String> fileNames = invertedIndex.multipledatasearch(searchWords); // Get the files from inverted index
            for (String fileName : fileNames) {
                String filePath = getFilePath(DIRECTORY_PATH, fileName);
                List<String> lines = readLinesFromFile(filePath);
                int count = getCountOfMatchingWords(lines, searchWords);
                fileWordCountMap.put(fileName, count);
            }
        } 
        catch (IllegalArgumentException e) 
        {
            System.err.println("Error: " + e.getMessage());
        }
        return fileWordCountMap;
    }

    public static String getFilePath(String directoryPath, String fileName) 
    // Get platform-independent file path
    {
        Path filePath = Paths.get(directoryPath, fileName);
        return filePath.toString();
    }
    // below method is used to read lines from file and takes file path where inverted index files are stored
    // it will return list of lines
    public static List<String> readLinesFromFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
    //List of lines and searchWords array are passed as parameters  in below getter method
    //It will return total no. of word presence found in text files

    public static int getCountOfMatchingWords(List<String> lines, String[] searchWords) {
        int count = 0;
    StringBuilder fileContent = new StringBuilder();
    for (String line : lines) {
        fileContent.append(line).append(" "); // Append line to the file content
    }
    String fileText = fileContent.toString().toLowerCase(); // Convert to lowercase for case-insensitive matching
    for (String word : searchWords) {
        if (fileText.contains(word.toLowerCase())) { // Check if each word is present in the file text
            count++;
        }
    }
    return count;
        }
    
    // This will take fileWordCountMap value returned from searchWordsInFiles method 
    // it will print files containg matching keyword with count 
    public void printMatchingFiles(Map<String, Integer> fileWordCountMap) 
    {
        TreeMap<String, List<String>> groupedFiles = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : fileWordCountMap.entrySet()) {
            String fileName = entry.getKey();
            String groupKey = getGroupKey(fileName);
            groupedFiles.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(fileName + " - count " + entry.getValue());
        }
        System.out.println("Crawled text files with matching data:");
        for (List<String> fileList : groupedFiles.values()) {
            for (String fileInfo : fileList) {
                System.out.println(fileInfo);
            }
        }
    }
    // below getter method is used to return group key based on file name where matching keyword is found.
    private String getGroupKey(String fileName) {
        if (fileName.startsWith("CarRentals")) {
            return "CarRentals";
        } else if (fileName.startsWith("Orbitz")) {
            return "Orbitz";
        } else if (fileName.startsWith("Expedia")) {
            return "Expedia";
        }
        return "Other";
    }

}

