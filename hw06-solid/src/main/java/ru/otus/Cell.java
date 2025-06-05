package ru.otus;

class Cell {
    private final Banknote denomination;
    private int count;

    public Cell(Banknote denomination, int initialCount) {
        if (denomination == null) {
            throw new IllegalArgumentException("Номинал не может быть null.");
        }
        if (initialCount < 0) {
            throw new IllegalArgumentException("Начальное количество не может быть отрицательным.");
        }
        this.denomination = denomination;
        this.count = initialCount;
    }

    public Banknote getDenomination() {
        return denomination;
    }

    public int getCount() {
        return count;
    }

    public int getTotalValue() {
        return denomination.getValue() * count;
    }

    public void addBanknotes(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество добавляемых банкнот должно быть положительным.");
        }
        this.count += quantity;
    }

    public void dispenseBanknotes(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество выдаваемых банкнот должно быть положительным.");
        }
        if (quantity > this.count) {
            throw new IllegalStateException("Недостаточно банкнот номинала " + denomination + " для выдачи. Запрошено: "
                    + quantity + ", Доступно: " + this.count);
        }
        this.count -= quantity;
    }

    @Override
    public String toString() {
        return "Ячейка{" + "номинал=" + denomination + ", количество=" + count + '}';
    }
}
