package ai.aircompany.earthquakewatcher.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NtfyWebClientConfiguration {

    @Value("${ntfy.api.base-url}")
    private String ntfyApiUrl;

    @Bean
    public WebClient ntfyWebClient() {
        return WebClient.builder().baseUrl(ntfyApiUrl).build();
    }
}
