package com.jwcrain.sandcastle;

import java.util.PriorityQueue;

public class Heapsort {
    public static void sort(int[] array) {
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();

        for (int i = 0; i < array.length; i++) {
            priorityQueue.add(array[i]);
        }

        for (int i = 0; i < array.length; i++) {
            array[i] = priorityQueue.poll();
        }
    }
}
