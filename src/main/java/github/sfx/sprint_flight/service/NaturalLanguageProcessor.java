package github.sfx.sprint_flight.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NaturalLanguageProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(NaturalLanguageProcessor.class);
    
    // Enhanced airline mapping with multiple name variations
    private static final Map<String, String> AIRLINE_MAPPING = new HashMap<>();
    static {
        // US Airlines
        AIRLINE_MAPPING.put("american", "AA");
        AIRLINE_MAPPING.put("american airlines", "AA");
        AIRLINE_MAPPING.put("aa", "AA");
        AIRLINE_MAPPING.put("delta", "DL");
        AIRLINE_MAPPING.put("delta airlines", "DL");
        AIRLINE_MAPPING.put("dl", "DL");
        AIRLINE_MAPPING.put("united", "UA");
        AIRLINE_MAPPING.put("united airlines", "UA");
        AIRLINE_MAPPING.put("ua", "UA");
        AIRLINE_MAPPING.put("southwest", "WN");
        AIRLINE_MAPPING.put("southwest airlines", "WN");
        AIRLINE_MAPPING.put("jetblue", "B6");
        AIRLINE_MAPPING.put("jet blue", "B6");
        AIRLINE_MAPPING.put("alaska", "AS");
        AIRLINE_MAPPING.put("alaska airlines", "AS");
        
        // International Airlines
        AIRLINE_MAPPING.put("british airways", "BA");
        AIRLINE_MAPPING.put("ba", "BA");
        AIRLINE_MAPPING.put("lufthansa", "LH");
        AIRLINE_MAPPING.put("air france", "AF");
        AIRLINE_MAPPING.put("klm", "KL");
        AIRLINE_MAPPING.put("emirates", "EK");
        AIRLINE_MAPPING.put("qatar", "QR");
        AIRLINE_MAPPING.put("qatar airways", "QR");
        AIRLINE_MAPPING.put("singapore airlines", "SQ");
        AIRLINE_MAPPING.put("cathay pacific", "CX");
        AIRLINE_MAPPING.put("etihad", "EY");
        
        // African Airlines
        AIRLINE_MAPPING.put("south african airways", "SA");
        AIRLINE_MAPPING.put("saa", "SA");
        AIRLINE_MAPPING.put("ethiopian", "ET");
        AIRLINE_MAPPING.put("kenya airways", "KQ");
    }
    
    // Comprehensive airport and city mapping with aliases
    private static final Map<String, AirportInfo> AIRPORT_MAPPING = new HashMap<>();
    static {
        // Major US Airports
        addAirport("JFK", "John F Kennedy International", "new york", "ny", "jfk", "kennedy");
        addAirport("LGA", "LaGuardia Airport", "new york laguardia", "lga", "laguardia");
        addAirport("EWR", "Newark Liberty International", "newark", "new york newark", "ewr");
        addAirport("LAX", "Los Angeles International", "los angeles", "la", "lax", "california");
        addAirport("ORD", "O'Hare International", "chicago", "ohare", "o'hare", "ord");
        addAirport("MDW", "Chicago Midway", "chicago midway", "midway", "mdw");
        addAirport("MIA", "Miami International", "miami", "mia", "florida");
        addAirport("SFO", "San Francisco International", "san francisco", "sf", "sfo");
        addAirport("BOS", "Logan International", "boston", "logan", "bos");
        addAirport("SEA", "Seattle-Tacoma International", "seattle", "sea", "tacoma");
        addAirport("DEN", "Denver International", "denver", "den", "colorado");
        addAirport("ATL", "Hartsfield-Jackson Atlanta International", "atlanta", "atl", "georgia");
        addAirport("DFW", "Dallas/Fort Worth International", "dallas", "dfw", "fort worth");
        addAirport("IAH", "George Bush Intercontinental", "houston", "iah", "bush");
        addAirport("PHX", "Phoenix Sky Harbor", "phoenix", "phx", "sky harbor");
        addAirport("LAS", "McCarran International", "las vegas", "vegas", "las", "mccarran");
        
        // International Airports
        addAirport("LHR", "Heathrow Airport", "london", "heathrow", "lhr", "london heathrow");
        addAirport("LGW", "Gatwick Airport", "london gatwick", "gatwick", "lgw");
        addAirport("CDG", "Charles de Gaulle", "paris", "cdg", "charles de gaulle");
        addAirport("FRA", "Frankfurt am Main", "frankfurt", "fra", "germany");
        addAirport("AMS", "Amsterdam Schiphol", "amsterdam", "schiphol", "ams");
        addAirport("NRT", "Narita International", "tokyo", "narita", "nrt");
        addAirport("HND", "Haneda Airport", "tokyo haneda", "haneda", "hnd");
        addAirport("ICN", "Incheon International", "seoul", "incheon", "icn", "south korea");
        addAirport("SIN", "Changi Airport", "singapore", "changi", "sin");
        addAirport("HKG", "Hong Kong International", "hong kong", "hkg", "hongkong");
        addAirport("DXB", "Dubai International", "dubai", "dxb", "uae");
        addAirport("DOH", "Hamad International", "doha", "doh", "qatar");
        
        // South African Airports
        addAirport("CPT", "Cape Town International", "cape town", "capetown", "cpt");
        addAirport("JNB", "OR Tambo International", "johannesburg", "jnb", "or tambo", "joburg");
        addAirport("DUR", "King Shaka International", "durban", "dur", "king shaka");
        
        // Australian Airports
        addAirport("SYD", "Kingsford Smith Airport", "sydney", "syd", "kingsford smith");
        addAirport("MEL", "Melbourne Airport", "melbourne", "mel", "tullamarine");
        addAirport("BNE", "Brisbane Airport", "brisbane", "bne");
        addAirport("PER", "Perth Airport", "perth", "per");
        
        // Canadian Airports
        addAirport("YYZ", "Toronto Pearson", "toronto", "yyz", "pearson");
        addAirport("YVR", "Vancouver International", "vancouver", "yvr");
        addAirport("YUL", "Montreal-Trudeau", "montreal", "yul", "trudeau");
        
        // Indian Airports
        addAirport("BOM", "Chhatrapati Shivaji International", "mumbai", "bom", "bombay");
        addAirport("DEL", "Indira Gandhi International", "delhi", "del", "new delhi");
        addAirport("BLR", "Kempegowda International", "bangalore", "blr", "bengaluru");
    }
    
    // Inner class for airport information
    private static class AirportInfo {
        String code;
        String name;
        List<String> aliases;
        
        AirportInfo(String code, String name, List<String> aliases) {
            this.code = code;
            this.name = name;
            this.aliases = aliases;
        }
    }
    
    private static void addAirport(String code, String name, String... aliases) {
        AIRPORT_MAPPING.put(code.toLowerCase(), new AirportInfo(code, name, Arrays.asList(aliases)));
        for (String alias : aliases) {
            AIRPORT_MAPPING.put(alias.toLowerCase(), new AirportInfo(code, name, Arrays.asList(aliases)));
        }
    }
    
    // Enhanced regex patterns
    private static final Pattern FLIGHT_NUMBER_PATTERN = Pattern.compile("\\b([A-Z]{1,3}\\d{1,4}[A-Z]?)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern AIRPORT_CODE_PATTERN = Pattern.compile("\\b([A-Z]{3})\\b");
    private static final Pattern ROUTE_PATTERN = Pattern.compile("\\b(?:from|departure|departing|leaving)\\s+([a-zA-Z\\s]+?)\\s+(?:to|arrival|arriving|going|bound|→|->)\\s+([a-zA-Z\\s]+?)(?:\\s+on\\s|\\s+at\\s|\\s*$|\\s+\\d)", Pattern.CASE_INSENSITIVE);
    private static final Pattern FUZZY_ROUTE_PATTERN = Pattern.compile("\\b([a-zA-Z\\s]{2,20}?)\\s+(?:to|→|->)\\s+([a-zA-Z\\s]{2,20}?)(?:\\s+on\\s|\\s+at\\s|\\s*$|\\s+\\d)", Pattern.CASE_INSENSITIVE);
    
    // Date and time patterns
    private static final Pattern DATE_PATTERN = Pattern.compile("\\b(?:on\\s+)?(monday|tuesday|wednesday|thursday|friday|saturday|sunday|today|tomorrow|\\d{1,2}[/-]\\d{1,2}(?:[/-]\\d{2,4})?)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern TIME_PATTERN = Pattern.compile("\\b(?:at\\s+)?(\\d{1,2}(?::\\d{2})?(?:\\s*(?:am|pm|AM|PM))?)\\b", Pattern.CASE_INSENSITIVE);
    
    /**
     * Enhanced method to extract flight parameters from natural language query
     */
    public Map<String, String> extractFlightParameters(String query) {
        logger.debug("Extracting parameters from query: {}", query);
        
        Map<String, String> params = new HashMap<>();
        String lowerQuery = query.toLowerCase().trim();
        
        // Extract date and time information
        extractTemporalInfo(lowerQuery, params);
        
        // Determine query type with enhanced logic
        String queryType = determineQueryType(lowerQuery);
        params.put("queryType", queryType);
        
        // Extract specific parameters based on query type
        switch (queryType) {
            case "airline":
                extractEnhancedAirlineInfo(lowerQuery, params);
                break;
            case "departure":
                extractEnhancedDepartureInfo(lowerQuery, params);
                break;
            case "arrival":
                extractEnhancedArrivalInfo(lowerQuery, params);
                break;
            case "route":
                extractEnhancedRouteInfo(lowerQuery, params);
                break;
            case "flight_number":
                extractEnhancedFlightNumberInfo(query, params);
                break;
            default:
                params.put("queryType", "active");
                break;
        }
        
        // Extract limit with better patterns
        extractEnhancedLimit(lowerQuery, params);
        
        // Add confidence score
        params.put("confidence", calculateConfidence(params));
        
        logger.debug("Extracted parameters: {}", params);
        return params;
    }
    
    /**
     * Extract temporal information (dates, times, days)
     */
    private void extractTemporalInfo(String query, Map<String, String> params) {
        Matcher dateMatcher = DATE_PATTERN.matcher(query);
        if (dateMatcher.find()) {
            params.put("date", dateMatcher.group(1));
        }
        
        Matcher timeMatcher = TIME_PATTERN.matcher(query);
        if (timeMatcher.find()) {
            params.put("time", timeMatcher.group(1));
        }
    }
    
    /**
     * Enhanced query type determination with better accuracy
     */
    private String determineQueryType(String query) {
        // Flight number has highest priority
        if (FLIGHT_NUMBER_PATTERN.matcher(query).find()) {
            return "flight_number";
        }
        
        // Route patterns with multiple variations
        if (containsRoutePatterns(query)) {
            return "route";
        }
        
        // Airline patterns
        if (containsAirlinePatterns(query)) {
            return "airline";
        }
        
        // Departure patterns
        if (containsEnhancedDeparturePatterns(query)) {
            return "departure";
        }
        
        // Arrival patterns
        if (containsEnhancedArrivalPatterns(query)) {
            return "arrival";
        }
        
        // Status queries
        if (containsStatusPatterns(query)) {
            return "active";
        }
        
        return "active"; // Default
    }
    
    /**
     * Check for route patterns with multiple variations
     */
    private boolean containsRoutePatterns(String query) {
        return ROUTE_PATTERN.matcher(query).find() || 
               FUZZY_ROUTE_PATTERN.matcher(query).find() ||
               query.matches(".*\\b\\w+\\s+(to|→|->)\\s+\\w+.*");
    }
    
    /**
     * Enhanced airline pattern detection
     */
    private boolean containsAirlinePatterns(String query) {
        String[] airlineKeywords = {"airline", "carrier", "company", "airways", "air"};
        for (String keyword : airlineKeywords) {
            if (query.contains(keyword)) return true;
        }
        
        // Check for airline names with fuzzy matching
        for (String airlineName : AIRLINE_MAPPING.keySet()) {
            if (query.contains(airlineName) || fuzzyMatch(query, airlineName, 0.8)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Enhanced departure pattern detection
     */
    private boolean containsEnhancedDeparturePatterns(String query) {
        String[] keywords = {"departure", "departing", "leaving", "from", "outbound", "take off", "takeoff"};
        return Arrays.stream(keywords).anyMatch(query::contains);
    }
    
    /**
     * Enhanced arrival pattern detection
     */
    private boolean containsEnhancedArrivalPatterns(String query) {
        String[] keywords = {"arrival", "arriving", "landing", "to", "destination", "inbound", "going to"};
        return Arrays.stream(keywords).anyMatch(query::contains);
    }
    
    /**
     * Status pattern detection
     */
    private boolean containsStatusPatterns(String query) {
        String[] keywords = {"active", "live", "current", "now", "status", "tracking", "real time", "realtime"};
        return Arrays.stream(keywords).anyMatch(query::contains);
    }
    
    /**
     * Enhanced airline information extraction with fuzzy matching
     */
    private void extractEnhancedAirlineInfo(String query, Map<String, String> params) {
        String bestMatch = null;
        double bestScore = 0.0;
        
        for (Map.Entry<String, String> entry : AIRLINE_MAPPING.entrySet()) {
            String airlineName = entry.getKey();
            if (query.contains(airlineName)) {
                double score = (double) airlineName.length() / query.length();
                if (score > bestScore) {
                    bestScore = score;
                    bestMatch = entry.getValue();
                }
            } else {
                double fuzzyScore = fuzzyMatchScore(query, airlineName);
                if (fuzzyScore > 0.7 && fuzzyScore > bestScore) {
                    bestScore = fuzzyScore;
                    bestMatch = entry.getValue();
                }
            }
        }
        
        if (bestMatch != null) {
            params.put("airline", bestMatch);
        }
        
        // Fallback to airline code pattern
        if (!params.containsKey("airline")) {
            Matcher matcher = Pattern.compile("\\b([A-Z]{2,3})\\b").matcher(query.toUpperCase());
            if (matcher.find()) {
                params.put("airline", matcher.group(1));
            }
        }
    }
    
    /**
     * Enhanced route information extraction with better parsing
     */
    private void extractEnhancedRouteInfo(String query, Map<String, String> params) {
        logger.debug("Extracting route info from: {}", query);
        
        // Try multiple route patterns
        Matcher routeMatcher = ROUTE_PATTERN.matcher(query);
        if (routeMatcher.find()) {
            String departure = routeMatcher.group(1).trim();
            String arrival = routeMatcher.group(2).trim();
            logger.debug("Route pattern matched - departure: '{}', arrival: '{}'", departure, arrival);
            
            String depCode = findBestAirportMatch(departure);
            String arrCode = findBestAirportMatch(arrival);
            logger.debug("Airport codes - departure: '{}', arrival: '{}'", depCode, arrCode);
            
            if (depCode != null) params.put("departure", depCode);
            if (arrCode != null) params.put("arrival", arrCode);
            return;
        }
        
        // Try fuzzy route pattern
        Matcher fuzzyMatcher = FUZZY_ROUTE_PATTERN.matcher(query);
        if (fuzzyMatcher.find()) {
            String departure = fuzzyMatcher.group(1).trim();
            String arrival = fuzzyMatcher.group(2).trim();
            logger.debug("Fuzzy pattern matched - departure: '{}', arrival: '{}'", departure, arrival);
            
            String depCode = findBestAirportMatch(departure);
            String arrCode = findBestAirportMatch(arrival);
            logger.debug("Fuzzy airport codes - departure: '{}', arrival: '{}'", depCode, arrCode);
            
            if (depCode != null) params.put("departure", depCode);
            if (arrCode != null) params.put("arrival", arrCode);
        }
        
        logger.debug("Route extraction completed - params: {}", params);
    }
    
    /**
     * Find the best airport match with fuzzy matching
     */
    private String findBestAirportMatch(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        
        String cleanInput = input.toLowerCase().trim();
        logger.debug("Finding airport match for: '{}'", cleanInput);
        
        // Direct lookup first
        AirportInfo directMatch = AIRPORT_MAPPING.get(cleanInput);
        if (directMatch != null) {
            logger.debug("Direct match found: {} -> {}", cleanInput, directMatch.code);
            return directMatch.code;
        }
        
        // Fuzzy matching
        String bestMatch = null;
        double bestScore = 0.0;
        
        for (Map.Entry<String, AirportInfo> entry : AIRPORT_MAPPING.entrySet()) {
            String key = entry.getKey();
            double score = fuzzyMatchScore(cleanInput, key);
            
            if (score > 0.6 && score > bestScore) {
                bestScore = score;
                bestMatch = entry.getValue().code;
                logger.debug("Better fuzzy match: {} -> {} (score: {})", cleanInput, bestMatch, score);
            }
        }
        
        logger.debug("Best airport match for '{}': {} (score: {})", cleanInput, bestMatch, bestScore);
        return bestMatch;
    }
    
    /**
     * Calculate fuzzy match score between two strings
     */
    private double fuzzyMatchScore(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        if (s1.equals(s2)) return 1.0;
        
        // Simple similarity based on common characters
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        
        return 1.0 - (double) levenshteinDistance(s1, s2) / maxLen;
    }
    
    /**
     * Simple fuzzy matching
     */
    private boolean fuzzyMatch(String text, String pattern, double threshold) {
        return fuzzyMatchScore(text, pattern) >= threshold;
    }
    
    /**
     * Calculate Levenshtein distance
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    /**
     * Enhanced departure information extraction
     */
    private void extractEnhancedDepartureInfo(String query, Map<String, String> params) {
        String airportCode = findBestAirportMatch(extractLocationFromQuery(query, "departure"));
        if (airportCode != null) {
            params.put("departure", airportCode);
        }
    }
    
    /**
     * Enhanced arrival information extraction
     */
    private void extractEnhancedArrivalInfo(String query, Map<String, String> params) {
        String airportCode = findBestAirportMatch(extractLocationFromQuery(query, "arrival"));
        if (airportCode != null) {
            params.put("arrival", airportCode);
        }
    }
    
    /**
     * Extract location information from query based on context
     */
    private String extractLocationFromQuery(String query, String context) {
        // This is a simplified implementation - could be enhanced further
        String[] words = query.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            if (AIRPORT_MAPPING.containsKey(word)) {
                return word;
            }
            
            // Check multi-word locations
            if (i < words.length - 1) {
                String twoWords = word + " " + words[i + 1].toLowerCase();
                if (AIRPORT_MAPPING.containsKey(twoWords)) {
                    return twoWords;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Enhanced flight number extraction
     */
    private void extractEnhancedFlightNumberInfo(String query, Map<String, String> params) {
        Matcher matcher = FLIGHT_NUMBER_PATTERN.matcher(query);
        if (matcher.find()) {
            params.put("flightNumber", matcher.group(1).toUpperCase());
        }
    }
    
    /**
     * Enhanced limit extraction with more patterns
     */
    private void extractEnhancedLimit(String query, Map<String, String> params) {
        Pattern[] limitPatterns = {
            Pattern.compile("\\b(?:show|display|give|find)\\s+(?:me\\s+)?(?:the\\s+)?(?:top\\s+|first\\s+)?(\\d+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(\\d+)\\s+(?:flights|results)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\blimit\\s+(\\d+)", Pattern.CASE_INSENSITIVE)
        };
        
        for (Pattern pattern : limitPatterns) {
            Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                int limit = Math.min(Integer.parseInt(matcher.group(1)), 50);
                params.put("limit", String.valueOf(limit));
                return;
            }
        }
        
        params.put("limit", "10"); // Default
    }
    
    /**
     * Calculate confidence score for extracted parameters
     */
    private String calculateConfidence(Map<String, String> params) {
        double score = 0.5; // Base score
        
        if (params.containsKey("departure") && params.containsKey("arrival")) {
            score += 0.3;
        } else if (params.containsKey("departure") || params.containsKey("arrival")) {
            score += 0.2;
        }
        
        if (params.containsKey("airline")) {
            score += 0.1;
        }
        
        if (params.containsKey("flightNumber")) {
            score += 0.2;
        }
        
        if (params.containsKey("date")) {
            score += 0.1;
        }
        
        return String.format("%.2f", Math.min(score, 1.0));
    }
}
