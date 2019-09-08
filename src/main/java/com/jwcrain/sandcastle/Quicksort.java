package com.jwcrain.sandcastle;

/*
Mid-pivot quicksort implementation with a store
@author Jon Crain
 */
public class Quicksort {
    /* Helper method to start a quicksort */
    public static void sort(int[] array) {
        quicksort(array, 0, array.length - 1);
    }

    /* Recursive part of the function*/
    private static void quicksort(int[] array, int leftEdge, int rightEdge) {
        if (leftEdge < rightEdge) {
            int index = partition(array, leftEdge, rightEdge);
            quicksort(array, leftEdge, index - 1);
            quicksort(array, index + 1, rightEdge);
        }
    }

    /* Partition all elements of the array around one pivot, chosen as the middle of the array */
    private static int partition(int[] array, int leftEdge, int rightEdge) {
        int pivot = (leftEdge + rightEdge) / 2;

        int pivotValue = array[pivot];

        ArrayHelper.swap(array, pivot, rightEdge);

        int store = leftEdge;

        for (int i = leftEdge; i < rightEdge; i++) {
            int currentValue = array[i];

            if (currentValue <= pivotValue) {
                ArrayHelper.swap(array, i, store);
                store++;
            }
        }

        ArrayHelper.swap(array, rightEdge, store);

        return store;
    }
}
