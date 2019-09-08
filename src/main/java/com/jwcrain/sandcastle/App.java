package com.jwcrain.sandcastle;

/*
Some sweet sandbox code
@author Jon Crain
 */
public class App {
    public static void main(String[] args) {
        App app = new App();

        app.quicksort();

        app.mergesort();
    }

    private void quicksort() {
        System.out.println("Quicksorting");

        int[] array = ArrayHelper.generateRandomArray(20, 10);

        ArrayHelper.print(array);

        Quicksort.sort(array);

        ArrayHelper.print(array);

        System.out.println("Done quicksorting");
    }

    private void mergesort() {
        System.out.println("Mergesorting");

        int[] array = ArrayHelper.generateRandomArray(20, 10);

        ArrayHelper.print(array);

        array = Mergesort.sort(array);

        ArrayHelper.print(array);

        System.out.println("Done mergesorting");
    }

}
