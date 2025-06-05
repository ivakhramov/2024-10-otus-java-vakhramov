package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;

public class ProcessorSwapFields11And12 implements Processor {

    @Override
    public Message process(Message message) {
        String field11Value = message.getField11();
        String field12Value = message.getField12();
        return message.toBuilder().field11(field12Value).field12(field11Value).build();
    }
}
