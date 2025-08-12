package github.sfx.sprint_flight.service;

import github.sfx.sprint_flight.model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConversationService {
    
    @Autowired
    private NaturalLanguageProcessor nlpProcessor;
    
    @Autowired
    private FlightService flightService;
    
    public ConversationResponse processMessage(String userMessage) {
        NaturalLanguageProcessor.QueryIntent intent = nlpProcessor.parseQuery(userMessage);
        
        switch (intent.getType()) {
            case GREETING:
                return createGreetingResponse();
                
            case HELP:
                return createHelpResponse();
                
            case FLIGHT_NUMBER:
                return processFlightNumberQuery(intent);
                
            case ROUTE:
                return processRouteQuery(intent);
                
            case DEPARTURE:
                return processDepartureQuery(intent);
                
            case ARRIVAL:
                return processArrivalQuery(intent);
                
            case AIRLINE:
                return processAirlineQuery(intent);
                
            case ACTIVE_FLIGHTS:
                return processActiveFlightsQuery(intent);
                
            case UNKNOWN:
            default:
                return createUnknownResponse(userMessage);
        }
    }
    
    private ConversationResponse createGreetingResponse() {
        return new ConversationResponse(
            "✈️ **Welcome to Global Flight Search!** 🌍\\n\\n" +
            "Hello! I'm your AI flight assistant, ready to help you find flight information worldwide! 🛩️\\n\\n" +
            "**What I can help you with:**\\n" +
            "🌐 **Global Coverage** - Search flights across all continents\\n" +
            "🔍 **Natural Language** - Just tell me what you're looking for in plain English\\n" +
            "✈️ **International Airlines** - Emirates, British Airways, Singapore Airlines, and 100+ more\\n" +
            "🛫 **Major Airports** - From JFK to LHR, NRT to DXB, and 200+ worldwide hubs\\n\\n" +
            "**Try asking me:**\\n" +
            "• \\\"Flights from London to Tokyo\\\"\\n" +
            "• \\\"Emirates flights from Dubai\\\"\\n" +
            "• \\\"What's departing from Charles de Gaulle?\\\"\\n" +
            "• \\\"Singapore Airlines to Los Angeles\\\"\\n\\n" +
            "Type your flight query or say 'help' for more examples! 🚀",
            null,
            ConversationResponse.ResponseType.GREETING
        );
    }
    
    private ConversationResponse createHelpResponse() {
        return new ConversationResponse(
            "🛩️ **Global Flight Search Help**\\n\\n" +
            "I understand natural language queries about flights worldwide. Here are examples:\\n\\n" +
            "**✈️ International Route Searches:**\\n" +
            "• \\\"Flights from London to Tokyo\\\"\\n" +
            "• \\\"Show me routes from Dubai to New York\\\"\\n" +
            "• \\\"Paris to Singapore flights\\\"\\n" +
            "• \\\"Frankfurt to Bangkok routes\\\"\\n\\n" +
            "**🛫 Global Departure Searches:**\\n" +
            "• \\\"Flights leaving from Heathrow\\\"\\n" +
            "• \\\"What's departing from Charles de Gaulle?\\\"\\n" +
            "• \\\"Show departures from Narita\\\"\\n" +
            "• \\\"Flights from Changi airport\\\"\\n\\n" +
            "**🛬 International Arrival Searches:**\\n" +
            "• \\\"Flights arriving at LAX\\\"\\n" +
            "• \\\"What's landing in Dubai?\\\"\\n" +
            "• \\\"Arrivals at Schiphol\\\"\\n\\n" +
            "**✈️ Global Airline Searches:**\\n" +
            "• \\\"Emirates flights\\\"\\n" +
            "• \\\"Singapore Airlines departures\\\"\\n" +
            "• \\\"British Airways to New York\\\"\\n" +
            "• \\\"Lufthansa flights from Frankfurt\\\"\\n" +
            "• \\\"Qatar Airways routes\\\"\\n\\n" +
            "**🎯 International Flight Numbers:**\\n" +
            "• \\\"Flight EK215\\\" (Emirates)\\n" +
            "• \\\"Check flight BA456\\\" (British Airways)\\n" +
            "• \\\"SQ317 status\\\" (Singapore Airlines)\\n\\n" +
            "**🌍 Supported Regions:**\\n" +
            "• North America, Europe, Asia-Pacific\\n" +
            "• Middle East, Africa, South America\\n" +
            "• Major airports and airlines worldwide\\n\\n" +
            "You can use city names, airport names, or IATA codes (JFK, LHR, NRT, etc.)",
            null,
            ConversationResponse.ResponseType.INFO
        );
    }
    
    private ConversationResponse processFlightNumberQuery(NaturalLanguageProcessor.QueryIntent intent) {
        try {
            List<Flight> flights = flightService.getFlightByNumber(intent.getFlightNumber(), intent.getLimit());
            
            if (flights.isEmpty()) {
                return new ConversationResponse(
                    String.format("I couldn't find any information for flight %s. 🔍\\n\\n" +
                        "This could mean:\\n" +
                        "• The flight number doesn't exist today\\n" +
                        "• The flight might be for a different date\\n" +
                        "• There might be a typo in the flight number\\n\\n" +
                        "Try asking for flights by airline or route instead!", intent.getFlightNumber()),
                    null,
                    ConversationResponse.ResponseType.NO_RESULTS
                );
            }
            
            String response = String.format("✈️ **Flight %s Information** (%d result%s found)\\n\\n", 
                intent.getFlightNumber(), flights.size(), flights.size() == 1 ? "" : "s");
            
            return new ConversationResponse(response, flights, ConversationResponse.ResponseType.FLIGHT_DATA);
            
        } catch (Exception e) {
            return createErrorResponse("flight number lookup", e.getMessage());
        }
    }
    
    private ConversationResponse processRouteQuery(NaturalLanguageProcessor.QueryIntent intent) {
        try {
            List<Flight> flights = flightService.getFlightsByRoute(
                intent.getDepartureAirport(), 
                intent.getArrivalAirport(), 
                intent.getLimit()
            );
            
            if (flights.isEmpty()) {
                return new ConversationResponse(
                    String.format("No flights found from %s to %s today. 🔍\\n\\n" +
                        "This could mean:\\n" +
                        "• No direct flights on this route today\\n" +
                        "• Flights might be scheduled for different times\\n" +
                        "• Try checking individual airports or different airlines\\n\\n" +
                        "Would you like me to check departures from %s or arrivals at %s separately?", 
                        intent.getDepartureAirport(), intent.getArrivalAirport(), 
                        intent.getDepartureAirport(), intent.getArrivalAirport()),
                    null,
                    ConversationResponse.ResponseType.NO_RESULTS
                );
            }
            
            String response = String.format("🛫➡️🛬 **Flights from %s to %s** (%d result%s found)\\n\\n", 
                intent.getDepartureAirport(), intent.getArrivalAirport(), 
                flights.size(), flights.size() == 1 ? "" : "s");
            
            return new ConversationResponse(response, flights, ConversationResponse.ResponseType.FLIGHT_DATA);
            
        } catch (Exception e) {
            return createErrorResponse("route search", e.getMessage());
        }
    }
    
    private ConversationResponse processDepartureQuery(NaturalLanguageProcessor.QueryIntent intent) {
        try {
            List<Flight> flights = flightService.getFlightsByDepartureAirport(intent.getDepartureAirport(), intent.getLimit());
            
            if (flights.isEmpty()) {
                return new ConversationResponse(
                    String.format("No departing flights found from %s today. 🛫\\n\\n" +
                        "This could mean:\\n" +
                        "• No flights scheduled for departure right now\\n" +
                        "• The airport code might not be recognized\\n" +
                        "• Try checking active flights or different airports\\n\\n" +
                        "Would you like me to show you all active flights instead?", intent.getDepartureAirport()),
                    null,
                    ConversationResponse.ResponseType.NO_RESULTS
                );
            }
            
            String response = String.format("🛫 **Flights departing from %s** (%d result%s found)\\n\\n", 
                intent.getDepartureAirport(), flights.size(), flights.size() == 1 ? "" : "s");
            
            return new ConversationResponse(response, flights, ConversationResponse.ResponseType.FLIGHT_DATA);
            
        } catch (Exception e) {
            return createErrorResponse("departure search", e.getMessage());
        }
    }
    
    private ConversationResponse processArrivalQuery(NaturalLanguageProcessor.QueryIntent intent) {
        try {
            List<Flight> flights = flightService.getFlightsByArrivalAirport(intent.getArrivalAirport(), intent.getLimit());
            
            if (flights.isEmpty()) {
                return new ConversationResponse(
                    String.format("No arriving flights found at %s today. 🛬\\n\\n" +
                        "This could mean:\\n" +
                        "• No flights scheduled to arrive right now\\n" +
                        "• The airport code might not be recognized\\n" +
                        "• Try checking active flights or different airports", intent.getArrivalAirport()),
                    null,
                    ConversationResponse.ResponseType.NO_RESULTS
                );
            }
            
            String response = String.format("🛬 **Flights arriving at %s** (%d result%s found)\\n\\n", 
                intent.getArrivalAirport(), flights.size(), flights.size() == 1 ? "" : "s");
            
            return new ConversationResponse(response, flights, ConversationResponse.ResponseType.FLIGHT_DATA);
            
        } catch (Exception e) {
            return createErrorResponse("arrival search", e.getMessage());
        }
    }
    
    private ConversationResponse processAirlineQuery(NaturalLanguageProcessor.QueryIntent intent) {
        try {
            List<Flight> flights = flightService.getFlightsByAirline(intent.getAirline(), intent.getLimit());
            
            if (flights.isEmpty()) {
                return new ConversationResponse(
                    String.format("No flights found for airline %s today. ✈️\\n\\n" +
                        "This could mean:\\n" +
                        "• No flights from this airline right now\\n" +
                        "• The airline code might not be recognized\\n" +
                        "• Try checking other airlines or active flights", intent.getAirline()),
                    null,
                    ConversationResponse.ResponseType.NO_RESULTS
                );
            }
            
            String response = String.format("✈️ **%s Flights** (%d result%s found)\\n\\n", 
                intent.getAirline(), flights.size(), flights.size() == 1 ? "" : "s");
            
            return new ConversationResponse(response, flights, ConversationResponse.ResponseType.FLIGHT_DATA);
            
        } catch (Exception e) {
            return createErrorResponse("airline search", e.getMessage());
        }
    }
    
    private ConversationResponse processActiveFlightsQuery(NaturalLanguageProcessor.QueryIntent intent) {
        try {
            List<Flight> flights = flightService.getAllActiveFlights(intent.getLimit());
            
            if (flights.isEmpty()) {
                return new ConversationResponse(
                    "No active flights found at the moment. 🔍\\n\\n" +
                    "This could be due to:\\n" +
                    "• Temporary API issues\\n" +
                    "• Low flight activity at this time\\n" +
                    "• Rate limiting on the flight data service\\n\\n" +
                    "Please try again in a few moments or ask for specific routes or airlines.",
                    null,
                    ConversationResponse.ResponseType.NO_RESULTS
                );
            }
            
            String response = String.format("🌍 **Active Flights** (%d result%s found)\\n\\n", 
                flights.size(), flights.size() == 1 ? "" : "s");
            
            return new ConversationResponse(response, flights, ConversationResponse.ResponseType.FLIGHT_DATA);
            
        } catch (Exception e) {
            return createErrorResponse("active flights search", e.getMessage());
        }
    }
    
    private ConversationResponse createUnknownResponse(String userMessage) {
        return new ConversationResponse(
            "🤔 I'm not sure I understand that request. Let me help you!\\n\\n" +
            "I can help you find flights using natural language. Try asking:\\n\\n" +
            "• **\\\"Show me flights from JFK to LAX\\\"** - for routes\\n" +
            "• **\\\"What flights are leaving Chicago?\\\"** - for departures\\n" +
            "• **\\\"American Airlines flights\\\"** - for specific airlines\\n" +
            "• **\\\"Check flight AA100\\\"** - for flight numbers\\n" +
            "• **\\\"Show me active flights\\\"** - for current flights\\n\\n" +
            "Or type **\\\"help\\\"** for more examples. What would you like to search for?",
            null,
            ConversationResponse.ResponseType.CLARIFICATION
        );
    }
    
    private ConversationResponse createErrorResponse(String operation, String error) {
        return new ConversationResponse(
            String.format("⚠️ Sorry, I encountered an issue while performing the %s.\\n\\n" +
                "**Error:** %s\\n\\n" +
                "Please try again or ask for help if the problem persists. " +
                "You can also try a different search or check our service status.", operation, error),
            null,
            ConversationResponse.ResponseType.ERROR
        );
    }
    
    public static class ConversationResponse {
        private String message;
        private List<Flight> flights;
        private ResponseType type;
        
        public ConversationResponse(String message, List<Flight> flights, ResponseType type) {
            this.message = message;
            this.flights = flights;
            this.type = type;
        }
        
        // Getters
        public String getMessage() { return message; }
        public List<Flight> getFlights() { return flights; }
        public ResponseType getType() { return type; }
        
        public enum ResponseType {
            INFO,
            FLIGHT_DATA,
            NO_RESULTS,
            ERROR,
            CLARIFICATION,
            GREETING
        }
    }
}
