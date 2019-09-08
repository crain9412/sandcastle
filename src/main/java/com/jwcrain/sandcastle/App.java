package com.jwcrain.sandcastle;

/*
Some sweet sandbox code
@author Jon Crain
 */
public class App {
    private static int arraySize = 10;

    public static void main(String[] args) {
        App app = new App();

        app.quicksort();

        app.mergesort();

        app.heapsort();
    }

    private void quicksort() {
        System.out.printf("Quicksorting %d numbers \n", arraySize);

        int[] array = ArrayHelper.generateRandomArray(arraySize, arraySize);

        long startTime = System.nanoTime();

        Quicksort.sort(array);

        long duration = System.nanoTime() - startTime;

        System.out.printf("Finished in %s nanoseconds \n", duration);
    }

    private void mergesort() {
        System.out.printf("Mergesorting %d numbers \n", arraySize);

        int[] array = ArrayHelper.generateRandomArray(arraySize, arraySize);

        long startTime = System.nanoTime();

        array = Mergesort.sort(array);

        long duration = System.nanoTime() - startTime;

        System.out.printf("Finished in %s nanoseconds \n", duration);
    }

    private void heapsort() {
        System.out.printf("Heapsorting %d numbers \n", arraySize);

        int[] array = ArrayHelper.generateRandomArray(arraySize, arraySize);

        ArrayHelper.print(array);

        long startTime = System.nanoTime();

        Heapsort.sort(array);

        long duration = System.nanoTime() - startTime;

        ArrayHelper.print(array);

        System.out.printf("Finished in %s nanoseconds \n", duration);
    }

}
