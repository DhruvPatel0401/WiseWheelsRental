package Uwindsor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrieNode {
    private static final int ALPHABET_SIZE = 26;
    private TrieNode[] children;
    private boolean isEndOfWord;

    public TrieNode() {
        children = new TrieNode[ALPHABET_SIZE];
        isEndOfWord = false;
    }

    public void insert(String word) {
        TrieNode current = this;
        for (int i = 0; i < word.length(); i++) {
            char ch = Character.toUpperCase(word.charAt(i)); // Convert to uppercase
            int index = ch - 'A'; 
            if (index < 0 || index >= ALPHABET_SIZE) {
                // Handle non-alphabetic characters
                continue;
            }
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }
        current.isEndOfWord = true;
    }

    public boolean search(String word) {
        TrieNode current = this;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            int index = ch - 'A';
            if (current.children[index] == null) {
                return false;
            }
            current = current.children[index];
        }
        return current != null && current.isEndOfWord;
    }

    public List<String> suggestCompletions(String prefix) {
        List<String> suggestions = new ArrayList<>();
        TrieNode prefixNode = findNode(prefix);
        if (prefixNode != null) {
            collectWords(prefixNode, prefix, suggestions);
        }
        return suggestions;
    }

    private TrieNode findNode(String prefix) {
        TrieNode current = this;
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            int index = Character.toUpperCase(ch) - 'A';
            if (current.children[index] == null) {
                return null;
            }
            current = current.children[index];
        }
        return current;
    }

    private void collectWords(TrieNode node, String prefix, List<String> suggestions) {
        if (node.isEndOfWord) {
            suggestions.add(prefix.toLowerCase());
        }
        for (char i = 0; i < ALPHABET_SIZE; i++) {
            TrieNode child = node.children[i];
            if (child != null) {
                collectWords(child, prefix + (char) ('A' + i), suggestions);
            }
        }
    }
}


public class WordCompletion {
	
    public static List<String> findNearestWords(String input) {
    	String filePath = "src/main/resources/locations.txt"; // Provide the path to your text file
        buildTrie(filePath);
        
        List<String> suggestions = root.suggestCompletions(input);
        return suggestions;
    }
    
    private static TrieNode root = new TrieNode();
    
    public static void buildTrie(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toUpperCase();
                root.insert(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
