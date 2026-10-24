package ai.aircompany.earthquakewatcher.domain.service;

import ai.aircompany.earthquakewatcher.domain.dto.EarthquakeEvent;
import ai.aircompany.earthquakewatcher.domain.entity.EarthquakeAlert;
import ai.aircompany.earthquakewatcher.domain.repository.EarthquakeAlertRepository;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EarthquakeAlertServiceTests {

    private final EarthquakeAlertRepository repository = mock(EarthquakeAlertRepository.class);
    private final WebClient webClient = mock(WebClient.class);
    private final EarthquakeAlertService service = new EarthquakeAlertService(webClient, repository);

    @Test
    void savesEventWithSourceIdAndOccurrenceTime() {
        var event = new EarthquakeEvent("us7000test", 5.2, "Test location",
                1_700_000_000_123L, "critical", true);

        service.saveAlert(event);

        verify(repository).save(new EarthquakeAlert(
                "us7000test", 5.2, "Test location",
                Instant.ofEpochMilli(1_700_000_000_123L), "critical", true));
        verifyNoInteractions(webClient);
    }

    @Test
    void rejectsMissingIdToPreventGeneratedIdsAndDuplicateStatistics() {
        var event = new EarthquakeEvent(null, 5.2, "Test location",
                1_700_000_000_123L, "critical", false);

        assertThatThrownBy(() -> service.saveAlert(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Earthquake event ID must not be blank");

        verifyNoInteractions(repository);
    }

    @Test
    void rejectsMissingTimestamp() {
        var event = new EarthquakeEvent("us7000test", 5.2, "Test location",
                null, "critical", false);

        assertThatThrownBy(() -> service.saveAlert(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Earthquake event timestamp must not be null");

        verifyNoInteractions(repository);
    }

    @Test
    void propagatesPersistenceFailureForMessageErrorHandling() {
        var event = new EarthquakeEvent("us7000test", 5.2, "Test location",
                1_700_000_000_123L, "critical", false);
        var failure = new DataAccessResourceFailureException("MongoDB unavailable");
        when(repository.save(any(EarthquakeAlert.class))).thenThrow(failure);

        assertThatThrownBy(() -> service.saveAlert(event)).isSameAs(failure);
    }
}
