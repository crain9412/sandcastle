package com.jwcrain.sandcastle.huffmancoding;

import java.util.*;

public class HuffmanCoding {
    public static byte[] encode(String string) {
        HashMap<Character, Float> weights = getCharacterWeights(string);
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        ArrayList<Byte> byteArrayList = new ArrayList<>();
        byte[] bytes;
        Tree tree = new Tree();

        for (Map.Entry<Character, Float> entry : weights.entrySet()) {
            priorityQueue.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (!priorityQueue.isEmpty()) {
            Node left = priorityQueue.poll();

            if (priorityQueue.isEmpty()) {
                tree.setRoot(left);
            } else {
                Node right = priorityQueue.poll();
                float internalNodeWeight = left.getWeight() + right.getWeight();
                Node internalNode = new Node(internalNodeWeight, left, right);
                priorityQueue.add(internalNode);
            }
        }

        appendToByteArrayList(byteArrayList, tree.toBytes());

        boolean[] bits = new boolean[2048]; /* TODO: chunk file in some way, huffman encode each chunk */
        int i = 0;

        for (Map.Entry<Character, boolean[]> entry : tree.getCodes().entrySet()) {
            for (int j = 0; j < entry.getValue().length; j++) {
                if ()
                i++;
            }
            i += entry.getValue().length;
        }

        bytes = new byte[byteArrayList.size()];

        for (int i = 0; i < byteArrayList.size(); i++) {
            bytes[i] = byteArrayList.get(i);
        }

        return bytes;
    }

    private void

    private static void appendToByteArrayList(ArrayList<Byte> arrayList, byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            arrayList.add(bytes[i]);
        }
    }

    /* Public for testing purposes */
    public static HashMap<Character, Float> getCharacterWeights(String string) {
        HashMap<Character, Integer> frequencies = new HashMap<>();
        HashMap<Character, Float> weights = new HashMap<>();

        for (int i = 0; i < string.length(); i++) {
            char currentCharacter = string.charAt(i);
            int currentFrequencyCount = frequencies.getOrDefault(currentCharacter, 0);
            frequencies.put(currentCharacter, currentFrequencyCount + 1);
        }

        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            weights.put(entry.getKey(), entry.getValue() / (float) string.length());
        }

        return weights;
    }
}
