/*
 * This class calls the Joiner method from Guava.
 */
package ru.otus;

import com.google.common.base.Joiner;

@SuppressWarnings("java:S106")
public class HelloOtus {
    public static void main(String... args) {
        String[] words = {"Hello", "Otus"};
        String result = Joiner.on(" ").join(words);
        System.out.println(result);
    }
}
