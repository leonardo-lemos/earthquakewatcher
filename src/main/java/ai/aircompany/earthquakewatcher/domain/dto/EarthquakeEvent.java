package ai.aircompany.earthquakewatcher.domain.dto;

public record EarthquakeEvent(
        String id,
        Double magnitude,
        String place,
        Long timestamp,
        String alertLevel,
        Boolean hasTsunamiWarning
) {
}
