package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;

public class ProcessorThrowExceptionEvenSecond implements Processor {

    private final DateTimeProvider dateTimeProvider;

    public ProcessorThrowExceptionEvenSecond(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public Message process(Message message) {
        int currentSecond = dateTimeProvider.getCurrentDateTime().getSecond();
        if (currentSecond % 2 == 0) {
            throw new EvenSecondException("Exception thrown because current second is even: " + currentSecond);
        }
        return message;
    }
}
