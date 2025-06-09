package ru.otus.jpql.cache;

public interface CacheListener<K, V> {
    void notify(K key, V value, String action);
}
