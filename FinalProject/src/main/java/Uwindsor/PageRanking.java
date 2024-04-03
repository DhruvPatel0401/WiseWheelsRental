package Uwindsor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PageRanking {
    static void displayPageRanking(String filePath, String keyword) throws IOException {
    	try {
            MaxHeap maxHeap = getPageRanking(filePath, keyword);

            // Print car types with counts in descending order
            System.out.println("\n******************************");
            System.out.println("Ranking According to " + keyword + ": ");
            System.out.println("******************************");
            while (!maxHeap.isEmpty()) {
                CarTypeCount carTypeCount = maxHeap.extractMax();
                System.out.println(carTypeCount.carType + ": " + carTypeCount.count);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the files: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static MaxHeap getPageRanking(String filePath, String targetTextFile) throws IOException {
        MaxHeap maxHeap = new MaxHeap();

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
                                maxHeap.insertOrUpdate(carType);
                            }
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid directory or directory is empty: " + filePath);
        }

        return maxHeap;
    }

    static class MaxHeap {
        private Node root;

        public boolean isEmpty() {
            return root == null;
        }

        public void insertOrUpdate(String carType) {
            if (root == null) {
                root = new Node(carType);
            } else {
                root = insertOrUpdate(root, carType);
            }
        }

        private Node insertOrUpdate(Node node, String carType) {
            if (node == null) {
                return new Node(carType);
            }

            if (node.carType.equals(carType)) {
                node.incrementCount();
            } else if (node.left == null) {
                node.left = insertOrUpdate(node.left, carType);
                if (node.left.count > node.count) {
                    node = rotateRight(node);
                }
            } else if (node.right == null) {
                node.right = insertOrUpdate(node.right, carType);
                if (node.right.count > node.count) {
                    node = rotateLeft(node);
                }
            } else {
                if (node.left.count > node.right.count) {
                    node = rotateRight(node);
                    node.left = insertOrUpdate(node.left, carType);
                } else {
                    node = rotateLeft(node);
                    node.right = insertOrUpdate(node.right, carType);
                }
            }

            return node;
        }

        public CarTypeCount extractMax() {
            if (root == null) {
                return null;
            }

            CarTypeCount max = new CarTypeCount(root.carType);
            max.count = root.count;

            if (root.left == null && root.right == null) {
                root = null;
            } else if (root.left != null && root.right == null) {
                root = root.left;
            } else if (root.left == null && root.right != null) {
                root = root.right;
            } else {
                if (root.left.count > root.right.count) {
                    root = rotateRight(root);
                    root.right = extractMax(root.right);
                } else {
                    root = rotateLeft(root);
                    root.left = extractMax(root.left);
                }
            }

            return max;
        }

        private Node extractMax(Node node) {
            if (node.right != null) {
                if (node.right.right == null) {
                    Node max = node.right;
                    node.right = null;
                    return max;
                } else {
                    return extractMax(node.right);
                }
            } else {
                Node max = node;
                node = node.left;
                return max;
            }
        }

        private Node rotateRight(Node node) {
            Node temp = node.left;
            node.left = temp.right;
            temp.right = node;
            return temp;
        }

        private Node rotateLeft(Node node) {
            Node temp = node.right;
            node.right = temp.left;
            temp.left = node;
            return temp;
        }

        private class Node {
            String carType;
            int count;
            Node left;
            Node right;

            Node(String carType) {
                this.carType = carType;
                this.count = 1;
            }

            void incrementCount() {
                this.count++;
            }
        }
    }

    public static class CarTypeCount {
        String carType;
        int count;

        CarTypeCount(String carType) {
            this.carType = carType;
            this.count = 1;
        }

        void incrementCount() {
            this.count++;
        }

        int getCount() {
            return count;
        }
    }
}
