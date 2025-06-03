package ru.otus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.ListenerPrinterConsole;
import ru.otus.listener.homework.HistoryListener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.homework.DateTimeProvider;
import ru.otus.processor.homework.ProcessorSwapFields11And12;
import ru.otus.processor.homework.ProcessorThrowExceptionEvenSecond;

public class HomeWork {

    /*
    Реализовать to do:
      1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
      2. Сделать процессор, который поменяет местами значения field11 и field12
      3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
            Секунда должна определяьться во время выполнения.
            Тест - важная часть задания
            Обязательно посмотрите пример к паттерну Мементо!
      4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
         Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
         Для него уже есть тест, убедитесь, что тест проходит
    */

    public static void main(String[] args) {
        /*
          по аналогии с Demo.class
          из элеменов "to do" создать new ComplexProcessor и обработать сообщение
        */

        DateTimeProvider realTimeProvider = LocalDateTime::now;

        var processors =
                List.of(new ProcessorSwapFields11And12(), new ProcessorThrowExceptionEvenSecond(realTimeProvider));

        var complexProcessor = new ComplexProcessor(processors, ex -> {
            System.err.println("Processor exception: " + ex.getMessage());
        });

        var historyListener = new HistoryListener();
        complexProcessor.addListener(historyListener);
        var consoleListener = new ListenerPrinterConsole();
        complexProcessor.addListener(consoleListener);

        long messageId = 1L;
        var field13InitialData = new ArrayList<String>();
        field13InitialData.add("data1");
        field13InitialData.add("data2");

        var objectForMessage = new ObjectForMessage();
        objectForMessage.setData(field13InitialData);

        var message = new Message.Builder(messageId)
                .field1("value1")
                .field2("value2")
                .field10("value10")
                .field11("value11_initial")
                .field12("value12_initial")
                .field13(objectForMessage)
                .build();

        System.out.println("Initial message: " + message);

        Message result = complexProcessor.handle(message);
        System.out.println("Result message: " + result);

        var messageFromHistory = historyListener.findMessageById(messageId);
        System.out.println("Message from history by ID " + messageId + ": " + messageFromHistory.orElse(null));

        if (messageFromHistory.isPresent()) {
            field13InitialData.add("newDataAfterProcessing");
            objectForMessage.setData(new ArrayList<>(List.of("completelyNewData")));

            System.out.println("Original message's field13 after modification (should not affect history): "
                    + message.getField13().getData());
            System.out.println("Message from history (field13 should be unchanged): "
                    + messageFromHistory.get().getField13().getData());

            if (messageFromHistory.get().getField13().getData().contains("data1")
                    && !messageFromHistory.get().getField13().getData().contains("newDataAfterProcessing")
                    && !messageFromHistory.get().getField13().getData().contains("completelyNewData")) {
                System.out.println("HistoryListener correctly preserved field13's state!");
            } else {
                System.err.println("HistoryListener FAILED to preserve field13's state correctly.");
            }
        }

        long messageId2 = 2L;
        var message2 = new Message.Builder(messageId2)
                .field11("m2_field11")
                .field12("m2_field12")
                .build();
        System.out.println("\nInitial message2: " + message2);
        Message result2 = complexProcessor.handle(message2);
        System.out.println("Result message2: " + result2);
        var message2FromHistory = historyListener.findMessageById(messageId2);
        System.out.println("Message2 from history: " + message2FromHistory.orElse(null));

        complexProcessor.removeListener(historyListener);
        complexProcessor.removeListener(consoleListener);
    }
}
