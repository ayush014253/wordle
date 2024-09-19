package wordle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Huffman {
    private static final int R = 256;
    // Inner class to represent a node in the Huffman tree
    public static HashMap<Character,Integer> letterFrequency;


    public static class Node implements Comparable<Node> {
        char character; // The character stored in the node (for leaf nodes)
        int frequency; // Frequency of the character
        Node leftChild; // Left child of the node
        Node rightChild; // Right child of the node

        // Constructor for leaf nodes
        public Node(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        // Constructor for internal nodes
        public Node(int frequency, Node leftChild, Node rightChild) {
            this.frequency = frequency;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }
        private boolean isLeaf() {
            assert ((leftChild == null) && (rightChild == null)) || ((leftChild != null) && (rightChild != null));
            return leftChild == null;
        }
        @Override
        public int compareTo(Node other) {
            // Nodes are compared based on their frequencies
            return this.frequency - other.frequency;
        }

    }

    // Method to build the Huffman tree
    public static Node buildHuffmanTree(Map<Character, Integer> frequencies) {
        // Priority queue to store nodes based on their frequencies
        PriorityQueue<Node> pq = new PriorityQueue<>();

        // Create leaf nodes for each character and add them to the priority queue
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            pq.offer(new Node(entry.getKey(), entry.getValue()));
        }

        // Build the Huffman tree by merging nodes from the priority queue
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node(left.frequency + right.frequency, left, right);
            pq.offer(parent);
        }

        // Return the root of the Huffman tree
        return pq.poll();
    }
    // Read input from standard input
    public static Node readInput() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\ayush\\IdeaProjects\\datastructures20280-23\\src\\wordle\\resources\\dictionary.txt"));
        StringBuilder sb = new StringBuilder();
        HashMap<Character, Integer> frequencies ;
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        frequencies=buildFrequencyTable(sb.toString());
        letterFrequency=frequencies;
        return buildHuffmanTree(frequencies);
    }
    private static HashMap<Character, Integer> buildFrequencyTable(String input) {
        HashMap<Character, Integer> freqMap = new HashMap<>();

        // Iterate over each character in the input string
        for (char c : input.toCharArray()) {
            // Increment the frequency count for the character
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }
        return freqMap;
    }
    public Map<String,Integer> getWordFrequency(List<String> list) {
        Map<String, Integer> wordFrequencyMap = new HashMap<>();
        Map<Character, Integer> frequencyMap = buildFrequencyTable(String.valueOf(list));
        for (String word : list){
            int frequencySum = 0;

            for (int i = 0; i < 5; i++) {

                frequencySum += letterFrequency.get(word.charAt(i));
            }
            wordFrequencyMap.put(word,frequencySum);
        }
        return wordFrequencyMap;
        }
    // Method to encode a word using the Huffman tree
    public static String encodeWord(String word, Node root) {
        StringBuilder encodedString = new StringBuilder();
        Map<Character, String> encodingMap = buildEncodingMap(root, "");

        for (char c : word.toCharArray()) {
            if (encodingMap.containsKey(c)) {
                encodedString.append(encodingMap.get(c));
            } else {
                throw new IllegalArgumentException("Character '" + c + "' is not present in the Huffman tree.");
            }
        }

        return encodedString.toString();
    }

    // Helper method to build a map of character-to-binary-string encoding
    private static Map<Character, String> buildEncodingMap(Node node, String code) {
        Map<Character, String> encodingMap = new HashMap<>();

        if (node.isLeaf()) {
            encodingMap.put(node.character, code);
        } else {
            encodingMap.putAll(buildEncodingMap(node.leftChild, code + "0"));
            encodingMap.putAll(buildEncodingMap(node.rightChild, code + "1"));
        }

        return encodingMap;
    }
    // Method to decode a word using the Huffman tree
    public static String decodeWord(String encodedWord, Node root) {
        StringBuilder decodedString = new StringBuilder();
        Node current = root;

        for (int i = 0; i < encodedWord.length(); i++) {
            char bit = encodedWord.charAt(i);

            if (bit == '0') {
                current = current.leftChild;
            } else if (bit == '1') {
                current = current.rightChild;
            } else {
                throw new IllegalArgumentException("Invalid bit encountered: " + bit);
            }

            if (current.isLeaf()) {
                decodedString.append(current.character);
                current = root; // Reset to the root for the next character
            }
        }

        return decodedString.toString();
    }

    // Example usage
    public static void main(String[] args) throws IOException {
        // Example frequency map

        // Build the Huffman tree
        Node root = Huffman.readInput();

        // Encode and decode a word
        String word = "abcdfefffeeaghi";
        String encodedWord = encodeWord(word, root);
        String decodedWord = decodeWord(encodedWord, root);

        List<String> list=new ArrayList<>();
        list.add("mystr");
        list.add("sudho");
        Huffman huff = new Huffman();
        System.out.println(huff.getWordFrequency(list));
        System.out.println("Original word: " + word);
        System.out.println("Encoded word: " + encodedWord);
        System.out.println("Decoded word: " + decodedWord);
    }
}
