package github.sfx.sprint_flight.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Departure {
    @JsonProperty("airport")
    private String airport;
    
    @JsonProperty("timezone")
    private String timezone;
    
    @JsonProperty("iata")
    private String iata;
    
    @JsonProperty("icao")
    private String icao;
    
    @JsonProperty("terminal")
    private String terminal;
    
    @JsonProperty("gate")
    private String gate;
    
    @JsonProperty("delay")
    private Integer delay;
    
    @JsonProperty("scheduled")
    private String scheduled;
    
    @JsonProperty("estimated")
    private String estimated;
    
    @JsonProperty("actual")
    private String actual;
    
    // Constructors
    public Departure() {}
    
    // Getters and Setters
    public String getAirport() {
        return airport;
    }
    
    public void setAirport(String airport) {
        this.airport = airport;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
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
    
    public String getTerminal() {
        return terminal;
    }
    
    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }
    
    public String getGate() {
        return gate;
    }
    
    public void setGate(String gate) {
        this.gate = gate;
    }
    
    public Integer getDelay() {
        return delay;
    }
    
    public void setDelay(Integer delay) {
        this.delay = delay;
    }
    
    public String getScheduled() {
        return scheduled;
    }
    
    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }
    
    public String getEstimated() {
        return estimated;
    }
    
    public void setEstimated(String estimated) {
        this.estimated = estimated;
    }
    
    public String getActual() {
        return actual;
    }
    
    public void setActual(String actual) {
        this.actual = actual;
    }
}
