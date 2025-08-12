package github.sfx.sprint_flight.service;

import github.sfx.sprint_flight.model.Flight;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class GeminiConversationService {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiConversationService.class);
    
    @Autowired
    private FlightService flightService;
    
    @Autowired
    private NaturalLanguageProcessor nlpService;
    
    @Value("${gemini.api.key:}")
    private String geminiApiKey;
    
    /**
     * Validate Gemini configuration on service initialization
     */
    @PostConstruct
    public void validateGeminiConfiguration() {
        if (geminiApiKey == null || geminiApiKey.trim().isEmpty() || 
            geminiApiKey.contains("your_") || geminiApiKey.contains("YOUR_")) {
            logger.warn("⚠️  Gemini API key not configured - using fallback responses");
            logger.info("   Set GEMINI_API_KEY environment variable for enhanced AI features");
        } else {
            logger.info("✅ Gemini AI configuration validated");
        }
    }
    
    /**
     * Check if API key is properly configured (not placeholder)
     */
    private boolean isApiKeyConfigured() {
        return geminiApiKey != null && 
               !geminiApiKey.trim().isEmpty() && 
               !geminiApiKey.contains("your_") && 
               !geminiApiKey.contains("YOUR_");
    }
    
    @Value("${gemini.model:gemini-2.0-flash-exp}")
    private String model;
    
    @Value("${gemini.temperature:0.7}")
    private double temperature;
    
    @Value("${gemini.max-tokens:2000}")
    private int maxTokens;
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public GeminiConversationService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Process a natural language flight query using Gemini AI with conversation context
     */
    public String processFlightQueryWithContext(String userQuery, List<github.sfx.sprint_flight.service.ConversationService.ConversationMessage> conversationHistory) {
        logger.info("Processing flight query with context: {}", userQuery);
        
        try {
            // First, extract flight intent and parameters using enhanced NLP with country support
            Map<String, String> extractedParams = nlpService.extractFlightParametersWithSuggestions(userQuery);
            logger.debug("Extracted parameters with suggestions: {}", extractedParams);
            
            // Get relevant flight data based on extracted parameters
            List<Flight> flightData = getRelevantFlightData(extractedParams);
            
            // Generate conversational response using Gemini with context
            String response = generateConversationalResponseWithContext(userQuery, flightData, extractedParams, conversationHistory);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error processing flight query with context: {}", e.getMessage());
            return "I apologize, but I encountered an error while processing your flight query. Please try rephrasing your question or contact support if the issue persists.";
        }
    }
    
    /**
     * Process a natural language flight query using Gemini AI
     */
    public String processFlightQuery(String userQuery) {
        logger.info("Processing flight query: {}", userQuery);
        
        try {
            // First, extract flight intent and parameters using enhanced NLP with country support
            Map<String, String> extractedParams = nlpService.extractFlightParametersWithSuggestions(userQuery);
            logger.debug("Extracted parameters with suggestions: {}", extractedParams);
            
            // Get relevant flight data based on extracted parameters
            List<Flight> flightData = getRelevantFlightData(extractedParams);
            
            // Generate conversational response using Gemini
            String response = generateConversationalResponse(userQuery, flightData, extractedParams);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error processing flight query: {}", e.getMessage());
            return "I apologize, but I encountered an error while processing your flight query. Please try rephrasing your question or contact support if the issue persists.";
        }
    }
    
    /**
     * Get relevant flight data based on extracted parameters with enhanced country support
     */
    private List<Flight> getRelevantFlightData(Map<String, String> params) {
        String queryType = params.get("queryType");
        int limit = Integer.parseInt(params.getOrDefault("limit", "5"));
        
        switch (queryType) {
            case "airline":
                return flightService.getFlightsByAirline(params.get("airline"), limit);
            case "departure":
                return flightService.getFlightsByDepartureAirport(params.get("departure"), limit);
            case "arrival":
                return flightService.getFlightsByArrivalAirport(params.get("arrival"), limit);
            case "route":
                String departure = params.get("departure");
                String arrival = params.get("arrival");
                
                if (departure != null && arrival != null) {
                    return flightService.getFlightsByRoute(departure, arrival, limit);
                } else if (departure != null) {
                    return flightService.getFlightsByDepartureAirport(departure, limit);
                } else if (arrival != null) {
                    return flightService.getFlightsByArrivalAirport(arrival, limit);
                }
                
                // If no specific airports, try country suggestions
                if (params.containsKey("suggested_airports")) {
                    String[] airports = params.get("suggested_airports").split(",");
                    if (airports.length > 0) {
                        return flightService.getFlightsByDepartureAirport(airports[0].trim(), limit);
                    }
                }
                return flightService.getAllActiveFlights(limit);
            case "flight_number":
                return flightService.getFlightByNumber(params.get("flightNumber"), limit);
            case "active":
            default:
                return flightService.getAllActiveFlights(limit);
        }
    }
    
    /**
     * Generate conversational response using Gemini AI with conversation context
     */
    private String generateConversationalResponseWithContext(String userQuery, List<Flight> flightData, Map<String, String> params, List<github.sfx.sprint_flight.service.ConversationService.ConversationMessage> conversationHistory) {
        // Check if we have a valid API key configured
        if (!isApiKeyConfigured()) {
            // Fallback response when no API key is configured
            return generateFallbackResponse(userQuery, flightData, params);
        }
        
        try {
            String prompt = buildPromptWithContext(userQuery, flightData, params, conversationHistory);
            
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            Map<String, Object> parts = new HashMap<>();
            parts.put("text", prompt);
            contents.put("parts", List.of(parts));
            requestBody.put("contents", List.of(contents));
            
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", temperature);
            generationConfig.put("maxOutputTokens", maxTokens);
            generationConfig.put("topP", 0.95);  // Higher for more creative responses
            generationConfig.put("topK", 40);    // Optimized for Gemini 2.0 Flash
            requestBody.put("generationConfig", generationConfig);
            
            String response = webClient.post()
                    .uri("/models/" + model + ":generateContent?key=" + geminiApiKey)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            return extractTextFromGeminiResponse(response);
            
        } catch (Exception e) {
            logger.error("Error calling Gemini API with context: {}", e.getMessage());
            return generateFallbackResponse(userQuery, flightData, params);
        }
    }
    
    /**
     * Generate conversational response using Gemini AI
     */
    private String generateConversationalResponse(String userQuery, List<Flight> flightData, Map<String, String> params) {
        // Check if we have a valid API key configured
        if (!isApiKeyConfigured()) {
            // Fallback response when no API key is configured
            return generateFallbackResponse(userQuery, flightData, params);
        }
        
        try {
            String prompt = buildPrompt(userQuery, flightData, params);
            
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            Map<String, Object> parts = new HashMap<>();
            parts.put("text", prompt);
            contents.put("parts", List.of(parts));
            requestBody.put("contents", List.of(contents));
            
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", temperature);
            generationConfig.put("maxOutputTokens", maxTokens);
            generationConfig.put("topP", 0.95);  // Higher for more creative responses
            generationConfig.put("topK", 40);    // Optimized for Gemini 2.0 Flash
            requestBody.put("generationConfig", generationConfig);
            
            String response = webClient.post()
                    .uri("/models/" + model + ":generateContent?key=" + geminiApiKey)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            return extractTextFromGeminiResponse(response);
            
        } catch (Exception e) {
            logger.error("Error calling Gemini API: {}", e.getMessage());
            return generateFallbackResponse(userQuery, flightData, params);
        }
    }
    
    /**
     * Build prompt for Gemini 2.0 Flash AI with conversation context
     */
    private String buildPromptWithContext(String userQuery, List<Flight> flightData, Map<String, String> params, List<github.sfx.sprint_flight.service.ConversationService.ConversationMessage> conversationHistory) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an intelligent flight information assistant powered by Gemini 2.0 Flash. ");
        prompt.append("You excel at understanding natural language queries about flights and providing detailed, helpful responses.\n\n");
        
        // Add conversation context if available
        if (conversationHistory != null && conversationHistory.size() > 1) {
            prompt.append("Previous conversation context:\n");
            // Include last few messages for context (excluding current query)
            int start = Math.max(0, conversationHistory.size() - 6); // Last 6 messages
            for (int i = start; i < conversationHistory.size() - 1; i++) {
                var message = conversationHistory.get(i);
                prompt.append(message.getRole().equals("user") ? "User: " : "Assistant: ");
                prompt.append(message.getContent()).append("\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("Current User Query: \"").append(userQuery).append("\"\n\n");
        
        if (flightData.isEmpty()) {
            prompt.append("No flight data was found for this query. ");
            prompt.append("Please provide a helpful, conversational response that:\n");
            prompt.append("- Explains why no flights were found\n");
            prompt.append("- Suggests alternative search criteria (airline codes: AA, DL, UA; airport codes: JFK, LAX, ORD)\n");
            prompt.append("- Offers general flight search tips\n");
            prompt.append("- Maintains a friendly, professional tone\n");
            prompt.append("- References the conversation context if relevant\n");
        } else {
            prompt.append("Here is the relevant flight data to help answer their query:\n\n");
            for (int i = 0; i < Math.min(flightData.size(), 5); i++) {
                Flight flight = flightData.get(i);
                prompt.append("Flight ").append(i + 1).append(":\n");
                if (flight.getFlight() != null) {
                    prompt.append("- Flight Number: ").append(flight.getFlight().getIata()).append("\n");
                }
                if (flight.getAirline() != null) {
                    prompt.append("- Airline: ").append(flight.getAirline().getName()).append(" (").append(flight.getAirline().getIata()).append(")\n");
                }
                if (flight.getDeparture() != null) {
                    prompt.append("- Departure: ").append(flight.getDeparture().getAirport()).append(" (").append(flight.getDeparture().getIata()).append(")");
                    if (flight.getDeparture().getScheduled() != null) {
                        prompt.append(" at ").append(flight.getDeparture().getScheduled());
                    }
                    prompt.append("\n");
                }
                if (flight.getArrival() != null) {
                    prompt.append("- Arrival: ").append(flight.getArrival().getAirport()).append(" (").append(flight.getArrival().getIata()).append(")");
                    if (flight.getArrival().getScheduled() != null) {
                        prompt.append(" at ").append(flight.getArrival().getScheduled());
                    }
                    prompt.append("\n");
                }
                prompt.append("- Status: ").append(flight.getFlightStatus()).append("\n");
                prompt.append("- Date: ").append(flight.getFlightDate()).append("\n\n");
            }
            
            if (flightData.size() > 5) {
                prompt.append("... and ").append(flightData.size() - 5).append(" more flights available.\n\n");
            }
        }
        
        prompt.append("Please provide a natural, conversational response that:\n");
        prompt.append("- Directly answers their question using the flight data\n");
        prompt.append("- Uses emojis appropriately (✈️, 🛫, 🛬, ⏰, etc.) to make it engaging\n");
        prompt.append("- Is helpful, concise, and friendly\n");
        prompt.append("- Includes specific flight details when relevant\n");
        prompt.append("- Suggests related queries or actions they might find useful\n");
        prompt.append("- Shows understanding of aviation terminology and context\n");
        prompt.append("- References previous conversation context when appropriate\n");
        
        return prompt.toString();
    }
    
    /**
     * Build prompt for Gemini 2.0 Flash AI
     */
    private String buildPrompt(String userQuery, List<Flight> flightData, Map<String, String> params) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an intelligent flight information assistant powered by Gemini 2.0 Flash. ");
        prompt.append("You excel at understanding natural language queries about flights and providing detailed, helpful responses.\n\n");
        
        prompt.append("User Query: \"").append(userQuery).append("\"\n\n");
        
        if (flightData.isEmpty()) {
            prompt.append("No flight data was found for this query. ");
            prompt.append("Please provide a helpful, conversational response that:\n");
            prompt.append("- Explains why no flights were found\n");
            prompt.append("- Suggests alternative search criteria (airline codes: AA, DL, UA; airport codes: JFK, LAX, ORD)\n");
            prompt.append("- Offers general flight search tips\n");
            prompt.append("- Maintains a friendly, professional tone\n");
        } else {
            prompt.append("Here is the relevant flight data to help answer their query:\n\n");
            for (int i = 0; i < Math.min(flightData.size(), 5); i++) {
                Flight flight = flightData.get(i);
                prompt.append("Flight ").append(i + 1).append(":\n");
                if (flight.getFlight() != null) {
                    prompt.append("- Flight Number: ").append(flight.getFlight().getIata()).append("\n");
                }
                if (flight.getAirline() != null) {
                    prompt.append("- Airline: ").append(flight.getAirline().getName()).append(" (").append(flight.getAirline().getIata()).append(")\n");
                }
                if (flight.getDeparture() != null) {
                    prompt.append("- Departure: ").append(flight.getDeparture().getAirport()).append(" (").append(flight.getDeparture().getIata()).append(")");
                    if (flight.getDeparture().getScheduled() != null) {
                        prompt.append(" at ").append(flight.getDeparture().getScheduled());
                    }
                    prompt.append("\n");
                }
                if (flight.getArrival() != null) {
                    prompt.append("- Arrival: ").append(flight.getArrival().getAirport()).append(" (").append(flight.getArrival().getIata()).append(")");
                    if (flight.getArrival().getScheduled() != null) {
                        prompt.append(" at ").append(flight.getArrival().getScheduled());
                    }
                    prompt.append("\n");
                }
                prompt.append("- Status: ").append(flight.getFlightStatus()).append("\n");
                prompt.append("- Date: ").append(flight.getFlightDate()).append("\n\n");
            }
            
            if (flightData.size() > 5) {
                prompt.append("... and ").append(flightData.size() - 5).append(" more flights available.\n\n");
            }
        }
        
        prompt.append("Please provide a natural, conversational response that:\n");
        prompt.append("- Directly answers their question using the flight data\n");
        prompt.append("- Uses emojis appropriately (✈️, 🛫, 🛬, ⏰, etc.) to make it engaging\n");
        prompt.append("- Is helpful, concise, and friendly\n");
        prompt.append("- Includes specific flight details when relevant\n");
        prompt.append("- Suggests related queries or actions they might find useful\n");
        prompt.append("- Shows understanding of aviation terminology and context\n");
        
        return prompt.toString();
    }
    
    /**
     * Extract text response from Gemini API response
     */
    private String extractTextFromGeminiResponse(String response) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    return (String) parts.get(0).get("text");
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing Gemini response: {}", e.getMessage());
        }
        
        return "I received a response but couldn't parse it properly. Please try your query again.";
    }
    
    /**
     * Generate fallback response when Gemini API is not available
     */
    private String generateFallbackResponse(String userQuery, List<Flight> flightData, Map<String, String> params) {
        if (flightData.isEmpty()) {
            return "I couldn't find any flights matching your query \"" + userQuery + "\". " +
                   "Please try searching with different criteria such as airline codes (AA, DL, UA), " +
                   "airport codes (JFK, LAX, ORD), or specific flight numbers.";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("I found ").append(flightData.size()).append(" flight(s) for your query \"").append(userQuery).append("\":\n\n");
        
        for (int i = 0; i < Math.min(flightData.size(), 3); i++) {
            Flight flight = flightData.get(i);
            response.append("✈️ Flight ").append(i + 1).append(":\n");
            
            if (flight.getFlight() != null && flight.getAirline() != null) {
                response.append("🏷️ ").append(flight.getAirline().getName()).append(" ").append(flight.getFlight().getIata()).append("\n");
            }
            
            if (flight.getDeparture() != null && flight.getArrival() != null) {
                response.append("🛫 ").append(flight.getDeparture().getAirport()).append(" → ").append(flight.getArrival().getAirport()).append("\n");
            }
            
            response.append("📊 Status: ").append(flight.getFlightStatus()).append("\n\n");
        }
        
        if (flightData.size() > 3) {
            response.append("... and ").append(flightData.size() - 3).append(" more flights available.\n");
        }
        
        response.append("💡 Tip: You can ask me about specific airlines, routes, or flight numbers for more detailed information!");
        
        return response.toString();
    }
    
    /**
     * Get information about the current AI model
     */
    public String getModelInfo() {
        return "Currently using " + model + " for intelligent flight conversations with temperature " + 
               temperature + " and max tokens " + maxTokens;
    }
    
    /**
     * Health check for Gemini 2.0 Flash API
     */
    public boolean isGeminiAvailable() {
        // Check if API key is configured for enhanced responses
        if (!isApiKeyConfigured()) {
            return false;
        }
        
        try {
            String testResponse = generateConversationalResponse("Hello", List.of(), Map.of("queryType", "test"));
            return !testResponse.contains("couldn't find any flights");
        } catch (Exception e) {
            logger.error("Gemini health check failed: {}", e.getMessage());
            return false;
        }
    }
}
