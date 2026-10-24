package ai.aircompany.earthquakewatcher.domain.service;

import ai.aircompany.earthquakewatcher.configuration.RabbitMQConfiguration;
import ai.aircompany.earthquakewatcher.domain.dto.EarthquakeEvent;
import ai.aircompany.earthquakewatcher.domain.dto.EarthquakeFeed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class EarthquakeIngestionService {

    private final RabbitTemplate rabbitTemplate;

    private final WebClient usgsWebClient;

    public void collectAndPublishEarthquakeAlerts() {
        var earthquakeFeed = fetchEarthquakeAllDayFeed();

        if (earthquakeFeed != null && earthquakeFeed.features() != null) {
            earthquakeFeed.features().forEach(feature -> {
                // Publish each earthquake feature to RabbitMQ
                var properties = feature.properties();

                var id = feature.id();
                var magnitude = properties.mag();
                var place = properties.place();
                var timestamp = properties.time();
                var tsunami = properties.tsunami() == 1;

                // Severity logic
                var severity = (magnitude >= 4.5) ? "critical" : (magnitude >= 2.5 ? "medium" : "low");
                var tsunamiRisk = tsunami ? "tsunami" : "no_tsunami";

                // Routing key building based on severity and tsunami risk
                var routingKey = String.format("alert.%s.%s", severity, tsunamiRisk);

                var event = new EarthquakeEvent(id, magnitude, place, timestamp, severity, tsunami);

                // Publish the event to RabbitMQ with the constructed routing key
                rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_TOPIC, routingKey, event);

                log.info("[PRODUCER] Published earthquake event: {} with routing key: {}", event, routingKey);
            });
        }
    }

    private EarthquakeFeed fetchEarthquakeAllDayFeed() {
        return usgsWebClient.get()
                .uri("/earthquakes/feed/v1.0/summary/all_day.geojson")
                .retrieve()
                .bodyToMono(EarthquakeFeed.class)
                .block();
    }

}
