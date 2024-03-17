package Uwindsor;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WordCompletion {
	public static List<String> findNearestWords(String input) {
		List<String> nearestWords = new ArrayList<>();
        int minDistance = Integer.MAX_VALUE;

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/locations.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                int distance = EditDistance.calculateEditDistance(input, line.trim());

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestWords.clear(); // Clear previous words since a new minimum is found
                    if (!nearestWords.contains(line.trim())) {
                        // If it doesn't contain the word, then add it
                        nearestWords.add(line.trim());
                    }
                } else if (distance == minDistance) {
                    if (!nearestWords.contains(line.trim())) {
                        // If it doesn't contain the word, then add it
                        nearestWords.add(line.trim());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (nearestWords.size() == 1 && nearestWords.get(0).equals(input)) {
            return new ArrayList<>();
        }
        
        return nearestWords;
    }
}
