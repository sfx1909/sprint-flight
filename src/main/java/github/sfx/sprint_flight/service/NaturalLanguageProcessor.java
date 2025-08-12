package github.sfx.sprint_flight.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NaturalLanguageProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(NaturalLanguageProcessor.class);
    
    // Dynamic country-to-airport mapping cache
    private final Map<String, List<String>> countryAirportCache = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initializeCountryMappings() {
        logger.info("Initializing dynamic country-to-airport mappings...");
        buildCountryAirportMappings();
        logger.info("Country mappings initialized with {} countries", countryAirportCache.size());
    }
    
    /**
     * Build comprehensive country-to-airport mappings
     */
    private void buildCountryAirportMappings() {
        // North America
        addCountryMapping("US", "JFK", "LAX", "ORD", "ATL", "DFW", "MIA", "SFO", "BOS", "SEA", "DEN", "LAS", "PHX", "IAH", "LGA", "EWR", "MDW");
        addCountryMapping("CA", "YYZ", "YVR", "YUL", "YYC", "YEG", "YOW");
        addCountryMapping("MX", "MEX", "CUN", "GDL");
        
        // Europe
        addCountryMapping("GB", "LHR", "LGW", "STN", "LTN", "MAN", "EDI", "GLA");
        addCountryMapping("FR", "CDG", "ORY", "NCE", "LYS");
        addCountryMapping("DE", "FRA", "MUC", "TXL", "DUS", "HAM", "CGN");
        addCountryMapping("IT", "FCO", "MXP", "LIN", "NAP", "VCE");
        addCountryMapping("ES", "MAD", "BCN", "PMI", "LAS");
        addCountryMapping("NL", "AMS");
        addCountryMapping("BE", "BRU");
        addCountryMapping("CH", "ZUR");
        addCountryMapping("AT", "VIE");
        addCountryMapping("DK", "CPH");
        addCountryMapping("SE", "ARN");
        addCountryMapping("NO", "OSL");
        addCountryMapping("FI", "HEL");
        addCountryMapping("RU", "SVO", "DME", "LED");
        addCountryMapping("PL", "WAW");
        addCountryMapping("CZ", "PRG");
        addCountryMapping("HU", "BUD");
        addCountryMapping("RO", "OTP");
        addCountryMapping("BG", "SOF");
        addCountryMapping("GR", "ATH");
        addCountryMapping("TR", "IST", "SAW");
        
        // Asia-Pacific
        addCountryMapping("JP", "NRT", "HND", "KIX", "ITM", "NGO");
        addCountryMapping("CN", "PEK", "PKX", "PVG", "SHA", "CAN", "SZX", "CTU", "XIY");
        addCountryMapping("KR", "ICN", "GMP", "PUS");
        addCountryMapping("IN", "DEL", "BOM", "BLR", "MAA", "CCU", "HYD", "COK", "AMD", "PNQ", "GOI");
        addCountryMapping("SG", "SIN");
        addCountryMapping("MY", "KUL");
        addCountryMapping("TH", "BKK", "DMK", "HKT");
        addCountryMapping("ID", "CGK", "DPS");
        addCountryMapping("PH", "MNL");
        addCountryMapping("VN", "SGN", "HAN");
        addCountryMapping("AU", "SYD", "MEL", "BNE", "PER", "ADL", "CNS");
        addCountryMapping("NZ", "AKL", "CHC", "WLG");
        
        // Middle East
        addCountryMapping("AE", "DXB", "AUH");
        addCountryMapping("QA", "DOH");
        addCountryMapping("SA", "RUH", "JED");
        addCountryMapping("KW", "KWI");
        addCountryMapping("BH", "BAH");
        addCountryMapping("OM", "MCT");
        addCountryMapping("IL", "TLV");
        addCountryMapping("JO", "AMM");
        addCountryMapping("LB", "BEY");
        addCountryMapping("IR", "IKA");
        
        // Africa
        addCountryMapping("ZA", "JNB", "CPT", "DUR");
        addCountryMapping("EG", "CAI");
        addCountryMapping("MA", "CMN");
        addCountryMapping("NG", "LOS", "ABV");
        addCountryMapping("KE", "NBO");
        addCountryMapping("ET", "ADD");
        addCountryMapping("GH", "ACC");
        
        // South America
        addCountryMapping("BR", "GRU", "GIG", "BSB", "FOR", "SSA", "REC");
        addCountryMapping("AR", "EZE", "AEP");
        addCountryMapping("CL", "SCL");
        addCountryMapping("PE", "LIM");
        addCountryMapping("CO", "BOG", "MDE");
        addCountryMapping("EC", "UIO");
        addCountryMapping("VE", "CCS");
        
        // Add common country name aliases
        addCountryAliases();
    }
    
    /**
     * Add country mapping with multiple airports
     */
    private void addCountryMapping(String countryCode, String... airportCodes) {
        countryAirportCache.put(countryCode, Arrays.asList(airportCodes));
    }
    
    /**
     * Add country name aliases for better recognition
     */
    private void addCountryAliases() {
        // Copy mappings for common country name variations
        Map<String, String> aliases = new HashMap<>();
        aliases.put("united states", "US");
        aliases.put("usa", "US");
        aliases.put("america", "US");
        aliases.put("united states of america", "US");
        
        aliases.put("united kingdom", "GB");
        aliases.put("uk", "GB");
        aliases.put("britain", "GB");
        aliases.put("england", "GB");
        aliases.put("great britain", "GB");
        
        aliases.put("germany", "DE");
        aliases.put("deutschland", "DE");
        
        aliases.put("france", "FR");
        aliases.put("spain", "ES");
        aliases.put("italy", "IT");
        aliases.put("japan", "JP");
        aliases.put("china", "CN");
        aliases.put("india", "IN");
        aliases.put("australia", "AU");
        aliases.put("canada", "CA");
        aliases.put("brazil", "BR");
        aliases.put("south africa", "ZA");
        aliases.put("russia", "RU");
        aliases.put("south korea", "KR");
        aliases.put("korea", "KR");
        aliases.put("netherlands", "NL");
        aliases.put("holland", "NL");
        aliases.put("switzerland", "CH");
        aliases.put("mexico", "MX");
        aliases.put("turkey", "TR");
        
        // Add alias mappings
        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            String aliasName = entry.getKey();
            String countryCode = entry.getValue();
            List<String> airports = countryAirportCache.get(countryCode);
            if (airports != null) {
                countryAirportCache.put(aliasName, airports);
            }
        }
    }
    
    /**
     * Get airports for a country with enhanced lookup
     */
    public List<String> getAirportsForCountry(String countryInput) {
        if (countryInput == null) return Arrays.asList();
        
        String normalized = countryInput.toLowerCase().trim();
        
        // Direct lookup
        List<String> airports = countryAirportCache.get(normalized);
        if (airports != null) {
            logger.debug("Found {} airports for country: {}", airports.size(), countryInput);
            return airports;
        }
        
        // Try uppercase (for country codes)
        airports = countryAirportCache.get(countryInput.toUpperCase());
        if (airports != null) {
            logger.debug("Found {} airports for country code: {}", airports.size(), countryInput);
            return airports;
        }
        
        // Try fuzzy matching
        for (Map.Entry<String, List<String>> entry : countryAirportCache.entrySet()) {
            String key = entry.getKey();
            if (key.length() > 2 && (key.contains(normalized) || normalized.contains(key))) {
                logger.debug("Fuzzy match found {} airports for country: {} (matched: {})", 
                           entry.getValue().size(), countryInput, key);
                return entry.getValue();
            }
        }
        
        logger.debug("No airports found for country: {}", countryInput);
        return Arrays.asList();
    }
    
    /**
     * Get major airports for a country (first 3-5 airports which are usually the major ones)
     */
    public List<String> getMajorAirportsForCountry(String countryInput) {
        List<String> allAirports = getAirportsForCountry(countryInput);
        return allAirports.stream().limit(5).collect(Collectors.toList());
    }
    
    /**
     * Enhanced route extraction with country-to-airport suggestions
     */
    private void extractEnhancedRouteInfoWithCountrySupport(String query, Map<String, String> params) {
        logger.debug("Extracting route info with country support from: {}", query);
        
        // Try the original route patterns first
        extractEnhancedRouteInfo(query, params);
        
        // If no specific airports found, try country-level matching
        if (!params.containsKey("departure") && !params.containsKey("arrival")) {
            String[] words = query.toLowerCase().split("\\s+");
            String fromCountry = null;
            String toCountry = null;
            
            // Look for "from [country] to [country]" patterns
            for (int i = 0; i < words.length - 2; i++) {
                if (words[i].equals("from")) {
                    // Extract country name (could be multiple words)
                    StringBuilder countryName = new StringBuilder();
                    int j = i + 1;
                    while (j < words.length && !words[j].equals("to")) {
                        if (countryName.length() > 0) countryName.append(" ");
                        countryName.append(words[j]);
                        j++;
                    }
                    fromCountry = countryName.toString();
                    
                    // Extract destination country
                    if (j < words.length - 1) {
                        StringBuilder destCountry = new StringBuilder();
                        for (int k = j + 1; k < words.length; k++) {
                            if (destCountry.length() > 0) destCountry.append(" ");
                            destCountry.append(words[k]);
                        }
                        toCountry = destCountry.toString();
                    }
                    break;
                }
            }
            
            // If we found country names, suggest major airports
            if (fromCountry != null && !fromCountry.isEmpty()) {
                List<String> depAirports = getMajorAirportsForCountry(fromCountry);
                if (!depAirports.isEmpty()) {
                    params.put("departure", depAirports.get(0)); // Use primary airport
                    params.put("departure_alternatives", String.join(",", depAirports));
                    logger.debug("Suggested departure airports for {}: {}", fromCountry, depAirports);
                }
            }
            
            if (toCountry != null && !toCountry.isEmpty()) {
                List<String> arrAirports = getMajorAirportsForCountry(toCountry);
                if (!arrAirports.isEmpty()) {
                    params.put("arrival", arrAirports.get(0)); // Use primary airport
                    params.put("arrival_alternatives", String.join(",", arrAirports));
                    logger.debug("Suggested arrival airports for {}: {}", toCountry, arrAirports);
                }
            }
        }
        
        logger.debug("Enhanced route extraction completed - params: {}", params);
    }
    
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
        addAirport("MAA", "Chennai International", "chennai", "maa", "madras");
        addAirport("CCU", "Netaji Subhash Chandra Bose International", "kolkata", "ccu", "calcutta");
        addAirport("HYD", "Rajiv Gandhi International", "hyderabad", "hyd");
        
        // European Airports
        addAirport("MAD", "Madrid-Barajas", "madrid", "mad", "barajas", "spain");
        addAirport("BCN", "Barcelona-El Prat", "barcelona", "bcn", "el prat", "spain");
        addAirport("FCO", "Leonardo da Vinci-Fiumicino", "rome", "fco", "fiumicino", "italy");
        addAirport("MXP", "Milan Malpensa", "milan", "mxp", "malpensa", "italy");
        addAirport("VIE", "Vienna International", "vienna", "vie", "austria");
        addAirport("ZUR", "Zurich Airport", "zurich", "zur", "switzerland");
        addAirport("CPH", "Copenhagen Airport", "copenhagen", "cph", "denmark");
        addAirport("OSL", "Oslo Airport", "oslo", "osl", "norway");
        addAirport("ARN", "Stockholm Arlanda", "stockholm", "arn", "arlanda", "sweden");
        addAirport("HEL", "Helsinki-Vantaa", "helsinki", "hel", "vantaa", "finland");
        addAirport("WAW", "Warsaw Chopin", "warsaw", "waw", "chopin", "poland");
        addAirport("PRG", "Václav Havel Airport Prague", "prague", "prg", "czech republic");
        addAirport("BUD", "Budapest Ferenc Liszt International", "budapest", "bud", "hungary");
        addAirport("OTP", "Henri Coandă International", "bucharest", "otp", "romania");
        addAirport("SOF", "Sofia Airport", "sofia", "sof", "bulgaria");
        addAirport("ATH", "Athens International", "athens", "ath", "greece");
        addAirport("IST", "Istanbul Airport", "istanbul", "ist", "turkey");
        addAirport("SAW", "Sabiha Gökçen International", "istanbul sabiha", "saw", "turkey");
        
        // Middle East Airports
        addAirport("TLV", "Ben Gurion Airport", "tel aviv", "tlv", "ben gurion", "israel");
        addAirport("CAI", "Cairo International", "cairo", "cai", "egypt");
        addAirport("RUH", "King Khalid International", "riyadh", "ruh", "saudi arabia");
        addAirport("JED", "King Abdulaziz International", "jeddah", "jed", "saudi arabia");
        addAirport("KWI", "Kuwait International", "kuwait", "kwi", "kuwait city");
        addAirport("BAH", "Bahrain International", "bahrain", "bah", "manama");
        addAirport("MCT", "Muscat International", "muscat", "mct", "oman");
        addAirport("AMM", "Queen Alia International", "amman", "amm", "jordan");
        addAirport("BEY", "Rafic Hariri International", "beirut", "bey", "lebanon");
        addAirport("DAM", "Damascus International", "damascus", "dam", "syria");
        addAirport("BGW", "Baghdad International", "baghdad", "bgw", "iraq");
        addAirport("IKA", "Imam Khomeini International", "tehran", "ika", "iran");
        
        // African Airports (Extended)
        addAirport("CAI", "Cairo International", "cairo", "cai", "egypt");
        addAirport("CMN", "Mohammed V International", "casablanca", "cmn", "morocco");
        addAirport("TUN", "Tunis-Carthage International", "tunis", "tun", "tunisia");
        addAirport("ALG", "Houari Boumediene Airport", "algiers", "alg", "algeria");
        addAirport("LOS", "Murtala Muhammed International", "lagos", "los", "nigeria");
        addAirport("ABV", "Nnamdi Azikiwe International", "abuja", "abv", "nigeria");
        addAirport("ACC", "Kotoka International", "accra", "acc", "ghana");
        addAirport("ABJ", "Félix-Houphouët-Boigny International", "abidjan", "abj", "ivory coast");
        addAirport("DKR", "Blaise Diagne International", "dakar", "dkr", "senegal");
        addAirport("NBO", "Jomo Kenyatta International", "nairobi", "nbo", "kenya");
        addAirport("ADD", "Addis Ababa Bole International", "addis ababa", "add", "ethiopia");
        addAirport("EBB", "Entebbe International", "entebbe", "ebb", "kampala", "uganda");
        addAirport("DAR", "Julius Nyerere International", "dar es salaam", "dar", "tanzania");
        addAirport("LUN", "Kenneth Kaunda International", "lusaka", "lun", "zambia");
        addAirport("HRE", "Robert Gabriel Mugabe International", "harare", "hre", "zimbabwe");
        addAirport("GBE", "Sir Seretse Khama International", "gaborone", "gbe", "botswana");
        addAirport("WDH", "Hosea Kutako International", "windhoek", "wdh", "namibia");
        addAirport("MPM", "Maputo International", "maputo", "mpm", "mozambique");
        
        // Asian Airports (Extended)
        addAirport("PEK", "Beijing Capital International", "beijing", "pek", "capital", "china");
        addAirport("PKX", "Beijing Daxing International", "beijing daxing", "pkx", "daxing", "china");
        addAirport("PVG", "Shanghai Pudong International", "shanghai", "pvg", "pudong", "china");
        addAirport("SHA", "Shanghai Hongqiao International", "shanghai hongqiao", "sha", "hongqiao", "china");
        addAirport("CAN", "Guangzhou Tianhe International", "guangzhou", "can", "tianhe", "china");
        addAirport("SZX", "Shenzhen Bao'an International", "shenzhen", "szx", "baoan", "china");
        addAirport("CGK", "Soekarno-Hatta International", "jakarta", "cgk", "soekarno hatta", "indonesia");
        addAirport("DPS", "Ngurah Rai International", "bali", "dps", "denpasar", "ngurah rai", "indonesia");
        addAirport("SUB", "Juanda International", "surabaya", "sub", "juanda", "indonesia");
        addAirport("KUL", "Kuala Lumpur International", "kuala lumpur", "kul", "malaysia");
        addAirport("BKK", "Suvarnabhumi Airport", "bangkok", "bkk", "suvarnabhumi", "thailand");
        addAirport("DMK", "Don Mueang International", "bangkok don mueang", "dmk", "don mueang", "thailand");
        addAirport("HKT", "Phuket International", "phuket", "hkt", "thailand");
        addAirport("SGN", "Tan Son Nhat International", "ho chi minh", "sgn", "saigon", "vietnam");
        addAirport("HAN", "Noi Bai International", "hanoi", "han", "noi bai", "vietnam");
        addAirport("MNL", "Ninoy Aquino International", "manila", "mnl", "ninoy aquino", "philippines");
        addAirport("CEB", "Mactan-Cebu International", "cebu", "ceb", "mactan", "philippines");
        addAirport("RGN", "Yangon International", "yangon", "rgn", "rangoon", "myanmar");
        addAirport("PNH", "Phnom Penh International", "phnom penh", "pnh", "cambodia");
        addAirport("VTE", "Wattay International", "vientiane", "vte", "wattay", "laos");
        addAirport("BWN", "Brunei International", "bandar seri begawan", "bwn", "brunei");
        addAirport("CMB", "Bandaranaike International", "colombo", "cmb", "bandaranaike", "sri lanka");
        addAirport("MLE", "Malé International", "male", "mle", "maldives");
        addAirport("KTM", "Tribhuvan International", "kathmandu", "ktm", "tribhuvan", "nepal");
        addAirport("DAC", "Hazrat Shahjalal International", "dhaka", "dac", "hazrat shahjalal", "bangladesh");
        addAirport("CXB", "Cox's Bazar Airport", "coxs bazar", "cxb", "bangladesh");
        
        // Latin American Airports
        addAirport("GRU", "São Paulo/Guarulhos International", "sao paulo", "gru", "guarulhos", "brazil");
        addAirport("GIG", "Rio de Janeiro/Galeão International", "rio de janeiro", "gig", "galeao", "brazil");
        addAirport("BSB", "Brasília International", "brasilia", "bsb", "brazil");
        addAirport("FOR", "Fortaleza International", "fortaleza", "for", "brazil");
        addAirport("SSA", "Salvador International", "salvador", "ssa", "brazil");
        addAirport("REC", "Recife International", "recife", "rec", "brazil");
        addAirport("EZE", "Ezeiza International", "buenos aires", "eze", "ezeiza", "argentina");
        addAirport("AEP", "Jorge Newbery Airfield", "buenos aires jorge newbery", "aep", "argentina");
        addAirport("SCL", "Santiago International", "santiago", "scl", "chile");
        addAirport("LIM", "Jorge Chávez International", "lima", "lim", "jorge chavez", "peru");
        addAirport("BOG", "El Dorado International", "bogota", "bog", "el dorado", "colombia");
        addAirport("MDE", "José María Córdova International", "medellin", "mde", "jose maria cordova", "colombia");
        addAirport("UIO", "Mariscal Sucre International", "quito", "uio", "mariscal sucre", "ecuador");
        addAirport("GYE", "José Joaquín de Olmedo International", "guayaquil", "gye", "ecuador");
        addAirport("CCS", "Simón Bolívar International", "caracas", "ccs", "simon bolivar", "venezuela");
        addAirport("ASU", "Silvio Pettirossi International", "asuncion", "asu", "silvio pettirossi", "paraguay");
        addAirport("MVD", "Carrasco International", "montevideo", "mvd", "carrasco", "uruguay");
        addAirport("LPB", "El Alto International", "la paz", "lpb", "el alto", "bolivia");
        addAirport("VVI", "Viru Viru International", "santa cruz", "vvi", "viru viru", "bolivia");
        addAirport("GEO", "Cheddi Jagan International", "georgetown", "geo", "cheddi jagan", "guyana");
        addAirport("PBM", "Johan Adolf Pengel International", "paramaribo", "pbm", "johan adolf pengel", "suriname");
        
        // Caribbean Airports
        addAirport("HAV", "José Martí International", "havana", "hav", "jose marti", "cuba");
        addAirport("SDQ", "Las Américas International", "santo domingo", "sdq", "las americas", "dominican republic");
        addAirport("PUJ", "Punta Cana International", "punta cana", "puj", "dominican republic");
        addAirport("SJU", "Luis Muñoz Marín International", "san juan", "sju", "luis munoz marin", "puerto rico");
        addAirport("NAS", "Lynden Pindling International", "nassau", "nas", "lynden pindling", "bahamas");
        addAirport("KIN", "Norman Manley International", "kingston", "kin", "norman manley", "jamaica");
        addAirport("MBJ", "Sangster International", "montego bay", "mbj", "sangster", "jamaica");
        addAirport("BGI", "Grantley Adams International", "bridgetown", "bgi", "grantley adams", "barbados");
        addAirport("POS", "Piarco International", "port of spain", "pos", "piarco", "trinidad");
        addAirport("CUR", "Hato International", "curacao", "cur", "hato", "willemstad");
        addAirport("AUA", "Queen Beatrix International", "aruba", "aua", "queen beatrix", "oranjestad");
        
        // Pacific Islands
        addAirport("HNL", "Daniel K. Inouye International", "honolulu", "hnl", "daniel inouye", "hawaii");
        addAirport("OGG", "Kahului Airport", "maui", "ogg", "kahului", "hawaii");
        addAirport("KOA", "Ellison Onizuka Kona International", "kona", "koa", "big island", "hawaii");
        addAirport("LIH", "Lihue Airport", "kauai", "lih", "lihue", "hawaii");
        addAirport("GUM", "Antonio B. Won Pat International", "guam", "gum", "antonio won pat");
        addAirport("NOU", "La Tontouta International", "noumea", "nou", "la tontouta", "new caledonia");
        addAirport("PPT", "Faa'a International", "tahiti", "ppt", "faaa", "papeete");
        addAirport("NAN", "Nadi International", "nadi", "nan", "fiji");
        addAirport("SUV", "Nausori Airport", "suva", "suv", "nausori", "fiji");
        addAirport("VLI", "Bauerfield International", "port vila", "vli", "bauerfield", "vanuatu");
        addAirport("HIR", "Honiara International", "honiara", "hir", "solomon islands");
        addAirport("POM", "Jacksons International", "port moresby", "pom", "jacksons", "papua new guinea");
        addAirport("APW", "Faleolo International", "apia", "apw", "faleolo", "samoa");
        addAirport("TBU", "Fuaʻamotu International", "nuku'alofa", "tbu", "fuaamotu", "tonga");
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
                extractEnhancedRouteInfoWithCountrySupport(lowerQuery, params);
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
     * Enhanced query type determination with country support
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
        
        // Check for country-level queries
        if (containsCountryPatterns(query)) {
            return "route"; // Treat country queries as route queries for now
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
     * Check for country-level patterns in queries
     */
    private boolean containsCountryPatterns(String query) {
        String[] countryKeywords = {"country", "nation", "flights to", "flights from", "from", "to"};
        
        for (String keyword : countryKeywords) {
            if (query.contains(keyword)) {
                // Check if any of our known countries are mentioned
                for (String country : countryAirportCache.keySet()) {
                    if (query.contains(country.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        
        return false;
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
    
    /**
     * Get supported countries for help/suggestions
     */
    public List<String> getSupportedCountries() {
        return countryAirportCache.keySet().stream()
                .filter(key -> key.length() == 2) // Only return country codes
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Get airport suggestions for a country
     */
    public String getAirportSuggestionsForCountry(String countryInput) {
        List<String> airports = getAirportsForCountry(countryInput);
        if (airports.isEmpty()) {
            return "No airports found for " + countryInput;
        }
        
        return "Major airports in " + countryInput + ": " + String.join(", ", airports);
    }
    
    /**
     * Enhanced parameter extraction that includes country-level suggestions
     */
    public Map<String, String> extractFlightParametersWithSuggestions(String query) {
        Map<String, String> params = extractFlightParameters(query);
        
        // Add country suggestions if specific airports weren't found
        if (!params.containsKey("departure") && !params.containsKey("arrival")) {
            String lowerQuery = query.toLowerCase();
            
            // Look for country mentions and suggest airports
            for (String country : countryAirportCache.keySet()) {
                if (lowerQuery.contains(country.toLowerCase())) {
                    List<String> airports = countryAirportCache.get(country);
                    if (!airports.isEmpty()) {
                        params.put("suggested_country", country);
                        params.put("suggested_airports", String.join(",", airports.subList(0, Math.min(3, airports.size()))));
                        break;
                    }
                }
            }
        }
        
        return params;
    }
}
