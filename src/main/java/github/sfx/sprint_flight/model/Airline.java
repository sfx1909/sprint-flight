package github.sfx.sprint_flight.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Airline {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("iata")
    private String iata;
    
    @JsonProperty("icao")
    private String icao;
    
    // Constructors
    public Airline() {}
    
    public Airline(String name, String iata, String icao) {
        this.name = name;
        this.iata = iata;
        this.icao = icao;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
}
