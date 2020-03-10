package com.jwcrain.sandcastle.huffmancoding;

import java.util.*;

public class Tree {
    private Node root;

    public Tree() {

    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public byte[] toBytes() {
        return new byte[]{};
    }

    @Override
    public String toString() {
        return printHelper(this.root, "", "");
    }

    public String printHelper(Node current, String left, String right) {
        if (current.getLeft() != null) {
            left = printHelper(current.getLeft(), current.toString() + "\n", right);
        }
        if (current.getRight() != null) {
            right = printHelper(current.getRight(), left, current.toString() + "\n");
        }
        return left + right;
    }

    public void printBinaryCodes() {
        for (Map.Entry<Character, boolean[]> entry : codeHelper(root, new boolean[8], new HashMap<>(), 0).entrySet()) {
            System.out.printf("Character: %c=%s\n", entry.getKey(), bitsToString(entry.getValue()));
        }
    }

    private String bitsToString(boolean[] bits) {
        String s = ""; /* TODO: stringbuilder */

        for (int i = 0; i < bits.length; i++) {
            if (bits[i]) {
                s += "1";
            } else {
                s += "0";
            }
        }

        return s;
    }

    public HashMap<Character, boolean[]> getCodes() {
        return codeHelper(root, new boolean[8], new HashMap<>(), 0);
    }

    public HashMap<Character, boolean[]> codeHelper(Node current, boolean[] bits, HashMap<Character, boolean[]> accumulator, int level) {
        if (current.getCharacter() != null) {
            accumulator.put(current.getCharacter(), bits);
            System.out.printf("Putting character %c with bits %s\n", current.getCharacter(), Arrays.toString(bits));
        }
        if (current.getLeft() != null) {
            boolean[] leftBits = bits.clone();
            leftBits[level] = false;
            codeHelper(current.getLeft(), leftBits, accumulator, level + 1);
        }
        if (current.getRight() != null) {
            boolean[] rightBits = bits.clone();
            rightBits[level] = true;
            codeHelper(current.getRight(), rightBits, accumulator, level + 1);
        }
        return accumulator;
    }

}
