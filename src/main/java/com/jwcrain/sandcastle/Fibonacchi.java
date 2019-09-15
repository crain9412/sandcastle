package com.jwcrain.sandcastle;

import java.math.BigInteger;

public class Fibonacchi {

    public static BigInteger calculate(int n) {
        BigInteger current = new BigInteger("-1");
        BigInteger last = new BigInteger("-1");

        for (int i = 0; i < n; i++) {
            if (i == 0) {
                current = new BigInteger("0");
            } else if (i == 1) {
                current = new BigInteger("0");
            } else if (i == 2) {
                current = new BigInteger("1");
            } else {
                current = current.add(last);
            }

            last = current;
        }

        return current;
    }
}
