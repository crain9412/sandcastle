package com.jwcrain.sandcastle.huffmancoding;

import java.util.HashMap;
import java.util.Map;

public class HuffmanCoding {
    public static byte[] encode(String string) {
        return string.getBytes();
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
