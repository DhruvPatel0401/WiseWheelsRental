package Uwindsor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    }
    
    // Compute Levenshtein distance between two strings
    private int levenshteinDistance(String word1, String word2) {
    	int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = minm_edits(dp[i - 1][j - 1] + NumOfReplacement(word1.charAt(i - 1), word2.charAt(j - 1)), dp[i - 1][j] + 1, dp[i][j - 1] + 1);
                }
            }
        }

        return dp[word1.length()][word2.length()];
    }
    
    static int NumOfReplacement(char c1, char c2)
    {
        return c1 == c2 ? 0 : 1;
    }
    
    static int minm_edits(int... nums)
    {
 
        return Arrays.stream(nums).min().orElse(
            Integer.MAX_VALUE);
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
    
//    public static void main(String[] args) {
//    	String wordToCheck = "wendsar";
//        String dictionaryFilePath = "src/main/resources/locations.txt";
//        
//        SpellChecker spellChecker = new SpellChecker(dictionaryFilePath);
//        PriorityQueue<String> suggestions = spellChecker.suggestCorrections(wordToCheck);
//        
//        if (suggestions.isEmpty()) {
//            System.out.println("No suggestions found for \"" + wordToCheck + "\"");
//        } else {
//            System.out.println("Suggestions for \"" + wordToCheck + "\":");
//            while (!suggestions.isEmpty()) {
//                System.out.println("- " + suggestions.poll());
//            }
//        }
//    }
    
    public static List<String> correctedWord(String input, String path) {
    	SpellChecker spellChecker = new SpellChecker(path);
        PriorityQueue<String> suggestions = spellChecker.suggestCorrections(input);
        List<String> correctedWords = new ArrayList<>();
        
        if (suggestions.isEmpty()) {
            return correctedWords;
        } else {
            while (!suggestions.isEmpty()) {
            	correctedWords.add(suggestions.poll());
            }
            return correctedWords;
        }
    }
}
