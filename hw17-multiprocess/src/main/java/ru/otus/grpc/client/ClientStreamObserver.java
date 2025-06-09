package ru.otus.grpc.client;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.grpc.generated.NumberResponse;

public class ClientStreamObserver implements StreamObserver<NumberResponse> {

    private static final Logger log = LoggerFactory.getLogger(ClientStreamObserver.class);

    private final AtomicLong lastValue = new AtomicLong(0);

    @Override
    public void onNext(NumberResponse response) {
        log.info("new value: {}", response.getNumber());
        this.lastValue.set(response.getNumber());
    }

    @Override
    public void onError(Throwable t) {
        log.error("Error occurred", t);
    }

    @Override
    public void onCompleted() {
        log.info("Request completed from server side.");
    }

    public long getLastValueAndReset() {
        return this.lastValue.getAndSet(0);
    }
}
