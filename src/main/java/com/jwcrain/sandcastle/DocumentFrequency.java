package com.jwcrain.sandcastle;

import java.util.HashMap;
import java.util.Map;

public class DocumentFrequency {
    HashMap<String, Integer> frequency = new HashMap<>();

    public DocumentFrequency(String document) {
        String[] words = document.split(" ");

        for (int i = 0; i < words.length; i++) {
            String currentWord = words[i];

            currentWord = sanitizeWord(currentWord);

            if (frequency.containsKey(currentWord)) {
                int currentFrequency = frequency.get(currentWord);
                frequency.put(currentWord, currentFrequency + 1);
            } else {
                frequency.put(currentWord, 1);
            }
        }
    }

    private String sanitizeWord(String input) {
        StringBuilder output = new StringBuilder();

        input = input.toLowerCase().trim();

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (currentChar >= 'a' && currentChar <= 'z') {
                output.append(currentChar);
            }
        }

        return output.toString();
    }

    public Integer search(String search) {
        if (frequency.containsKey(search)) {
            return frequency.get(search);
        }

        return null;
    }

    public void print() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : frequency.keySet()) {
            stringBuilder.append(key).append("=").append(frequency.get(key)).append("; ");
        }

        System.out.println(stringBuilder.toString());
    }
}
