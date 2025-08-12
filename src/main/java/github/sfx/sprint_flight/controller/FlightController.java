package github.sfx.sprint_flight.controller;

import github.sfx.sprint_flight.model.Flight;
import github.sfx.sprint_flight.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);
    
    @Autowired
    private FlightService flightService;
    
    /**
     * Welcome endpoint
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Sprint Flight API");
        response.put("version", "1.0.0");
        response.put("endpoints", Map.of(
            "GET /api/flights/airline/{airlineIata}", "Get flights by airline IATA code",
            "GET /api/flights/departure/{departureIata}", "Get flights by departure airport",
            "GET /api/flights/arrival/{arrivalIata}", "Get flights by arrival airport",
            "GET /api/flights/route/{departureIata}/{arrivalIata}", "Get flights by route",
            "GET /api/flights/number/{flightIata}", "Get flight by flight number",
            "GET /api/flights/active", "Get all active flights (limited)"
        ));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get flights by airline IATA code
     * Example: /api/flights/airline/AA?limit=10
     */
    @GetMapping("/airline/{airlineIata}")
    public ResponseEntity<Map<String, Object>> getFlightsByAirline(
            @PathVariable String airlineIata,
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("Request received for flights by airline: {}", airlineIata);
        
        if (limit > 100) {
            limit = 100; // Cap the limit to prevent excessive API calls
        }
        
        List<Flight> flights = flightService.getFlightsByAirline(airlineIata.toUpperCase(), limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("airline", airlineIata.toUpperCase());
        response.put("count", flights.size());
        response.put("flights", flights);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get flights by departure airport IATA code
     * Example: /api/flights/departure/JFK?limit=10
     */
    @GetMapping("/departure/{departureIata}")
    public ResponseEntity<Map<String, Object>> getFlightsByDepartureAirport(
            @PathVariable String departureIata,
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("Request received for flights from departure airport: {}", departureIata);
        
        if (limit > 100) {
            limit = 100;
        }
        
        List<Flight> flights = flightService.getFlightsByDepartureAirport(departureIata.toUpperCase(), limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("departure_airport", departureIata.toUpperCase());
        response.put("count", flights.size());
        response.put("flights", flights);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get flights by arrival airport IATA code
     * Example: /api/flights/arrival/LAX?limit=10
     */
    @GetMapping("/arrival/{arrivalIata}")
    public ResponseEntity<Map<String, Object>> getFlightsByArrivalAirport(
            @PathVariable String arrivalIata,
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("Request received for flights to arrival airport: {}", arrivalIata);
        
        if (limit > 100) {
            limit = 100;
        }
        
        List<Flight> flights = flightService.getFlightsByArrivalAirport(arrivalIata.toUpperCase(), limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("arrival_airport", arrivalIata.toUpperCase());
        response.put("count", flights.size());
        response.put("flights", flights);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get flights by route (departure to arrival)
     * Example: /api/flights/route/JFK/LAX?limit=10
     */
    @GetMapping("/route/{departureIata}/{arrivalIata}")
    public ResponseEntity<Map<String, Object>> getFlightsByRoute(
            @PathVariable String departureIata,
            @PathVariable String arrivalIata,
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("Request received for flights from {} to {}", departureIata, arrivalIata);
        
        if (limit > 100) {
            limit = 100;
        }
        
        List<Flight> flights = flightService.getFlightsByRoute(
                departureIata.toUpperCase(), 
                arrivalIata.toUpperCase(), 
                limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("route", departureIata.toUpperCase() + " → " + arrivalIata.toUpperCase());
        response.put("departure_airport", departureIata.toUpperCase());
        response.put("arrival_airport", arrivalIata.toUpperCase());
        response.put("count", flights.size());
        response.put("flights", flights);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get flight by flight number
     * Example: /api/flights/number/AA100?limit=5
     */
    @GetMapping("/number/{flightIata}")
    public ResponseEntity<Map<String, Object>> getFlightByNumber(
            @PathVariable String flightIata,
            @RequestParam(defaultValue = "5") int limit) {
        
        logger.info("Request received for flight number: {}", flightIata);
        
        if (limit > 50) {
            limit = 50;
        }
        
        List<Flight> flights = flightService.getFlightByNumber(flightIata.toUpperCase(), limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("flight_number", flightIata.toUpperCase());
        response.put("count", flights.size());
        response.put("flights", flights);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all active flights (limited to prevent excessive API usage)
     * Example: /api/flights/active?limit=20
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getAllActiveFlights(
            @RequestParam(defaultValue = "20") int limit) {
        
        logger.info("Request received for all active flights");
        
        if (limit > 50) {
            limit = 50; // Stricter limit for general queries
        }
        
        List<Flight> flights = flightService.getAllActiveFlights(limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "active_flights");
        response.put("limit", limit);
        response.put("count", flights.size());
        response.put("flights", flights);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "flight-api");
        response.put("timestamp", java.time.Instant.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Error handling for invalid requests
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Error in FlightController: ", e);
        
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", e.getMessage());
        response.put("timestamp", java.time.Instant.now().toString());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
