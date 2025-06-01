package ru.otus;

import java.util.*;

class Atm {
    private final SortedMap<Banknote, Cell> cells;

    public Atm(List<Cell> initialCells) {
        this.cells = new TreeMap<>(Comparator.comparingInt(Banknote::getValue).reversed());
        if (initialCells == null || initialCells.isEmpty()) {
            throw new IllegalArgumentException("Начальный список ячеек не может быть пустым или null.");
        }
        for (Cell cell : initialCells) {
            if (this.cells.containsKey(cell.getDenomination())) {
                throw new IllegalArgumentException("Дублирующая ячейка для номинала: " + cell.getDenomination());
            }
            this.cells.put(cell.getDenomination(), cell);
        }
    }

    public void deposit(Banknote banknote, int quantity) {
        Cell cell = cells.get(banknote);
        if (cell == null) {
            throw new IllegalArgumentException("Банкомат не поддерживает номинал: " + banknote);
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество для пополнения должно быть положительным.");
        }
        cell.addBanknotes(quantity);
        System.out.println(
                "Внесено: " + quantity + "x" + banknote.name() + ". Текущий баланс ячейки: " + cell.getCount());
    }

    public void deposit(Map<Banknote, Integer> banknotesToDeposit) {
        if (banknotesToDeposit == null || banknotesToDeposit.isEmpty()) {
            throw new IllegalArgumentException("Набор банкнот для пополнения не может быть пустым или null.");
        }
        for (Map.Entry<Banknote, Integer> entry : banknotesToDeposit.entrySet()) {
            deposit(entry.getKey(), entry.getValue());
        }
    }

    public Map<Banknote, Integer> withdraw(int amount) throws CannotWithdrawException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма для снятия должна быть положительной.");
        }
        if (amount > getBalance()) {
            throw new CannotWithdrawException(
                    "Недостаточно средств в банкомате. Запрошено: " + amount + ", Всего в банкомате: " + getBalance());
        }

        Map<Banknote, Integer> banknotesToDispense = new LinkedHashMap<>();
        int remainingAmount = amount;

        Map<Banknote, Integer> tempCellCounts = new HashMap<>();
        for (Map.Entry<Banknote, Cell> entry : cells.entrySet()) {
            tempCellCounts.put(entry.getKey(), entry.getValue().getCount());
        }

        for (Map.Entry<Banknote, Cell> entry : cells.entrySet()) {
            Banknote denomination = entry.getKey();
            int denominationValue = denomination.getValue();
            int availableInTempCell = tempCellCounts.get(denomination);

            if (remainingAmount >= denominationValue && availableInTempCell > 0) {
                int countToTake = Math.min(remainingAmount / denominationValue, availableInTempCell);
                if (countToTake > 0) {
                    banknotesToDispense.put(denomination, countToTake);
                    remainingAmount -= countToTake * denominationValue;
                    tempCellCounts.put(denomination, availableInTempCell - countToTake);
                }
            }

            if (remainingAmount == 0) {
                break;
            }
        }

        if (remainingAmount == 0) {
            for (Map.Entry<Banknote, Integer> dispenseEntry : banknotesToDispense.entrySet()) {
                cells.get(dispenseEntry.getKey()).dispenseBanknotes(dispenseEntry.getValue());
            }
            return banknotesToDispense;
        } else {
            throw new CannotWithdrawException(
                    "Невозможно выдать точную сумму: " + amount + ". Не удалось выдать: " + remainingAmount + " RUB.");
        }
    }

    public int getBalance() {
        int totalBalance = 0;
        for (Cell cell : cells.values()) {
            totalBalance += cell.getTotalValue();
        }
        return totalBalance;
    }

    public Map<Banknote, Integer> getCellStatus() {
        Map<Banknote, Integer> status = new LinkedHashMap<>();
        this.cells.forEach((banknote, cell) -> status.put(banknote, cell.getCount()));
        return status;
    }
}
