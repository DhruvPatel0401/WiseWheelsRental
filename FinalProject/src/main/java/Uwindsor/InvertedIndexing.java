package Uwindsor;

import java.io.*;
import java.util.*;

public class InvertedIndexing {
    private Map<String, List<String>> invertedIndex;

    public InvertedIndexing() {
        invertedIndex = new HashMap<>();
    }

    public void loadData(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] tokens = line.split(":");
                        if (tokens.length == 2 && tokens[0].trim().equals("Car Name")) {
                            String carName = tokens[1].trim();
                            carName = carName.toLowerCase();
                            System.out.println(carName);
                            if (!invertedIndex.containsKey(carName)) {
                                invertedIndex.put(carName, new ArrayList<>());
                            }
                            invertedIndex.get(carName).add(fileName);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String> search(String query) {
        query = query.toLowerCase();
        return invertedIndex.getOrDefault(query, Collections.emptyList());
    }

    public void saveIndexToFile() {
        try (PrintWriter writer = new PrintWriter("src/main/resources")) {
            for (Map.Entry<String, List<String>> entry : invertedIndex.entrySet()) {
                writer.print(entry.getKey() + ": ");
                writer.println(String.join(", ", entry.getValue()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

