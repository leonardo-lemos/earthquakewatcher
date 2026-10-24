package ai.aircompany.earthquakewatcher.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class USGSWebClientConfiguration {

    @Value("${usgs.api.base-url}")
    private String usgsApiUrl;

    @Bean
    public WebClient usgsWebClient() {
        return WebClient.builder().baseUrl(usgsApiUrl).build();
    }
}
