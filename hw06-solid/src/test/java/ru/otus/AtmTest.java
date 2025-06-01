package ru.otus;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AtmTest {

    private Atm atm;
    private List<Cell> initialCells;

    @BeforeEach
    void setUp() {
        initialCells = Arrays.asList(
                new Cell(Banknote.RUB5000, 10),
                new Cell(Banknote.RUB1000, 20),
                new Cell(Banknote.RUB500, 30),
                new Cell(Banknote.RUB100, 50));
        atm = new Atm(new ArrayList<>(initialCells));
    }

    private Atm createAtmWithSpecificSetup(List<Cell> cells) {
        return new Atm(cells);
    }

    @Test
    @DisplayName("Проверка начального баланса")
    void getBalance_initial() {
        assertEquals(90000, atm.getBalance());
    }

    @Test
    @DisplayName("Успешное пополнение одной банкнотой")
    void deposit_singleBanknote_updatesBalanceAndCellCount() {
        int initialBalance = atm.getBalance();
        int initialRub1000Count = atm.getCellStatus().get(Banknote.RUB1000);

        atm.deposit(Banknote.RUB1000, 5);

        assertEquals(initialBalance + 5000, atm.getBalance());
        assertEquals(initialRub1000Count + 5, atm.getCellStatus().get(Banknote.RUB1000));
    }

    @Test
    @DisplayName("Успешное пополнение набором банкнот")
    void deposit_multipleBanknotes_updatesBalanceAndCellCounts() {
        int initialBalance = atm.getBalance();
        int initialRub500Count = atm.getCellStatus().get(Banknote.RUB500);
        int initialRub100Count = atm.getCellStatus().get(Banknote.RUB100);

        Map<Banknote, Integer> toDeposit = new HashMap<>();
        toDeposit.put(Banknote.RUB500, 10);
        toDeposit.put(Banknote.RUB100, 20);

        atm.deposit(toDeposit);

        assertEquals(initialBalance + (10 * 500) + (20 * 100), atm.getBalance());
        assertEquals(initialRub500Count + 10, atm.getCellStatus().get(Banknote.RUB500));
        assertEquals(initialRub100Count + 20, atm.getCellStatus().get(Banknote.RUB100));
    }

    @Test
    @DisplayName("Попытка пополнения неподдерживаемым номиналом")
    void deposit_unsupportedDenomination_throwsException() {
        List<Cell> cellsForThisTest = Arrays.asList(new Cell(Banknote.RUB100, 10));
        Atm testAtm = createAtmWithSpecificSetup(cellsForThisTest);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            testAtm.deposit(Banknote.RUB200, 1);
        });
        assertTrue(exception.getMessage().contains("Банкомат не поддерживает номинал: RUB200"));
    }

    @Test
    @DisplayName("Попытка пополнения нулевым или отрицательным количеством")
    void deposit_invalidQuantity_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> atm.deposit(Banknote.RUB100, 0));
        assertThrows(IllegalArgumentException.class, () -> atm.deposit(Banknote.RUB100, -5));
    }

    @Test
    @DisplayName("Успешная выдача корректной суммы")
    void withdraw_successful_dispensesCorrectBanknotesAndUpdatesBalance() throws CannotWithdrawException {
        int amountToWithdraw = 6800;
        int initialBalance = atm.getBalance();
        int initialRub5000 = atm.getCellStatus().get(Banknote.RUB5000);
        int initialRub1000 = atm.getCellStatus().get(Banknote.RUB1000);
        int initialRub500 = atm.getCellStatus().get(Banknote.RUB500);
        int initialRub100 = atm.getCellStatus().get(Banknote.RUB100);

        Map<Banknote, Integer> withdrawn = atm.withdraw(amountToWithdraw);

        assertEquals(1, withdrawn.get(Banknote.RUB5000));
        assertEquals(1, withdrawn.get(Banknote.RUB1000));
        assertEquals(1, withdrawn.get(Banknote.RUB500));
        assertEquals(3, withdrawn.get(Banknote.RUB100));
        assertNull(withdrawn.get(Banknote.RUB200));

        assertEquals(initialBalance - amountToWithdraw, atm.getBalance());
        assertEquals(initialRub5000 - 1, atm.getCellStatus().get(Banknote.RUB5000));
        assertEquals(initialRub1000 - 1, atm.getCellStatus().get(Banknote.RUB1000));
        assertEquals(initialRub500 - 1, atm.getCellStatus().get(Banknote.RUB500));
        assertEquals(initialRub100 - 3, atm.getCellStatus().get(Banknote.RUB100));
    }

    @Test
    @DisplayName("Выдача минимальным количеством банкнот (жадный алгоритм)")
    void withdraw_usesMinimalBanknotesGreedy() throws CannotWithdrawException {
        Map<Banknote, Integer> withdrawn = atm.withdraw(5000);
        assertEquals(1, withdrawn.get(Banknote.RUB5000));
        assertEquals(1, withdrawn.size());

        Map<Banknote, Integer> withdrawn2 = atm.withdraw(4900);
        assertEquals(4, withdrawn2.get(Banknote.RUB1000));
        assertEquals(1, withdrawn2.get(Banknote.RUB500));
        assertEquals(4, withdrawn2.get(Banknote.RUB100));
    }

    @Test
    @DisplayName("Попытка снять сумму, которую невозможно набрать точно")
    void withdraw_amountNotExactlyPossible_throwsCannotWithdrawException() {
        List<Cell> cells = Arrays.asList(new Cell(Banknote.RUB5000, 1), new Cell(Banknote.RUB1000, 1));
        Atm customAtm = new Atm(cells);

        Exception exception = assertThrows(CannotWithdrawException.class, () -> {
            customAtm.withdraw(150);
        });
        assertTrue(exception.getMessage().contains("Невозможно выдать точную сумму: 150"));
        assertEquals(6000, customAtm.getBalance());
    }

    @Test
    @DisplayName("Попытка снять сумму, превышающую общий баланс")
    void withdraw_amountExceedsTotalBalance_throwsCannotWithdrawException() {
        int currentBalance = atm.getBalance();
        Exception exception = assertThrows(CannotWithdrawException.class, () -> {
            atm.withdraw(currentBalance + 100);
        });
        assertTrue(exception.getMessage().contains("Недостаточно средств в банкомате"));
        assertEquals(currentBalance, atm.getBalance());
    }

    @Test
    @DisplayName("Попытка снять сумму, когда недостаточно конкретных номиналов для точной выдачи")
    void withdraw_insufficientSpecificDenominationsForExactAmount_throwsCannotWithdrawException() {
        List<Cell> cells = Arrays.asList(new Cell(Banknote.RUB1000, 1), new Cell(Banknote.RUB100, 4));
        Atm customAtm = new Atm(cells);

        Exception exception = assertThrows(CannotWithdrawException.class, () -> {
            customAtm.withdraw(500);
        });
        assertTrue(exception.getMessage().contains("Невозможно выдать точную сумму: 500"));
        assertEquals(1400, customAtm.getBalance());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -100, -5000})
    @DisplayName("Попытка снять нулевую или отрицательную сумму")
    void withdraw_invalidAmount_throwsIllegalArgumentException(int invalidAmount) {
        assertThrows(IllegalArgumentException.class, () -> atm.withdraw(invalidAmount));
    }

    @Test
    @DisplayName("Снятие всех денег из банкомата")
    void withdraw_allMoney_leavesZeroBalance() throws CannotWithdrawException {
        int totalBalance = atm.getBalance();
        atm.withdraw(totalBalance);
        assertEquals(0, atm.getBalance());
        assertTrue(atm.getCellStatus().values().stream().allMatch(count -> count == 0));
    }

    @Test
    @DisplayName("Инициализация банкомата с дублирующимися ячейками номиналов")
    void constructor_duplicateDenominationCells_throwsIllegalArgumentException() {
        List<Cell> cellsWithDuplicates = Arrays.asList(new Cell(Banknote.RUB100, 10), new Cell(Banknote.RUB100, 5));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Atm(cellsWithDuplicates);
        });
        assertTrue(exception.getMessage().contains("Дублирующая ячейка для номинала: RUB100"));
    }

    @Test
    @DisplayName("Инициализация банкомата пустым или null списком ячеек")
    void constructor_emptyOrNullInitialCells_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Atm(Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> new Atm(null));
    }

    @Test
    @DisplayName("Cell: Попытка выдать больше, чем есть в ячейке")
    void cell_dispenseMoreThanAvailable_throwsIllegalStateException() {
        Cell cell = new Cell(Banknote.RUB100, 5);
        assertThrows(IllegalStateException.class, () -> cell.dispenseBanknotes(10));
    }

    @Test
    @DisplayName("Cell: Попытка добавить/выдать некорректное количество")
    void cell_addOrDispenseInvalidQuantity_throwsIllegalArgumentException() {
        Cell cell = new Cell(Banknote.RUB100, 5);
        assertThrows(IllegalArgumentException.class, () -> cell.addBanknotes(0));
        assertThrows(IllegalArgumentException.class, () -> cell.addBanknotes(-1));
        assertThrows(IllegalArgumentException.class, () -> cell.dispenseBanknotes(0));
        assertThrows(IllegalArgumentException.class, () -> cell.dispenseBanknotes(-1));
    }
}
