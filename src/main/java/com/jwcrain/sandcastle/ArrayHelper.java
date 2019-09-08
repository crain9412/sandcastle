package com.jwcrain.sandcastle;

/*
Some cool array helper functions
@author Jon Crain
 */
public class ArrayHelper {
    /* Print all elements of the array seperated by a space, with a stringbuilder so O(n) time */
    public static void print(int[] array) {
        StringBuilder stringBuilder = new StringBuilder(array.length);

        for (int i = 0; i < array.length; i++) {
            stringBuilder.append(array[i]).append(" ");
        }

        System.out.println(stringBuilder.toString());
    }

    /* Swap two elements in place */
    public static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /* Generate a random array of size elements bounded by bound */
    public static int[] generateRandomArray(int size, int bound) {
        int[] randomArray = new int[size];

        for (int i = 0; i < size; i++) {
            randomArray[i] = (int) (Math.random() * bound);
        }

        return randomArray;
    }
}
