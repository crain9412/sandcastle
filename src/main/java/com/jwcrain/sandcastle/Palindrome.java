package com.jwcrain.sandcastle;

import java.util.Arrays;

/* Detects ASCII palindromes */
public class Palindrome {
    public static boolean detect(String s) {
        int leftEnd = s.length() / 2;
        int rightBegin = (s.length() % 2 == 0) ? leftEnd : leftEnd + 1;
        int[] leftCounts = new int[255];
        int[] rightCounts = new int[255];
        Arrays.fill(leftCounts, 0);
        Arrays.fill(rightCounts, 0);

        System.out.println("String " + s);
        System.out.println("Left End " + leftEnd);
        System.out.println("Right Begin " + rightBegin);

        for (int i = 0; i < leftEnd; i++) {
            leftCounts[s.charAt(i)]++;
        }

        for (int i = rightBegin; i < s.length(); i++) {
            rightCounts[s.charAt(i)]++;
        }

        for (int i = 0; i < 255; i++) {
            if (leftCounts[i] != rightCounts[i]) {
                return false;
            }
        }

        return true;
    }
}
