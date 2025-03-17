package ua.in.photomap.geojsongenerator.listener;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;
import ua.in.photomap.geojsongenerator.service.PhotoFeatureService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaListener {

    private final PhotoFeatureService photoFeatureService;
    private final KafkaReceiver<Integer, String> photoAddKafkaReceiver;
    private final KafkaReceiver<Integer, String> photoDeleteKafkaReceiver;
    private final KafkaReceiver<Integer, String> photoUpdateKafkaReceiver;

    @PostConstruct
    public void listen() {
        photoAddKafkaReceiver.receive()
                .flatMap(record -> {
                    log.info("Received SAVE message: {}", record.value());
                    return handleSave(record.value())
                            .doOnTerminate(() -> record.receiverOffset().acknowledge());
                })
                .subscribe();

        photoUpdateKafkaReceiver.receive()
                .flatMap(record -> {
                    log.info("Received UPDATE message: {}", record.value());
                    return handleUpdate(record.value())
                            .doOnTerminate(() -> record.receiverOffset().acknowledge());
                })
                .subscribe();

        photoDeleteKafkaReceiver.receive()
                .flatMap(record -> {
                    log.info("Received DELETE message: {}", record.value());
                    return handleDelete(record.value())
                            .doOnTerminate(() -> record.receiverOffset().acknowledge());
                })
                .subscribe();
    }

    private static RetryBackoffSpec getRetrySpec() {
        return Retry.backoff(3, Duration.of(10L, ChronoUnit.SECONDS))
                .doAfterRetry(retrySignal -> log.warn("Retrying due to error", retrySignal.failure()))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
    }

    private Mono<Void> handleSave(String message) {
        return photoFeatureService.savePhotoFeature(message)
                .retryWhen(getRetrySpec())
                .onErrorResume(e -> {
                    log.error("Error saving photo feature: ", e);
                    return Mono.empty();
                });
    }

    private Mono<Void> handleUpdate(String message) {
        return photoFeatureService.savePhotoFeature(message)
                .retryWhen(getRetrySpec())
                .onErrorResume(e -> {
                    log.error("Error updating photo feature: ", e);
                    return Mono.empty();
                });
    }

    private Mono<Void> handleDelete(String message) {
        return photoFeatureService.deletePhotoFeature(Long.valueOf(message))
                .retryWhen(getRetrySpec())
                .onErrorResume(e -> {
                    log.error("Error deleting photo feature: ", e);
                    return Mono.empty();
                });
    }
}