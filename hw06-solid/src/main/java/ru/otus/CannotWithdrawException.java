package ru.otus;

class CannotWithdrawException extends Exception {
    public CannotWithdrawException(String message) {
        super(message);
    }
}
