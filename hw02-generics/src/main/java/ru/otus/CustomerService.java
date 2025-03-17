package ru.otus;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CustomerService {

    private final NavigableMap<Customer, String> customerMap =
            new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> entry = customerMap.firstEntry();
        return entry != null ? Map.entry(entry.getKey().copy(), entry.getValue()) : null;
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> entry = customerMap.higherEntry(customer);
        return entry != null ? Map.entry(entry.getKey().copy(), entry.getValue()) : null;
    }

    public void add(Customer customer, String data) {
        customerMap.put(customer, data);
    }
}
