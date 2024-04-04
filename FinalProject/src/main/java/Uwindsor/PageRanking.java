package Uwindsor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class PageRanking {
    static void displayPageRanking(String filePath, String keyword) throws IOException {
        try {
            PriorityQueue<CarTypeCount> maxHeap = getPageRanking(filePath, keyword);

            // Print car types with counts in descending order
            System.out.println("\n******************************");
            System.out.println("Ranking According to " + keyword + ": ");
            System.out.println("******************************");
            while (!maxHeap.isEmpty()) {
                CarTypeCount carTypeCount = maxHeap.poll();
                System.out.println(carTypeCount.carType + ": " + carTypeCount.count);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the files: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static PriorityQueue<CarTypeCount> getPageRanking(String filePath, String targetTextFile) throws IOException {
        Map<String, Integer> countMap = new HashMap<>();

        File directory = new File(filePath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith(targetTextFile)) {
                                String carType = line.substring(line.indexOf(":") + 2);
                                countMap.put(carType, countMap.getOrDefault(carType, 0) + 1);
                            }
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid directory or directory is empty: " + filePath);
        }

        PriorityQueue<CarTypeCount> maxHeap = new PriorityQueue<>((a, b) -> b.count - a.count);
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            maxHeap.add(new CarTypeCount(entry.getKey(), entry.getValue()));
        }

        return maxHeap;
    }

    public static class CarTypeCount {
        String carType;
        int count;

        CarTypeCount(String carType, int count) {
            this.carType = carType;
            this.count = count;
        }
    }
}
