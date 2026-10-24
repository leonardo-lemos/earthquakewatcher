package ai.aircompany.earthquakewatcher.domain.service;

import ai.aircompany.earthquakewatcher.domain.dto.EarthquakeEvent;
import ai.aircompany.earthquakewatcher.domain.entity.EarthquakeAlert;
import ai.aircompany.earthquakewatcher.domain.repository.EarthquakeAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class EarthquakeAlertService {

    private final WebClient ntfyWebClient;

    private final EarthquakeAlertRepository earthquakeAlertRepository;

    @Value("${ntfy.topic}")
    private String ntfyTopic;

    public void sendAlert(EarthquakeEvent event) {
        log.info("[ALERT SERVICE] Sending alert for event: {}", event);

        assertValidEvent(event);

        sendToNtfy(event);
    }

    public void saveAlert(EarthquakeEvent event) {
        log.info("[ALERT SERVICE] Saving alert for event: {}", event);

        assertValidEvent(event);

        var alert = new EarthquakeAlert(
                event.id(),
                event.magnitude(),
                event.place(),
                Instant.ofEpochMilli(event.timestamp()),
                event.alertLevel(),
                event.hasTsunamiWarning()
        );

        earthquakeAlertRepository.save(alert);
    }

    private void assertValidEvent(EarthquakeEvent event) {
        Assert.notNull(event, "EarthquakeEvent must not be null");
        Assert.hasText(event.id(), "EarthquakeEvent id must not be empty");
        Assert.notNull(event.magnitude(), "EarthquakeEvent magnitude must not be null");
        Assert.hasText(event.place(), "EarthquakeEvent place must not be empty");
        Assert.notNull(event.timestamp(), "EarthquakeEvent timestamp must not be null");
        Assert.hasText(event.alertLevel(), "EarthquakeEvent alertLevel must not be empty");
        Assert.notNull(event.hasTsunamiWarning(), "EarthquakeEvent hasTsunamiWarning must not be null");
    }

    private void sendToNtfy(EarthquakeEvent event) {
        ntfyWebClient.post()
                .uri("/" + ntfyTopic)
                .header("Title", "\uD83D\uDEA8 ALERTA DEFESA CIVIL: Terremoto!")
                .header("Priority", "urgent")
                .header("Tags", "warning,earthquake")
                .bodyValue(String.format("Magnitude %.1f registrada em %s!", event.magnitude(), event.place()))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }
}
