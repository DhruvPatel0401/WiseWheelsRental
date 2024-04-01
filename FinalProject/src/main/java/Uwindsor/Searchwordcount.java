package Uwindsor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Searchwordcount {
    private static final String DIRECTORY_PATH = "src\\main\\resources\\CarRentalData";

    public String[] getInputWordsFromUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter words to search (separated by spaces): ");
        return scanner.nextLine().split("\\s+");
    }

    public  Map<String, Integer> searchWordsInFiles(String[] searchWords) throws IOException {
        Map<String, Integer> fileWordCountMap = new HashMap<>();
        InvertedIndex invertedIndex = new InvertedIndex();
        
        Set<String> fileNames = invertedIndex.multipledatasearch(searchWords); // Get the files from inverted index
        for (String fileName : fileNames) {
            String filePath = DIRECTORY_PATH + "/" + fileName;
            List<String> lines = readLinesFromFile(filePath);
            int count = getCountOfMatchingWords(lines, searchWords);
                fileWordCountMap.put(fileName, count);
            }
        
            return fileWordCountMap;
    }

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

    public static int getCountOfMatchingWords(List<String> lines, String[] searchWords) {
        int count = 0;
        for (String word : searchWords) {
            for (String line : lines) {
                if (line.contains(word)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    public void printMatchingFiles(Map<String, Integer> fileWordCountMap) 
    {
        if (fileWordCountMap.isEmpty()) {
            System.out.println("No crawled text files contain the provided data.");
        } else {
            System.out.println("Crawled text files with matching data:");
            for (Map.Entry<String, Integer> entry : fileWordCountMap.entrySet()) {
                System.out.println(entry.getKey() + " - count " + entry.getValue());
            }
        }
    }
}
