package github.sfx.sprint_flight.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ConversationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);
    
    @Autowired
    private GeminiConversationService geminiService;
    
    /**
     * Process a user query and return a conversational response
     */
    public String processQuery(String query) {
        logger.info("Processing user query: {}", query);
        
        if (query == null || query.trim().isEmpty()) {
            return "I'm here to help you with flight information! You can ask me things like:\n" +
                   "• 'Show me American Airlines flights'\n" +
                   "• 'What flights are departing from JFK?'\n" +
                   "• 'Find flights from New York to Los Angeles'\n" +
                   "• 'Tell me about flight AA100'\n" +
                   "What would you like to know about flights?";
        }
        
        return geminiService.processFlightQuery(query.trim());
    }
    
    /**
     * Get help message with example queries
     */
    public String getHelpMessage() {
        return "🛩️ **Flight Information Assistant**\n\n" +
               "I can help you find flight information! Here are some example queries:\n\n" +
               "**By Airline:**\n" +
               "• 'Show me Delta flights'\n" +
               "• 'What American Airlines flights are available?'\n\n" +
               "**By Airport:**\n" +
               "• 'Flights departing from JFK'\n" +
               "• 'What flights are arriving at LAX?'\n\n" +
               "**By Route:**\n" +
               "• 'Flights from New York to Los Angeles'\n" +
               "• 'Show me routes from Chicago to Miami'\n\n" +
               "**By Flight Number:**\n" +
               "• 'Tell me about flight AA100'\n" +
               "• 'Status of Delta flight DL123'\n\n" +
               "**General:**\n" +
               "• 'Show me active flights'\n" +
               "• 'What flights are currently in the air?'\n\n" +
               "Just ask me naturally - I'll understand what you're looking for! ✈️";
    }
}
