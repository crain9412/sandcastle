package com.jwcrain.sandcastle.database.concurrency;

import com.jwcrain.sandcastle.database.error.Error;

import java.util.Optional;
import java.util.concurrent.Future;

public class Helper {
    private static int POLL_TIME_MILLIS = 1;

    public static <T> Optional<T> waitForFutureOptional(Future<Optional<T>> future) {
        try {
            while(!future.isDone()) {
                Thread.sleep(POLL_TIME_MILLIS);
            }

            return future.get();
        } catch (Exception e) {
            Error.handle("Error occurred while reader thread sleeping", e);
        }

        return Optional.empty();
    }

    public static <T> T waitForFuture(Future<T> future) {
        try {
            while(!future.isDone()) {
                Thread.sleep(POLL_TIME_MILLIS);
            }

            return future.get();
        } catch (Exception e) {
            Error.handle("Error occurred while reader thread sleeping", e);
        }

        return null;
    }
}
