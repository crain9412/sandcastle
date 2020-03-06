package com.jwcrain.sandcastle.database.transaction;

import java.util.ArrayList;
import java.util.Optional;

public interface Transaction {
    Optional<Long> begin(ArrayList<String> keysToLock);
    boolean end(long transactionId);
}
