package com.jwcrain.sandcastle.database.transaction;

import com.jwcrain.sandcastle.database.index.Index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class TransactionImpl implements Transaction {
    private long transactionId = Long.MIN_VALUE;
    private Index index;
    HashMap<Long, ArrayList<String>> hashMap = new HashMap<>(); /* transactionId -> keysLocked */

    public TransactionImpl(Index index) {
        this.index = index;
    }

    @Override
    public Optional<Long> begin(ArrayList<String> keysToLock) {
        transactionId++;

        for (String key : keysToLock) {
            boolean locked = index.lock(key);

            if (!locked) {
                return Optional.empty();
            }
        }

        return Optional.of(transactionId);
    }

    @Override
    public boolean end(long transactionId) {
        try {
            ArrayList<String> keysLocked = hashMap.get(transactionId);

            for (String key : keysLocked) {
                index.unlock(key);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

