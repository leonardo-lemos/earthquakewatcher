package ai.aircompany.earthquakewatcher.consumer;

import ai.aircompany.earthquakewatcher.domain.dto.EarthquakeEvent;
import ai.aircompany.earthquakewatcher.domain.service.EarthquakeAlertService;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class EarthquakeAlertConsumerTests {

    private final EarthquakeAlertService service = mock(EarthquakeAlertService.class);
    private final EarthquakeAlertConsumer listeners = new EarthquakeAlertConsumer(service);

    @Test
    void statisticsQueuePersistsAlertWithoutSendingNotification() {
        var event = new EarthquakeEvent("us7000test", 2.0, "Test location",
                1_700_000_000_123L, "low", false);

        listeners.handleStatisticsQueue(event);

        verify(service).saveAlert(event);
        verifyNoMoreInteractions(service);
    }

    @Test
    void statisticsQueuePropagatesPersistenceFailure() {
        var event = new EarthquakeEvent("us7000test", 2.0, "Test location",
                1_700_000_000_123L, "low", false);
        var failure = new DataAccessResourceFailureException("MongoDB unavailable");
        doThrow(failure).when(service).saveAlert(event);

        assertThatThrownBy(() -> listeners.handleStatisticsQueue(event)).isSameAs(failure);
    }
}
