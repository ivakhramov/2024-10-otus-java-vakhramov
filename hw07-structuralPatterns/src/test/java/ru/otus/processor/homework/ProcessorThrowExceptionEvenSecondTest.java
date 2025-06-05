package ru.otus.processor.homework;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

class ProcessorThrowExceptionEvenSecondTest {

    @Test
    @DisplayName("Должен выбросить исключение, если секунда четная")
    void process_shouldThrowException_whenSecondIsEven() {
        var message = new Message.Builder(1L).field1("Test").build();
        var dateTimeProvider = mock(DateTimeProvider.class);

        when(dateTimeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.of(2023, 1, 1, 10, 30, 22));

        var processor = new ProcessorThrowExceptionEvenSecond(dateTimeProvider);

        assertThrows(
                EvenSecondException.class,
                () -> processor.process(message),
                "Должно быть выброшено EvenSecondException");
    }

    @Test
    @DisplayName("Не должен выбрасывать исключение, если секунда нечетная")
    void process_shouldNotThrowException_whenSecondIsOdd() {
        var message = new Message.Builder(1L).field1("Test").build();
        var dateTimeProvider = mock(DateTimeProvider.class);

        when(dateTimeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.of(2023, 1, 1, 10, 30, 23));

        var processor = new ProcessorThrowExceptionEvenSecond(dateTimeProvider);

        assertDoesNotThrow(
                () -> {
                    Message result = processor.process(message);
                    assertSame(message, result, "Сообщение не должно было измениться");
                },
                "Не должно быть выброшено исключение для нечетной секунды");
    }

    @Test
    @DisplayName("Должен выбросить исключение, если секунда 0 (четная)")
    void process_shouldThrowException_whenSecondIsZero() {
        var message = new Message.Builder(1L).field1("Test").build();
        var dateTimeProvider = mock(DateTimeProvider.class);
        when(dateTimeProvider.getCurrentDateTime()).thenReturn(LocalDateTime.of(2023, 1, 1, 10, 30, 0));

        var processor = new ProcessorThrowExceptionEvenSecond(dateTimeProvider);

        assertThrows(
                EvenSecondException.class,
                () -> processor.process(message),
                "Должно быть выброшено EvenSecondException для нулевой секунды");
    }
}
