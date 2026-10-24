package ai.aircompany.earthquakewatcher.domain.repository;

import ai.aircompany.earthquakewatcher.domain.entity.EarthquakeAlert;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EarthquakeAlertRepository extends MongoRepository<EarthquakeAlert, String> {
}
