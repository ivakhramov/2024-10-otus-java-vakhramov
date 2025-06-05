package ru.otus.listener.homework;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ru.otus.listener.Listener;
import ru.otus.model.Message;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> history = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        if (msg == null) {
            System.err.println("HistoryListener received a null message in onUpdated.");
            return;
        }
        Message messageCopy = msg.toBuilder().build();
        history.put(messageCopy.getId(), messageCopy);
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        Message originalMessage = history.get(id);
        if (originalMessage != null) {
            return Optional.of(originalMessage.toBuilder().build());
        }
        return Optional.empty();
    }
}
