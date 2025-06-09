package ru.otus.cachehw;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCache<K, V> implements HwCache<K, V> {
    // Надо реализовать эти методы
    private static final Logger logger = LoggerFactory.getLogger(MyCache.class);

    private final Map<K, V> cache = new WeakHashMap<>();
    private final List<HwListener<K, V>> listeners = new ArrayList<>();

    private static final String ACTION_PUT = "PUT";
    private static final String ACTION_REMOVE = "REMOVE";
    private static final String ACTION_GET = "GET";

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        notifyListeners(key, value, ACTION_PUT);
    }

    @Override
    public void remove(K key) {
        V value = cache.remove(key);
        if (value != null) {
            notifyListeners(key, value, ACTION_REMOVE);
        }
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            notifyListeners(key, value, ACTION_GET);
        }
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(K key, V value, String action) {
        for (HwListener<K, V> listener : listeners) {
            try {
                listener.notify(key, value, action);
            } catch (Exception e) {
                logger.error("Error calling listener: key: {}, action: {}", key, action, e);
            }
        }
    }
}
