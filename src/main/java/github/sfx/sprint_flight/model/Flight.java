package github.sfx.sprint_flight.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Flight {
    @JsonProperty("flight_date")
    private String flightDate;
    
    @JsonProperty("flight_status")
    private String flightStatus;
    
    @JsonProperty("departure")
    private Departure departure;
    
    @JsonProperty("arrival")
    private Arrival arrival;
    
    @JsonProperty("airline")
    private Airline airline;
    
    @JsonProperty("flight")
    private FlightInfo flight;
    
    // Constructors
    public Flight() {}
    
    // Getters and Setters
    public String getFlightDate() {
        return flightDate;
    }
    
    public void setFlightDate(String flightDate) {
        this.flightDate = flightDate;
    }
    
    public String getFlightStatus() {
        return flightStatus;
    }
    
    public void setFlightStatus(String flightStatus) {
        this.flightStatus = flightStatus;
    }
    
    public Departure getDeparture() {
        return departure;
    }
    
    public void setDeparture(Departure departure) {
        this.departure = departure;
    }
    
    public Arrival getArrival() {
        return arrival;
    }
    
    public void setArrival(Arrival arrival) {
        this.arrival = arrival;
    }
    
    public Airline getAirline() {
        return airline;
    }
    
    public void setAirline(Airline airline) {
        this.airline = airline;
    }
    
    public FlightInfo getFlight() {
        return flight;
    }
    
    public void setFlight(FlightInfo flight) {
        this.flight = flight;
    }
}
