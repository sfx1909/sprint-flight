package github.sfx.sprint_flight.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class ConversationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);
    
    @Autowired
    private GeminiConversationService geminiService;
    
    // In-memory conversation storage (consider Redis for production)
    private final Map<String, List<ConversationMessage>> conversations = new ConcurrentHashMap<>();
    
    /**
     * Process a user query with conversation context and return a conversational response
     */
    public String processQuery(String query, String conversationId) {
        logger.info("Processing user query: {} for conversation: {}", query, conversationId);
        
        if (query == null || query.trim().isEmpty()) {
            return getDefaultHelpMessage();
        }
        
        // Get or create conversation history
        List<ConversationMessage> history = conversations.computeIfAbsent(conversationId, k -> new ArrayList<>());
        
        // Add user message to history
        history.add(new ConversationMessage("user", query.trim()));
        
        // Process with context
        String response = geminiService.processFlightQueryWithContext(query.trim(), history);
        
        // Add assistant response to history
        history.add(new ConversationMessage("assistant", response));
        
        // Keep only last 20 messages to prevent memory issues
        if (history.size() > 20) {
            history.subList(0, history.size() - 20).clear();
        }
        
        return response;
    }
    
    /**
     * Process a user query without conversation context (backwards compatibility)
     */
    public String processQuery(String query) {
        // Generate a temporary conversation ID for single queries
        String tempConversationId = "temp_" + System.currentTimeMillis();
        return processQuery(query, tempConversationId);
    }
    
    /**
     * Get conversation history for a specific conversation ID
     */
    public List<ConversationMessage> getConversationHistory(String conversationId) {
        return conversations.getOrDefault(conversationId, new ArrayList<>());
    }
    
    /**
     * Clear conversation history for a specific conversation ID
     */
    public void clearConversation(String conversationId) {
        conversations.remove(conversationId);
        logger.info("Cleared conversation: {}", conversationId);
    }
    
    /**
     * Get default help message
     */
    private String getDefaultHelpMessage() {
        return "I'm here to help you with flight information! You can ask me things like:\n" +
               "• 'Show me American Airlines flights'\n" +
               "• 'What flights are departing from JFK?'\n" +
               "• 'Find flights from New York to Los Angeles'\n" +
               "• 'Tell me about flight AA100'\n" +
               "What would you like to know about flights?";
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
    
    /**
     * Inner class to represent conversation messages
     */
    public static class ConversationMessage {
        private final String role;
        private final String content;
        private final long timestamp;
        
        public ConversationMessage(String role, String content) {
            this.role = role;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getRole() { return role; }
        public String getContent() { return content; }
        public long getTimestamp() { return timestamp; }
    }
}
