package ru.otus.sessionmanager;

import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionManagerSpring implements TransactionManager {

    @Override
    @Transactional
    public <T> T doInTransaction(Supplier<T> action) {
        return action.get();
    }
}
