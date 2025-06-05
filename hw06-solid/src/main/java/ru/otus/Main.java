package ru.otus;

import java.util.*;
import java.util.stream.Collectors;

class Main {
    public static void main(String[] args) {
        System.out.println("Инициализация банкомата...");
        List<Cell> initialCells = Arrays.asList(
                new Cell(Banknote.RUB5000, 10),
                new Cell(Banknote.RUB2000, 15),
                new Cell(Banknote.RUB1000, 20),
                new Cell(Banknote.RUB500, 30),
                new Cell(Banknote.RUB200, 40),
                new Cell(Banknote.RUB100, 50));
        Atm atm = new Atm(initialCells);

        System.out.println("Начальный баланс банкомата: " + atm.getBalance() + " RUB");
        printCellStatus(atm);

        System.out.println("Демонстрация пополнения:");
        try {
            atm.deposit(Banknote.RUB1000, 5);
            atm.deposit(Banknote.RUB500, 10);
            Map<Banknote, Integer> batchDeposit = new HashMap<>();
            batchDeposit.put(Banknote.RUB100, 20);
            batchDeposit.put(Banknote.RUB2000, 2);
            atm.deposit(batchDeposit);
            System.out.println("Баланс после пополнения: " + atm.getBalance() + " RUB");
            printCellStatus(atm);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка пополнения: " + e.getMessage());
        }

        int amountToWithdraw1 = 12800;
        System.out.println("Попытка снять " + amountToWithdraw1 + " RUB...");
        performWithdrawal(atm, amountToWithdraw1);

        int amountToWithdraw2 = 7700;
        System.out.println("Попытка снять " + amountToWithdraw2 + " RUB...");
        performWithdrawal(atm, amountToWithdraw2);

        int amountToWithdraw3 = 150;
        System.out.println("Попытка снять " + amountToWithdraw3 + " RUB (невозможно набрать точно)...");
        performWithdrawal(atm, amountToWithdraw3);

        System.out.println("Текущий баланс перед попыткой снять большую сумму: " + atm.getBalance() + " RUB");
        int amountToWithdraw4 = 200000;
        System.out.println("Попытка снять " + amountToWithdraw4 + " RUB (превышает баланс)...");
        performWithdrawal(atm, amountToWithdraw4);

        System.out.println("Снимаем все банкноты RUB5000, если возможно...");
        int current5000notes = atm.getCellStatus().getOrDefault(Banknote.RUB5000, 0);
        if (current5000notes > 0) {
            int amountToUseAll5000 = current5000notes * 5000 + 100;
            System.out.println("Попытка снять " + amountToUseAll5000 + " RUB, чтобы использовать все RUB5000...");
            performWithdrawal(atm, amountToUseAll5000);
        } else {
            System.out.println("Банкнот RUB5000 не осталось для демонстрации.");
        }

        System.out.println("Финальный баланс банкомата: " + atm.getBalance() + " RUB");
        printCellStatus(atm);
    }

    private static void performWithdrawal(Atm atm, int amount) {
        try {
            Map<Banknote, Integer> withdrawn = atm.withdraw(amount);
            System.out.println("Успешно снято: " + formatBanknotesMap(withdrawn));
            System.out.println("Баланс банкомата после снятия: " + atm.getBalance() + " RUB");
            printCellStatus(atm);
        } catch (CannotWithdrawException | IllegalArgumentException e) {
            System.err.println("Ошибка снятия " + amount + " RUB: " + e.getMessage());
            System.out.println("Баланс банкомата не изменился: " + atm.getBalance() + " RUB");
            printCellStatus(atm);
        }
    }

    private static String formatBanknotesMap(Map<Banknote, Integer> banknotes) {
        if (banknotes == null || banknotes.isEmpty()) {
            return "нет банкнот";
        }
        return banknotes.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted(Map.Entry.<Banknote, Integer>comparingByKey(
                        Comparator.comparingInt(Banknote::getValue).reversed()))
                .map(e -> e.getValue() + "x" + e.getKey().name())
                .collect(Collectors.joining(", "));
    }

    private static void printCellStatus(Atm atm) {
        System.out.println("Состояние ячеек: " + formatBanknotesMap(atm.getCellStatus()));
    }
}
