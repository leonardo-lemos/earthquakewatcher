package ai.aircompany.earthquakewatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EarthquakewatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(EarthquakewatcherApplication.class, args);
	}

}
