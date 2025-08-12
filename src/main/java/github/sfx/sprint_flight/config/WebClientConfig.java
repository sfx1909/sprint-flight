package github.sfx.sprint_flight.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;

@Configuration
public class WebClientConfig {
    
    @Value("${flight.api.base-url}")
    private String baseUrl;
    
    @Value("${flight.api.timeout:10000}")
    private int timeout;
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(1024 * 1024)) // 1MB
                .build();
    }
}
