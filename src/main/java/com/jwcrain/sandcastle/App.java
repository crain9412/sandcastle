package com.jwcrain.sandcastle;

/*
Some sweet sandbox code
@author Jon Crain
 */
public class App {
    public void quicksort() {
        System.out.println("Quicksorting");

        int[] array = {6, 5, 3, 2, 4, 1};

        ArrayHelper.print(array);

        Quicksort.sort(array);

        ArrayHelper.print(array);

        System.out.println("Done quicksorting");
    }

    public static void main(String[] args) {
        App app = new App();

        app.quicksort();
    }
}
