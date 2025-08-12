package github.sfx.sprint_flight.service;

import github.sfx.sprint_flight.model.Flight;
import github.sfx.sprint_flight.model.FlightResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightService {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);
    
    @Autowired
    private WebClient webClient;
    
    @Value("${flight.api.key}")
    private String apiKey;
    
    @Value("${flight.api.timeout:10000}")
    private int timeout;
    
    /**
     * Validate API key configuration on service initialization
     */
    @PostConstruct
    public void validateConfiguration() {
        if (apiKey == null || apiKey.trim().isEmpty() || 
            apiKey.contains("your_") || apiKey.contains("YOUR_")) {
            logger.error("⚠️  SECURITY ERROR: Flight API key not properly configured!");
            logger.error("   Please set FLIGHT_API_KEY environment variable or create .env file");
            throw new IllegalStateException("Flight API key not configured - check your environment variables");
        }
        logger.info("✅ Flight API configuration validated");
    }
    
    /**
     * Get flights by airline IATA code
     */
    public List<Flight> getFlightsByAirline(String airlineIata, int limit) {
        logger.info("Fetching flights for airline: {}", airlineIata);
        
        try {
            FlightResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/flights")
                            .queryParam("access_key", apiKey)
                            .queryParam("airline_iata", airlineIata)
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .bodyToMono(FlightResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();
            
            return response != null ? response.getData() : new ArrayList<>();
        } catch (WebClientResponseException e) {
            logger.error("API error when fetching flights by airline {}: {}", airlineIata, e.getMessage());
            return handleApiError(e);
        } catch (Exception e) {
            logger.error("Unexpected error when fetching flights by airline {}: {}", airlineIata, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get flights by departure airport IATA code
     */
    public List<Flight> getFlightsByDepartureAirport(String departureIata, int limit) {
        logger.info("Fetching flights from departure airport: {}", departureIata);
        
        try {
            FlightResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/flights")
                            .queryParam("access_key", apiKey)
                            .queryParam("dep_iata", departureIata)
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .bodyToMono(FlightResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();
            
            return response != null ? response.getData() : new ArrayList<>();
        } catch (WebClientResponseException e) {
            logger.error("API error when fetching flights by departure airport {}: {}", departureIata, e.getMessage());
            return handleApiError(e);
        } catch (Exception e) {
            logger.error("Unexpected error when fetching flights by departure airport {}: {}", departureIata, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get flights by arrival airport IATA code
     */
    public List<Flight> getFlightsByArrivalAirport(String arrivalIata, int limit) {
        logger.info("Fetching flights to arrival airport: {}", arrivalIata);
        
        try {
            FlightResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/flights")
                            .queryParam("access_key", apiKey)
                            .queryParam("arr_iata", arrivalIata)
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .bodyToMono(FlightResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();
            
            return response != null ? response.getData() : new ArrayList<>();
        } catch (WebClientResponseException e) {
            logger.error("API error when fetching flights by arrival airport {}: {}", arrivalIata, e.getMessage());
            return handleApiError(e);
        } catch (Exception e) {
            logger.error("Unexpected error when fetching flights by arrival airport {}: {}", arrivalIata, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get flights by route (departure to arrival)
     */
    public List<Flight> getFlightsByRoute(String departureIata, String arrivalIata, int limit) {
        logger.info("Fetching flights from {} to {}", departureIata, arrivalIata);
        
        try {
            FlightResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/flights")
                            .queryParam("access_key", apiKey)
                            .queryParam("dep_iata", departureIata)
                            .queryParam("arr_iata", arrivalIata)
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .bodyToMono(FlightResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();
            
            return response != null ? response.getData() : new ArrayList<>();
        } catch (WebClientResponseException e) {
            logger.error("API error when fetching flights by route {} to {}: {}", departureIata, arrivalIata, e.getMessage());
            return handleApiError(e);
        } catch (Exception e) {
            logger.error("Unexpected error when fetching flights by route {} to {}: {}", departureIata, arrivalIata, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get flight by flight number and airline
     */
    public List<Flight> getFlightByNumber(String flightIata, int limit) {
        logger.info("Fetching flight with number: {}", flightIata);
        
        try {
            FlightResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/flights")
                            .queryParam("access_key", apiKey)
                            .queryParam("flight_iata", flightIata)
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .bodyToMono(FlightResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();
            
            return response != null ? response.getData() : new ArrayList<>();
        } catch (WebClientResponseException e) {
            logger.error("API error when fetching flight by number {}: {}", flightIata, e.getMessage());
            return handleApiError(e);
        } catch (Exception e) {
            logger.error("Unexpected error when fetching flight by number {}: {}", flightIata, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get all active flights (limited)
     */
    public List<Flight> getAllActiveFlights(int limit) {
        logger.info("Fetching all active flights with limit: {}", limit);
        
        try {
            FlightResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/flights")
                            .queryParam("access_key", apiKey)
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .bodyToMono(FlightResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();
            
            return response != null ? response.getData() : new ArrayList<>();
        } catch (WebClientResponseException e) {
            logger.error("API error when fetching all active flights: {}", e.getMessage());
            return handleApiError(e);
        } catch (Exception e) {
            logger.error("Unexpected error when fetching all active flights: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<Flight> handleApiError(WebClientResponseException e) {
        if (e.getRawStatusCode() == 401) {
            logger.error("Authentication failed. Please check your API key.");
        } else if (e.getRawStatusCode() == 429) {
            logger.error("Rate limit exceeded. Please try again later.");
        } else if (e.getRawStatusCode() == 403) {
            logger.error("Access forbidden. Please check your API subscription.");
        }
        return new ArrayList<>();
    }
}
