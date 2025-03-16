package ru.otus;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    private final TreeMap<Customer, String> customerMap = new TreeMap<>(Comparator.comparingLong(Customer::getId));

    public Map.Entry<Customer, String> getSmallest() {
        return customerMap.firstEntry();
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return customerMap.higherEntry(customer);
    }

    public void add(Customer customer, String data) {
        customerMap.put(customer, data);
    }
}
