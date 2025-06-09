package ru.otus.grpc.server;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.grpc.generated.NumberRequest;
import ru.otus.grpc.generated.NumberResponse;
import ru.otus.grpc.generated.NumbersServiceGrpc;

public class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(NumbersServiceImpl.class);

    @Override
    public void getNumbers(NumberRequest request, StreamObserver<NumberResponse> responseObserver) {
        log.info(
                "Request for a new sequence of numbers, firstValue: {}, lastValue: {}",
                request.getFirstValue(),
                request.getLastValue());

        long firstValue = request.getFirstValue();
        long lastValue = request.getLastValue();
        AtomicLong currentValue = new AtomicLong(firstValue);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        try {
            Runnable task = () -> {
                long value = currentValue.incrementAndGet();
                NumberResponse response =
                        NumberResponse.newBuilder().setNumber(value).build();

                responseObserver.onNext(response);

                if (value == lastValue) {
                    executor.shutdown();
                    responseObserver.onCompleted();
                    log.info("Sequence of numbers finished.");
                }
            };
            executor.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error in number generation", e);
            responseObserver.onError(e);
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        }
    }
}
