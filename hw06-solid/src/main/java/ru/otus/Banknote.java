package ru.otus;

enum Banknote {
    RUB100(100),
    RUB200(200),
    RUB500(500),
    RUB1000(1000),
    RUB2000(2000),
    RUB5000(5000);

    private final int value;

    Banknote(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name() + "(" + value + ")";
    }
}
