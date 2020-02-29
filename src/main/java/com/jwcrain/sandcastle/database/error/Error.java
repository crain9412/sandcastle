package com.jwcrain.sandcastle.database.error;

import org.apache.log4j.Logger;

public interface Error {
    Logger logger = Logger.getLogger(Error.class);

    static void handle(String message, Exception e) {
        logger.error(message, e);
    }
}
