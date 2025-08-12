package github.sfx.sprint_flight.controller;

import github.sfx.sprint_flight.service.ConversationService;
import github.sfx.sprint_flight.service.ConversationService.ConversationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    @Autowired
    private ConversationService conversationService;
    
    /**
     * Main chat endpoint for natural language queries
     */
    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> processMessage(@RequestBody ChatRequest request) {
        logger.info("Processing chat message: {}", request.getMessage());
        
        try {
            ConversationResponse response = 
                conversationService.processMessage(request.getMessage());
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", response.getMessage());
            result.put("type", response.getType().toString().toLowerCase());
            result.put("flights", response.getFlights());
            result.put("timestamp", java.time.Instant.now().toString());
            
            // Add metadata for UI
            if (response.getFlights() != null && !response.getFlights().isEmpty()) {
                result.put("flight_count", response.getFlights().size());
                result.put("has_data", true);
            } else {
                result.put("has_data", false);
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error processing chat message", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "I'm sorry, I encountered an error processing your request. Please try again.");
            errorResponse.put("type", "error");
            errorResponse.put("flights", null);
            errorResponse.put("has_data", false);
            errorResponse.put("timestamp", java.time.Instant.now().toString());
            
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Get chat suggestions for user
     */
    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> getSuggestions() {
        Map<String, Object> suggestions = new HashMap<>();
        
        suggestions.put("quick_queries", new String[]{
            "Show me flights from London to Tokyo",
            "Emirates flights from Dubai",
            "What flights are leaving from Frankfurt?",
            "Singapore Airlines flights",
            "British Airways from Heathrow",
            "Check flight EK215",
            "Show me active flights"
        });
        
        suggestions.put("example_airports", new String[]{
            "LHR (London Heathrow)", "CDG (Paris)", "NRT (Tokyo Narita)", 
            "DXB (Dubai)", "FRA (Frankfurt)", "SIN (Singapore)",
            "JFK (New York)", "LAX (Los Angeles)", "ORD (Chicago)"
        });
        
        suggestions.put("example_airlines", new String[]{
            "Emirates (EK)", "Singapore Airlines (SQ)", "British Airways (BA)",
            "Lufthansa (LH)", "Air France (AF)", "Qatar Airways (QR)",
            "American Airlines (AA)", "Delta (DL)", "United (UA)"
        });
        
        return ResponseEntity.ok(suggestions);
    }
    
    /**
     * Chat info and capabilities
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getChatInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("name", "Flight Chat Assistant");
        info.put("version", "1.0.0");
        info.put("capabilities", new String[]{
            "Natural language flight searches",
            "Route-based queries (from X to Y)",
            "Airport departure/arrival searches",
            "Airline-specific searches",
            "Flight number lookups",
            "Active flight monitoring"
        });
        
        info.put("supported_formats", new String[]{
            "\"Flights from London to Tokyo\"",
            "\"Show me Emirates flights\"",
            "\"What's leaving from Frankfurt?\"",
            "\"Singapore Airlines from Changi\"",
            "\"Check flight BA123\"",
            "\"British Airways to New York\"",
            "\"Active flights please\""
        });
        
        return ResponseEntity.ok(info);
    }
    
    public static class ChatRequest {
        private String message;
        private String sessionId; // For future conversation context
        
        public ChatRequest() {}
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getSessionId() {
            return sessionId;
        }
        
        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }
}
