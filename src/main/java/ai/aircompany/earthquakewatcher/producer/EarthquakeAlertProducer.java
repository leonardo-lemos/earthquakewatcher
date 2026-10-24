package ai.aircompany.earthquakewatcher.producer;

import ai.aircompany.earthquakewatcher.domain.service.EarthquakeIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EarthquakeAlertProducer {

    private final EarthquakeIngestionService earthquakeIngestionService;

    @Scheduled(cron = "0 0/10 * * * ?") // Every 10 minutes
    public void ingestEarthquakeData() {
        log.info("[PRODUCER] Ingesting earthquake data...");

        earthquakeIngestionService.collectAndPublishEarthquakeAlerts();
    }
}
