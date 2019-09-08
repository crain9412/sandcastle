package com.jwcrain.sandcastle;

public class Mergesort {
    public static int[] sort(int[] array) {
        return mergesort(array, 0, array.length - 1);
    }

    private static int[] mergesort(int[] array, int leftEdge, int rightEdge) {
        if (leftEdge >= rightEdge) {
            return new int[]{array[rightEdge]};
        }

        int mid = (leftEdge + rightEdge) / 2;
        int[] left = mergesort(array, leftEdge, mid);
        int[] right = mergesort(array, mid + 1, rightEdge);
        return merge(left, right);
    }

    private static int[] merge(int[] left, int[] right) {
        int[] combined = new int[left.length + right.length];
        int leftIndex = 0;
        int rightIndex = 0;
        int combinedIndex = 0;

        while (leftIndex < left.length && rightIndex < right.length) {
            if (left[leftIndex] <= right[rightIndex]) {
                combined[combinedIndex] = left[leftIndex];
                leftIndex++;
            } else {
                combined[combinedIndex] = right[rightIndex];
                rightIndex++;
            }
            combinedIndex++;
        }

        while (leftIndex < left.length) {
            combined[combinedIndex] = left[leftIndex];
            leftIndex++;
            combinedIndex++;
        }

        while (rightIndex < right.length) {
            combined[combinedIndex] = right[rightIndex];
            rightIndex++;
            combinedIndex++;
        }

        return  combined;
    }
}
