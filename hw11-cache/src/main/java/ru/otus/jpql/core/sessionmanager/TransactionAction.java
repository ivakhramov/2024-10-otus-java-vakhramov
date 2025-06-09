package ru.otus.jpql.core.sessionmanager;

import java.util.function.Function;
import org.hibernate.Session;

public interface TransactionAction<T> extends Function<Session, T> {}
