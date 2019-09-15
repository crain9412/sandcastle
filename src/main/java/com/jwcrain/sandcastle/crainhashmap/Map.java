package com.jwcrain.sandcastle.crainhashmap;

public class Map {
    private Integer numberOfBuckets = 255;
    private List[] buckets = new List[numberOfBuckets];

    public Map() {
        for (int i = 0; i < numberOfBuckets; i++) {
            buckets[i] = new List();
        }
    }

    public void put(String key, String value) {
        Integer bucketIndex = hash(key);

        Node createdNode = new Node(key, value);

        buckets[bucketIndex].addNode(createdNode);
    }

    public String get(String key) {
        Integer bucketIndex = hash(key);

        Node current = buckets[bucketIndex].getHead();

        while (current != null) {
            if (current.getKey().equals(key)) {
                return current.getValue();
            }

            current = current.getNext();
        }

        return null;
    }

    private Integer hash(String key) {
        int charCount = 0;

        for (int i = 0; i < key.length(); i++) {
            charCount += key.charAt(i);
        }

        return charCount % 255;
    }

    public void print() {
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < numberOfBuckets; i++) {
            List list = buckets[i];
            stringBuilder.append("\nBucket ").append(i).append(": ");
            Node current = list.getHead();

            while (current != null) {
                stringBuilder.append("[").append(current.getValue()).append("]");

                if (current.getNext() != null) {
                    stringBuilder.append(" -> ");
                }
                current = current.getNext();
            }
        }

        System.out.println(stringBuilder.toString());
    }
}
