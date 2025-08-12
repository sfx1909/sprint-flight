package github.sfx.sprint_flight.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlightInfo {
    @JsonProperty("number")
    private String number;
    
    @JsonProperty("iata")
    private String iata;
    
    @JsonProperty("icao")
    private String icao;
    
    @JsonProperty("codeshared")
    private Codeshared codeshared;
    
    // Constructors
    public FlightInfo() {}
    
    // Getters and Setters
    public String getNumber() {
        return number;
    }
    
    public void setNumber(String number) {
        this.number = number;
    }
    
    public String getIata() {
        return iata;
    }
    
    public void setIata(String iata) {
        this.iata = iata;
    }
    
    public String getIcao() {
        return icao;
    }
    
    public void setIcao(String icao) {
        this.icao = icao;
    }
    
    public Codeshared getCodeshared() {
        return codeshared;
    }
    
    public void setCodeshared(Codeshared codeshared) {
        this.codeshared = codeshared;
    }
    
    // Inner class for codeshared flights
    public static class Codeshared {
        @JsonProperty("airline_name")
        private String airlineName;
        
        @JsonProperty("airline_iata")
        private String airlineIata;
        
        @JsonProperty("airline_icao")
        private String airlineIcao;
        
        @JsonProperty("flight_number")
        private String flightNumber;
        
        @JsonProperty("flight_iata")
        private String flightIata;
        
        @JsonProperty("flight_icao")
        private String flightIcao;
        
        // Constructors
        public Codeshared() {}
        
        // Getters and Setters
        public String getAirlineName() {
            return airlineName;
        }
        
        public void setAirlineName(String airlineName) {
            this.airlineName = airlineName;
        }
        
        public String getAirlineIata() {
            return airlineIata;
        }
        
        public void setAirlineIata(String airlineIata) {
            this.airlineIata = airlineIata;
        }
        
        public String getAirlineIcao() {
            return airlineIcao;
        }
        
        public void setAirlineIcao(String airlineIcao) {
            this.airlineIcao = airlineIcao;
        }
        
        public String getFlightNumber() {
            return flightNumber;
        }
        
        public void setFlightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
        }
        
        public String getFlightIata() {
            return flightIata;
        }
        
        public void setFlightIata(String flightIata) {
            this.flightIata = flightIata;
        }
        
        public String getFlightIcao() {
            return flightIcao;
        }
        
        public void setFlightIcao(String flightIcao) {
            this.flightIcao = flightIcao;
        }
    }
}
