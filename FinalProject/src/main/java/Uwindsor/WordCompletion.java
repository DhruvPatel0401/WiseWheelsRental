package Uwindsor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrieNode {
    private static final int ALPHABET_SIZE = 28;
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
            int index = getIndex(ch);
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
            int index = getIndex(ch);
            if (index < 0 || index >= ALPHABET_SIZE || current.children[index] == null) {
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
            int index = getIndex(ch);
            if (index < 0 || index >= ALPHABET_SIZE || current.children[index] == null) {
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
                char ch = getChar(i);
                collectWords(child, prefix + ch, suggestions);
            }
        }
    }

    private char getChar(int index) {
        if (index == 26) {
            return ' '; // Map last index to space
        } else if (index == 27) {
            return '-'; // Map second last index to '-'
        } else {
            return (char) ('A' + index);
        }
    }
    
    private int getIndex(char ch) {
        if (ch == ' ') {
            return 26; // Map space to the last index
        } else if (ch == '-') {
            return 27; // Map "-" to the last index (same as space)
        } else {
            return Character.toUpperCase(ch) - 'A';
        }
    }
}


public class WordCompletion {
    private static TrieNode root = new TrieNode();

    public static List<String> findNearestWords(String input) {
        String filePath = "src/main/resources/locations.txt"; // Provide the path to your text file
        try {
            buildTrie(filePath);
            List<String> suggestions = root.suggestCompletions(input);
            return suggestions;
        } catch (IOException e) {
            System.err.println("Error: File not found or could not be read.");
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list in case of error
        }
    }

    public static void buildTrie(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toUpperCase();
                root.insert(line);
            }
        } catch (IOException e) {
            throw new IOException("Error reading file: " + filePath, e);
        }
    }
}
