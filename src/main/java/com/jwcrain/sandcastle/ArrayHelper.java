package com.jwcrain.sandcastle;

/*
Some cool array helper functions
@author Jon Crain
 */
public class ArrayHelper {
    public static void print(int[] array) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            stringBuilder.append(array[i]).append(" ");
        }

        System.out.println(stringBuilder.toString());
    }

    public static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
