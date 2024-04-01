package Uwindsor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class SpellChecker {
    
    private ArrayList<ArrayList<String>> dictionary;
    private int[][] distanceCache;
    
    public SpellChecker(String dictionaryFilePath) {
        dictionary = new ArrayList<>();
        distanceCache = new int[100][100]; // Assuming maximum word length is 100
        for (int i = 0; i < 26; i++) {
            dictionary.add(new ArrayList<>());
        }
        loadDictionary(dictionaryFilePath);
    }
    
    // Load dictionary from a text file
    private void loadDictionary(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim().toLowerCase(); // Convert to lowercase
                int index = word.charAt(0) - 'a'; // Get the first character's index
                if (index >= 0 && index < 26) {
                    dictionary.get(index).add(word); // Add word to the corresponding list
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(dictionary);
    }
    
    // Compute Levenshtein distance between two strings
    private int levenshteinDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        
        // Check if distance is already computed and cached
        if (distanceCache[m][n] != 0) {
            return distanceCache[m][n];
        }
        
        int[][] dp = new int[m + 1][n + 1];
        
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }
        
        // Cache the computed distance
        distanceCache[m][n] = dp[m][n];
        
        return dp[m][n];
    }
    
    // Spell check a word and suggest corrections
    public PriorityQueue<String> suggestCorrections(String word) {
        PriorityQueue<String> suggestions = new PriorityQueue<>((a, b) -> levenshteinDistance(word, a) - levenshteinDistance(word, b));
        int index = word.charAt(0) - 'a'; // Get the first character's index
        if (index >= 0 && index < 26) {
            for (String dictWord : dictionary.get(index)) {
                if (levenshteinDistance(word, dictWord) <= 2) { // Adjust threshold as needed
                    suggestions.offer(dictWord);
                }
            }
        }
        return suggestions;
    }
    
    public static void main(String[] args) {
    	String wordToCheck = "winds";
        String dictionaryFilePath = "src/main/resources/locations.txt";
        
        SpellChecker spellChecker = new SpellChecker(dictionaryFilePath);
        PriorityQueue<String> suggestions = spellChecker.suggestCorrections(wordToCheck);
        
        if (suggestions.isEmpty()) {
            System.out.println("No suggestions found for \"" + wordToCheck + "\"");
        } else {
            System.out.println("Suggestions for \"" + wordToCheck + "\":");
            while (!suggestions.isEmpty()) {
                System.out.println("- " + suggestions.poll());
            }
        }
    }
    
    public static String correctedWord(String input) {
    	return "";
    }
}
