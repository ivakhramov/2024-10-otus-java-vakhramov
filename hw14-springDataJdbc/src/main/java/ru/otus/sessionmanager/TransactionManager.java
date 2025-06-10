package ru.otus.sessionmanager;

import java.util.function.Supplier;

public interface TransactionManager {
    <T> T doInTransaction(Supplier<T> action);
}
