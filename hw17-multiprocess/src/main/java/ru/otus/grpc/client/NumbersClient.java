package ru.otus.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.grpc.generated.NumberRequest;
import ru.otus.grpc.generated.NumbersServiceGrpc;

public class NumbersClient {

    private static final Logger log = LoggerFactory.getLogger(NumbersClient.class);

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final int LOOP_LIMIT = 50;
    private long currentValue = 0;

    public static void main(String[] args) throws InterruptedException {
        log.info("Numbers client is starting...");

        ManagedChannel channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        var asyncStub = NumbersServiceGrpc.newStub(channel);

        new NumbersClient().run(asyncStub);

        log.info("Numbers client is shutting down...");
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

    private void run(NumbersServiceGrpc.NumbersServiceStub asyncStub) {
        NumberRequest request =
                NumberRequest.newBuilder().setFirstValue(0).setLastValue(30).build();

        ClientStreamObserver observer = new ClientStreamObserver();

        asyncStub.getNumbers(request, observer);

        for (int i = 0; i < LOOP_LIMIT; i++) {
            long lastValueFromServer = observer.getLastValueAndReset();

            currentValue = currentValue + lastValueFromServer + 1;
            log.info("currentValue: {}", currentValue);

            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
