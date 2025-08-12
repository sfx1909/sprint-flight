package github.sfx.sprint_flight.controller;

import github.sfx.sprint_flight.service.ConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/conversation")
@CrossOrigin(origins = "*")
public class ConversationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);
    
    @Autowired
    private ConversationService conversationService;
    
    /**
     * Process natural language flight queries with conversation context
     * Example: POST /api/conversation/query
     * Body: {"query": "Show me American Airlines flights from JFK to LAX", "conversationId": "conv_123"}
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> processQuery(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        String conversationId = request.get("conversationId");
        logger.info("Received conversation query: {} for conversation: {}", query, conversationId);
        
        String response = conversationService.processQuery(query, conversationId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("query", query);
        result.put("response", response);
        result.put("conversationId", conversationId);
        result.put("timestamp", java.time.Instant.now().toString());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get help and example queries
     * Example: GET /api/conversation/help
     */
    @GetMapping("/help")
    public ResponseEntity<Map<String, Object>> getHelp() {
        String helpMessage = conversationService.getHelpMessage();
        
        Map<String, Object> result = new HashMap<>();
        result.put("help", helpMessage);
        result.put("examples", Map.of(
            "airline_query", "Show me Delta flights",
            "route_query", "Flights from New York to Los Angeles", 
            "airport_query", "What flights are departing from JFK?",
            "flight_number_query", "Tell me about flight AA100",
            "general_query", "Show me active flights"
        ));
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Quick query endpoint for simple text input
     * Example: GET /api/conversation/ask?q=flights from JFK
     */
    @GetMapping("/ask")
    public ResponseEntity<Map<String, Object>> askQuestion(@RequestParam("q") String question) {
        logger.info("Received quick query: {}", question);
        
        String response = conversationService.processQuery(question);
        
        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("answer", response);
        result.put("timestamp", java.time.Instant.now().toString());
        
        return ResponseEntity.ok(result);
    }
}
