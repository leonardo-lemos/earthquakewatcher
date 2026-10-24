package ai.aircompany.earthquakewatcher.consumer;

import ai.aircompany.earthquakewatcher.configuration.RabbitMQConfiguration;
import ai.aircompany.earthquakewatcher.domain.dto.EarthquakeEvent;
import ai.aircompany.earthquakewatcher.domain.service.EarthquakeAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EarthquakeAlertConsumer {

    private final EarthquakeAlertService earthquakeAlertService;

    @RabbitListener(queues = RabbitMQConfiguration.EMERGENCY_QUEUE, containerFactory = "prefetchOneContainerFactory")
    public void handleEmergencyQueue(EarthquakeEvent event) {
        log.info("[CONSUMER] Received event from emergency queue: {}", event);

        // Send alert to user for emergency events
        try {
            earthquakeAlertService.sendAlert(event);
        } catch (Exception e) {
            log.error("[CONSUMER] Error processing event from emergency queue: {}", event, e);

            throw new AmqpRejectAndDontRequeueException("Error processing event from emergency queue", e);
        }

    }

    @RabbitListener(queues = RabbitMQConfiguration.STATISTICS_QUEUE)
    public void handleStatisticsQueue(EarthquakeEvent event) {
        log.info("[CONSUMER] Received event from statistics queue: {}", event);

        // Save alert to database for statistics events
        try {
            earthquakeAlertService.saveAlert(event);
        } catch (Exception e) {
            log.error("[CONSUMER] Error processing event from statistics queue: {}", event, e);

            throw new AmqpRejectAndDontRequeueException("Error processing event from statistics queue", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfiguration.DLQ_QUEUE)
    public void handleDLQQueue(EarthquakeEvent event) {
        log.warn("[CONSUMER] Received event from DLQ queue: {}", event);
    }
}
