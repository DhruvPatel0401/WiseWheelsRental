package Uwindsor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class AVLNode {
    String word;
    int height;
    AVLNode left, right;

    AVLNode(String word) {
        this.word = word;
        this.height = 1;
        this.left = this.right = null;
    }
}

class AVLTree {
    AVLNode root;

    int height(AVLNode node) {
        if (node == null)
            return 0;
        return node.height;
    }

    int max(int a, int b) {
        return (a > b) ? a : b;
    }

    int getBalance(AVLNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }

    AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;

        return x;
    }

    AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        return y;
    }

    AVLNode insert(AVLNode node, String word) {
        if (node == null)
            return (new AVLNode(word));

        if (word.compareTo(node.word) < 0)
            node.left = insert(node.left, word);
        else if (word.compareTo(node.word) > 0)
            node.right = insert(node.right, word);
        else // Duplicate words not allowed
            return node;

        node.height = 1 + max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && word.compareTo(node.left.word) < 0)
            return rotateRight(node);

        if (balance < -1 && word.compareTo(node.right.word) > 0)
            return rotateLeft(node);

        if (balance > 1 && word.compareTo(node.left.word) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balance < -1 && word.compareTo(node.right.word) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    void inOrderTraversal(AVLNode node) {
        if (node != null) {
            inOrderTraversal(node.left);
            System.out.print(node.word + " ");
            inOrderTraversal(node.right);
        }
    }

    String spellCheck(String word) {
        AVLNode closestNode = findClosest(root, word);
        if (closestNode != null) {
            return closestNode.word;
        } else {
            return "No suggestion found";
        }
    }

    AVLNode findClosest(AVLNode node, String word) {
        if (node == null)
            return null;

        if (node.word.equals(word))
            return node;

        if (word.compareTo(node.word) < 0) {
            AVLNode leftClosest = findClosest(node.left, word);
            return (leftClosest == null || Math.abs(word.length() - leftClosest.word.length()) < Math.abs(word.length() - node.word.length())) ? leftClosest : node;
        } else {
            AVLNode rightClosest = findClosest(node.right, word);
            return (rightClosest == null || Math.abs(word.length() - rightClosest.word.length()) < Math.abs(word.length() - node.word.length())) ? rightClosest : node;
        }
    }
}

public class SpellChecking {
	public static String correctedWord(String input) {
		return "";
	}
	
	public static void main(String[] args) {
        AVLTree avlTree = new AVLTree();

        // Load words from file and build AVL tree
        String filename = "src/main/resources/locations.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                avlTree.root = avlTree.insert(avlTree.root, line.trim());
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Take user input
        String userInput = "windser";
        System.out.println("User Input: " + userInput);

        // Spell check
        String suggestion = avlTree.spellCheck(userInput);
        System.out.println("Suggestion: " + suggestion);
    }
}
