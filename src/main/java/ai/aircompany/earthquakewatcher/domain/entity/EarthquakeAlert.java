package ai.aircompany.earthquakewatcher.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "earthquake_alerts")
public record EarthquakeAlert(
        @Id String id,
        Double magnitude,
        String place,
        Instant occurredAt,
        String alertLevel,
        Boolean hasTsunamiWarning
) {
}
