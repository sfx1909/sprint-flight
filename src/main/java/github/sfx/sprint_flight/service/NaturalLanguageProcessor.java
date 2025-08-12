package github.sfx.sprint_flight.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class NaturalLanguageProcessor {
    
    // Airport codes mapping
    private final Map<String, String> airportCodes = new HashMap<>();
    private final Map<String, String> airlineCodes = new HashMap<>();
    
    // Patterns for different query types
    private final Pattern flightNumberPattern = Pattern.compile("\\b([A-Z]{1,3}\\d{1,4})\\b", Pattern.CASE_INSENSITIVE);
    private final Pattern routePattern = Pattern.compile("\\b(from|leaving|departing)\\s+([A-Z]{3}|[a-zA-Z\\s]+)\\s+(to|going|arriving)\\s+([A-Z]{3}|[a-zA-Z\\s]+)\\b", Pattern.CASE_INSENSITIVE);
    private final Pattern departurePattern = Pattern.compile("\\b(from|leaving|departing|out of)\\s+([A-Z]{3}|[a-zA-Z\\s]+)\\b", Pattern.CASE_INSENSITIVE);
    private final Pattern arrivalPattern = Pattern.compile("\\b(to|going to|arriving at|landing at)\\s+([A-Z]{3}|[a-zA-Z\\s]+)\\b", Pattern.CASE_INSENSITIVE);
    private final Pattern airlinePattern = Pattern.compile("\\b(on|with|by)\\s+([A-Z]{2,3}|american|delta|united|southwest|jetblue|alaska|spirit|frontier)\\b", Pattern.CASE_INSENSITIVE);
    
    public NaturalLanguageProcessor() {
        initializeAirportCodes();
        initializeAirlineCodes();
    }
    
    private void initializeAirportCodes() {
        // Major US airports
        airportCodes.put("new york", "JFK");
        airportCodes.put("nyc", "JFK");
        airportCodes.put("kennedy", "JFK");
        airportCodes.put("jfk", "JFK");
        airportCodes.put("laguardia", "LGA");
        airportCodes.put("lga", "LGA");
        airportCodes.put("newark", "EWR");
        airportCodes.put("ewr", "EWR");
        
        airportCodes.put("los angeles", "LAX");
        airportCodes.put("la", "LAX");
        airportCodes.put("lax", "LAX");
        
        airportCodes.put("chicago", "ORD");
        airportCodes.put("ohare", "ORD");
        airportCodes.put("ord", "ORD");
        airportCodes.put("midway", "MDW");
        airportCodes.put("mdw", "MDW");
        
        airportCodes.put("atlanta", "ATL");
        airportCodes.put("atl", "ATL");
        
        airportCodes.put("dallas", "DFW");
        airportCodes.put("dfw", "DFW");
        
        airportCodes.put("denver", "DEN");
        airportCodes.put("den", "DEN");
        
        airportCodes.put("san francisco", "SFO");
        airportCodes.put("sf", "SFO");
        airportCodes.put("sfo", "SFO");
        
        airportCodes.put("miami", "MIA");
        airportCodes.put("mia", "MIA");
        
        airportCodes.put("seattle", "SEA");
        airportCodes.put("sea", "SEA");
        
        airportCodes.put("boston", "BOS");
        airportCodes.put("bos", "BOS");
        
        airportCodes.put("washington", "DCA");
        airportCodes.put("dc", "DCA");
        airportCodes.put("dulles", "IAD");
        airportCodes.put("iad", "IAD");
        airportCodes.put("dca", "DCA");
        
        airportCodes.put("phoenix", "PHX");
        airportCodes.put("phx", "PHX");
        
        airportCodes.put("las vegas", "LAS");
        airportCodes.put("vegas", "LAS");
        airportCodes.put("las", "LAS");
        
        airportCodes.put("orlando", "MCO");
        airportCodes.put("mco", "MCO");
        
        airportCodes.put("houston", "IAH");
        airportCodes.put("iah", "IAH");
        airportCodes.put("hobby", "HOU");
        airportCodes.put("hou", "HOU");
        
        airportCodes.put("san diego", "SAN");
        airportCodes.put("san", "SAN");
        
        airportCodes.put("minneapolis", "MSP");
        airportCodes.put("msp", "MSP");
        
        airportCodes.put("detroit", "DTW");
        airportCodes.put("dtw", "DTW");
        
        airportCodes.put("salt lake city", "SLC");
        airportCodes.put("slc", "SLC");
        
        // EUROPE
        airportCodes.put("london", "LHR");
        airportCodes.put("heathrow", "LHR");
        airportCodes.put("lhr", "LHR");
        airportCodes.put("gatwick", "LGW");
        airportCodes.put("lgw", "LGW");
        airportCodes.put("stansted", "STN");
        airportCodes.put("stn", "STN");
        airportCodes.put("luton", "LTN");
        airportCodes.put("ltn", "LTN");
        
        airportCodes.put("paris", "CDG");
        airportCodes.put("charles de gaulle", "CDG");
        airportCodes.put("cdg", "CDG");
        airportCodes.put("orly", "ORY");
        airportCodes.put("ory", "ORY");
        
        airportCodes.put("frankfurt", "FRA");
        airportCodes.put("fra", "FRA");
        
        airportCodes.put("amsterdam", "AMS");
        airportCodes.put("schiphol", "AMS");
        airportCodes.put("ams", "AMS");
        
        airportCodes.put("madrid", "MAD");
        airportCodes.put("barajas", "MAD");
        airportCodes.put("mad", "MAD");
        
        airportCodes.put("barcelona", "BCN");
        airportCodes.put("bcn", "BCN");
        
        airportCodes.put("rome", "FCO");
        airportCodes.put("fiumicino", "FCO");
        airportCodes.put("fco", "FCO");
        
        airportCodes.put("milan", "MXP");
        airportCodes.put("malpensa", "MXP");
        airportCodes.put("mxp", "MXP");
        
        airportCodes.put("zurich", "ZUR");
        airportCodes.put("zur", "ZUR");
        
        airportCodes.put("vienna", "VIE");
        airportCodes.put("vie", "VIE");
        
        airportCodes.put("munich", "MUC");
        airportCodes.put("muc", "MUC");
        
        airportCodes.put("copenhagen", "CPH");
        airportCodes.put("cph", "CPH");
        
        airportCodes.put("stockholm", "ARN");
        airportCodes.put("arlanda", "ARN");
        airportCodes.put("arn", "ARN");
        
        airportCodes.put("oslo", "OSL");
        airportCodes.put("osl", "OSL");
        
        airportCodes.put("helsinki", "HEL");
        airportCodes.put("hel", "HEL");
        
        airportCodes.put("dublin", "DUB");
        airportCodes.put("dub", "DUB");
        
        airportCodes.put("brussels", "BRU");
        airportCodes.put("bru", "BRU");
        
        airportCodes.put("lisbon", "LIS");
        airportCodes.put("lis", "LIS");
        
        airportCodes.put("athens", "ATH");
        airportCodes.put("ath", "ATH");
        
        airportCodes.put("istanbul", "IST");
        airportCodes.put("ist", "IST");
        
        // ASIA-PACIFIC
        airportCodes.put("tokyo", "NRT");
        airportCodes.put("narita", "NRT");
        airportCodes.put("nrt", "NRT");
        airportCodes.put("haneda", "HND");
        airportCodes.put("hnd", "HND");
        
        airportCodes.put("osaka", "KIX");
        airportCodes.put("kansai", "KIX");
        airportCodes.put("kix", "KIX");
        
        airportCodes.put("seoul", "ICN");
        airportCodes.put("incheon", "ICN");
        airportCodes.put("icn", "ICN");
        
        airportCodes.put("beijing", "PEK");
        airportCodes.put("pek", "PEK");
        
        airportCodes.put("shanghai", "PVG");
        airportCodes.put("pudong", "PVG");
        airportCodes.put("pvg", "PVG");
        
        airportCodes.put("hong kong", "HKG");
        airportCodes.put("hkg", "HKG");
        
        airportCodes.put("singapore", "SIN");
        airportCodes.put("changi", "SIN");
        airportCodes.put("sin", "SIN");
        
        airportCodes.put("bangkok", "BKK");
        airportCodes.put("suvarnabhumi", "BKK");
        airportCodes.put("bkk", "BKK");
        
        airportCodes.put("kuala lumpur", "KUL");
        airportCodes.put("kul", "KUL");
        
        airportCodes.put("jakarta", "CGK");
        airportCodes.put("soekarno hatta", "CGK");
        airportCodes.put("cgk", "CGK");
        
        airportCodes.put("manila", "MNL");
        airportCodes.put("mnl", "MNL");
        
        airportCodes.put("delhi", "DEL");
        airportCodes.put("new delhi", "DEL");
        airportCodes.put("del", "DEL");
        
        airportCodes.put("mumbai", "BOM");
        airportCodes.put("bombay", "BOM");
        airportCodes.put("bom", "BOM");
        
        airportCodes.put("sydney", "SYD");
        airportCodes.put("kingsford smith", "SYD");
        airportCodes.put("syd", "SYD");
        
        airportCodes.put("melbourne", "MEL");
        airportCodes.put("mel", "MEL");
        
        airportCodes.put("brisbane", "BNE");
        airportCodes.put("bne", "BNE");
        
        airportCodes.put("perth", "PER");
        airportCodes.put("per", "PER");
        
        airportCodes.put("auckland", "AKL");
        airportCodes.put("akl", "AKL");
        
        // CANADA
        airportCodes.put("toronto", "YYZ");
        airportCodes.put("pearson", "YYZ");
        airportCodes.put("yyz", "YYZ");
        
        airportCodes.put("vancouver", "YVR");
        airportCodes.put("yvr", "YVR");
        
        airportCodes.put("montreal", "YUL");
        airportCodes.put("yul", "YUL");
        
        airportCodes.put("calgary", "YYC");
        airportCodes.put("yyc", "YYC");
        
        // MIDDLE EAST & AFRICA
        airportCodes.put("dubai", "DXB");
        airportCodes.put("dxb", "DXB");
        
        airportCodes.put("abu dhabi", "AUH");
        airportCodes.put("auh", "AUH");
        
        airportCodes.put("doha", "DOH");
        airportCodes.put("hamad", "DOH");
        airportCodes.put("doh", "DOH");
        
        airportCodes.put("riyadh", "RUH");
        airportCodes.put("ruh", "RUH");
        
        airportCodes.put("jeddah", "JED");
        airportCodes.put("jed", "JED");
        
        airportCodes.put("kuwait", "KWI");
        airportCodes.put("kwi", "KWI");
        
        airportCodes.put("tel aviv", "TLV");
        airportCodes.put("ben gurion", "TLV");
        airportCodes.put("tlv", "TLV");
        
        airportCodes.put("cairo", "CAI");
        airportCodes.put("cai", "CAI");
        
        airportCodes.put("casablanca", "CMN");
        airportCodes.put("mohammed v", "CMN");
        airportCodes.put("cmn", "CMN");
        
        airportCodes.put("johannesburg", "JNB");
        airportCodes.put("or tambo", "JNB");
        airportCodes.put("jnb", "JNB");
        
        airportCodes.put("cape town", "CPT");
        airportCodes.put("cpt", "CPT");
        
        // SOUTH AMERICA
        airportCodes.put("sao paulo", "GRU");
        airportCodes.put("guarulhos", "GRU");
        airportCodes.put("gru", "GRU");
        
        airportCodes.put("rio de janeiro", "GIG");
        airportCodes.put("galeao", "GIG");
        airportCodes.put("gig", "GIG");
        
        airportCodes.put("buenos aires", "EZE");
        airportCodes.put("ezeiza", "EZE");
        airportCodes.put("eze", "EZE");
        
        airportCodes.put("lima", "LIM");
        airportCodes.put("jorge chavez", "LIM");
        airportCodes.put("lim", "LIM");
        
        airportCodes.put("bogota", "BOG");
        airportCodes.put("bog", "BOG");
        
        airportCodes.put("mexico city", "MEX");
        airportCodes.put("mex", "MEX");
        
        airportCodes.put("cancun", "CUN");
        airportCodes.put("cun", "CUN");
        
        // RUSSIA
        airportCodes.put("moscow", "SVO");
        airportCodes.put("sheremetyevo", "SVO");
        airportCodes.put("svo", "SVO");
        airportCodes.put("domodedovo", "DME");
        airportCodes.put("dme", "DME");
        
        airportCodes.put("st petersburg", "LED");
        airportCodes.put("pulkovo", "LED");
        airportCodes.put("led", "LED");
    }
    
    private void initializeAirlineCodes() {
        // NORTH AMERICA
        airlineCodes.put("american", "AA");
        airlineCodes.put("american airlines", "AA");
        airlineCodes.put("aa", "AA");
        
        airlineCodes.put("delta", "DL");
        airlineCodes.put("delta airlines", "DL");
        airlineCodes.put("dl", "DL");
        
        airlineCodes.put("united", "UA");
        airlineCodes.put("united airlines", "UA");
        airlineCodes.put("ua", "UA");
        
        airlineCodes.put("southwest", "WN");
        airlineCodes.put("southwest airlines", "WN");
        airlineCodes.put("wn", "WN");
        
        airlineCodes.put("jetblue", "B6");
        airlineCodes.put("jetblue airways", "B6");
        airlineCodes.put("b6", "B6");
        
        airlineCodes.put("alaska", "AS");
        airlineCodes.put("alaska airlines", "AS");
        airlineCodes.put("as", "AS");
        
        airlineCodes.put("spirit", "NK");
        airlineCodes.put("spirit airlines", "NK");
        airlineCodes.put("nk", "NK");
        
        airlineCodes.put("frontier", "F9");
        airlineCodes.put("frontier airlines", "F9");
        airlineCodes.put("f9", "F9");
        
        // CANADA
        airlineCodes.put("air canada", "AC");
        airlineCodes.put("ac", "AC");
        
        airlineCodes.put("westjet", "WS");
        airlineCodes.put("ws", "WS");
        
        // EUROPE
        airlineCodes.put("british airways", "BA");
        airlineCodes.put("ba", "BA");
        
        airlineCodes.put("lufthansa", "LH");
        airlineCodes.put("lh", "LH");
        
        airlineCodes.put("air france", "AF");
        airlineCodes.put("af", "AF");
        
        airlineCodes.put("klm", "KL");
        airlineCodes.put("klm royal dutch airlines", "KL");
        airlineCodes.put("kl", "KL");
        
        airlineCodes.put("swiss", "LX");
        airlineCodes.put("swiss international", "LX");
        airlineCodes.put("lx", "LX");
        
        airlineCodes.put("alitalia", "AZ");
        airlineCodes.put("az", "AZ");
        
        airlineCodes.put("iberia", "IB");
        airlineCodes.put("ib", "IB");
        
        airlineCodes.put("sas", "SK");
        airlineCodes.put("scandinavian airlines", "SK");
        airlineCodes.put("sk", "SK");
        
        airlineCodes.put("finnair", "AY");
        airlineCodes.put("ay", "AY");
        
        airlineCodes.put("virgin atlantic", "VS");
        airlineCodes.put("vs", "VS");
        
        airlineCodes.put("ryanair", "FR");
        airlineCodes.put("fr", "FR");
        
        airlineCodes.put("easyjet", "U2");
        airlineCodes.put("u2", "U2");
        
        airlineCodes.put("austrian", "OS");
        airlineCodes.put("austrian airlines", "OS");
        airlineCodes.put("os", "OS");
        
        airlineCodes.put("tap", "TP");
        airlineCodes.put("tap air portugal", "TP");
        airlineCodes.put("tp", "TP");
        
        airlineCodes.put("aer lingus", "EI");
        airlineCodes.put("ei", "EI");
        
        airlineCodes.put("norwegian", "DY");
        airlineCodes.put("norwegian air", "DY");
        airlineCodes.put("dy", "DY");
        
        // MIDDLE EAST
        airlineCodes.put("emirates", "EK");
        airlineCodes.put("ek", "EK");
        
        airlineCodes.put("qatar airways", "QR");
        airlineCodes.put("qatar", "QR");
        airlineCodes.put("qr", "QR");
        
        airlineCodes.put("etihad", "EY");
        airlineCodes.put("etihad airways", "EY");
        airlineCodes.put("ey", "EY");
        
        airlineCodes.put("turkish airlines", "TK");
        airlineCodes.put("turkish", "TK");
        airlineCodes.put("tk", "TK");
        
        airlineCodes.put("el al", "LY");
        airlineCodes.put("ly", "LY");
        
        airlineCodes.put("saudia", "SV");
        airlineCodes.put("saudi arabian airlines", "SV");
        airlineCodes.put("sv", "SV");
        
        airlineCodes.put("kuwait airways", "KU");
        airlineCodes.put("ku", "KU");
        
        // ASIA-PACIFIC
        airlineCodes.put("singapore airlines", "SQ");
        airlineCodes.put("singapore", "SQ");
        airlineCodes.put("sq", "SQ");
        
        airlineCodes.put("cathay pacific", "CX");
        airlineCodes.put("cathay", "CX");
        airlineCodes.put("cx", "CX");
        
        airlineCodes.put("japan airlines", "JL");
        airlineCodes.put("jal", "JL");
        airlineCodes.put("jl", "JL");
        
        airlineCodes.put("ana", "NH");
        airlineCodes.put("all nippon airways", "NH");
        airlineCodes.put("nh", "NH");
        
        airlineCodes.put("korean air", "KE");
        airlineCodes.put("ke", "KE");
        
        airlineCodes.put("asiana", "OZ");
        airlineCodes.put("asiana airlines", "OZ");
        airlineCodes.put("oz", "OZ");
        
        airlineCodes.put("china eastern", "MU");
        airlineCodes.put("mu", "MU");
        
        airlineCodes.put("china southern", "CZ");
        airlineCodes.put("cz", "CZ");
        
        airlineCodes.put("air china", "CA");
        airlineCodes.put("ca", "CA");
        
        airlineCodes.put("thai airways", "TG");
        airlineCodes.put("thai", "TG");
        airlineCodes.put("tg", "TG");
        
        airlineCodes.put("malaysia airlines", "MH");
        airlineCodes.put("malaysia", "MH");
        airlineCodes.put("mh", "MH");
        
        airlineCodes.put("garuda indonesia", "GA");
        airlineCodes.put("garuda", "GA");
        airlineCodes.put("ga", "GA");
        
        airlineCodes.put("philippine airlines", "PR");
        airlineCodes.put("pal", "PR");
        airlineCodes.put("pr", "PR");
        
        airlineCodes.put("vietnam airlines", "VN");
        airlineCodes.put("vn", "VN");
        
        airlineCodes.put("air india", "AI");
        airlineCodes.put("ai", "AI");
        
        airlineCodes.put("indigo", "6E");
        airlineCodes.put("6e", "6E");
        
        airlineCodes.put("spicejet", "SG");
        airlineCodes.put("sg", "SG");
        
        airlineCodes.put("qantas", "QF");
        airlineCodes.put("qf", "QF");
        
        airlineCodes.put("jetstar", "JQ");
        airlineCodes.put("jq", "JQ");
        
        airlineCodes.put("virgin australia", "VA");
        airlineCodes.put("va", "VA");
        
        airlineCodes.put("air new zealand", "NZ");
        airlineCodes.put("nz", "NZ");
        
        // AFRICA
        airlineCodes.put("south african airways", "SA");
        airlineCodes.put("saa", "SA");
        airlineCodes.put("sa", "SA");
        
        airlineCodes.put("egyptair", "MS");
        airlineCodes.put("ms", "MS");
        
        airlineCodes.put("ethiopian airlines", "ET");
        airlineCodes.put("ethiopian", "ET");
        airlineCodes.put("et", "ET");
        
        airlineCodes.put("kenya airways", "KQ");
        airlineCodes.put("kq", "KQ");
        
        airlineCodes.put("royal air maroc", "AT");
        airlineCodes.put("ram", "AT");
        airlineCodes.put("at", "AT");
        
        // SOUTH AMERICA
        airlineCodes.put("latam", "LA");
        airlineCodes.put("la", "LA");
        
        airlineCodes.put("avianca", "AV");
        airlineCodes.put("av", "AV");
        
        airlineCodes.put("copa airlines", "CM");
        airlineCodes.put("copa", "CM");
        airlineCodes.put("cm", "CM");
        
        airlineCodes.put("azul", "AD");
        airlineCodes.put("ad", "AD");
        
        airlineCodes.put("gol", "G3");
        airlineCodes.put("g3", "G3");
        
        airlineCodes.put("aeromexico", "AM");
        airlineCodes.put("am", "AM");
        
        // RUSSIA & CIS
        airlineCodes.put("aeroflot", "SU");
        airlineCodes.put("su", "SU");
        
        airlineCodes.put("s7 airlines", "S7");
        airlineCodes.put("s7", "S7");
        
        airlineCodes.put("rossiya", "FV");
        airlineCodes.put("fv", "FV");
        
        // LOW COST CARRIERS
        airlineCodes.put("cebu pacific", "5J");
        airlineCodes.put("5j", "5J");
        
        airlineCodes.put("airasia", "AK");
        airlineCodes.put("ak", "AK");
        
        airlineCodes.put("scoot", "TR");
        airlineCodes.put("tr", "TR");
        
        airlineCodes.put("wizz air", "W6");
        airlineCodes.put("wizz", "W6");
        airlineCodes.put("w6", "W6");
        
        airlineCodes.put("vueling", "VY");
        airlineCodes.put("vy", "VY");
    }
    
    public QueryIntent parseQuery(String userInput) {
        String query = userInput.toLowerCase().trim();
        QueryIntent intent = new QueryIntent();
        
        // Check for greeting or help
        if (isGreeting(query)) {
            intent.setType(QueryType.GREETING);
            return intent;
        }
        
        if (isHelp(query)) {
            intent.setType(QueryType.HELP);
            return intent;
        }
        
        // Extract flight number if present
        Matcher flightMatcher = flightNumberPattern.matcher(userInput);
        if (flightMatcher.find()) {
            intent.setType(QueryType.FLIGHT_NUMBER);
            intent.setFlightNumber(flightMatcher.group(1).toUpperCase());
            intent.setLimit(extractLimit(query));
            return intent;
        }
        
        // Check for route queries (from X to Y)
        Matcher routeMatcher = routePattern.matcher(query);
        if (routeMatcher.find()) {
            String departure = routeMatcher.group(2).trim();
            String arrival = routeMatcher.group(4).trim();
            
            String depCode = resolveAirportCode(departure);
            String arrCode = resolveAirportCode(arrival);
            
            if (depCode != null && arrCode != null) {
                intent.setType(QueryType.ROUTE);
                intent.setDepartureAirport(depCode);
                intent.setArrivalAirport(arrCode);
                intent.setLimit(extractLimit(query));
                return intent;
            }
        }
        
        // Check for departure queries
        Matcher departureMatcher = departurePattern.matcher(query);
        if (departureMatcher.find()) {
            String departure = departureMatcher.group(2).trim();
            String depCode = resolveAirportCode(departure);
            
            if (depCode != null) {
                intent.setType(QueryType.DEPARTURE);
                intent.setDepartureAirport(depCode);
                intent.setLimit(extractLimit(query));
                return intent;
            }
        }
        
        // Check for arrival queries
        Matcher arrivalMatcher = arrivalPattern.matcher(query);
        if (arrivalMatcher.find()) {
            String arrival = arrivalMatcher.group(2).trim();
            String arrCode = resolveAirportCode(arrival);
            
            if (arrCode != null) {
                intent.setType(QueryType.ARRIVAL);
                intent.setArrivalAirport(arrCode);
                intent.setLimit(extractLimit(query));
                return intent;
            }
        }
        
        // Check for airline queries
        Matcher airlineMatcher = airlinePattern.matcher(query);
        if (airlineMatcher.find()) {
            String airline = airlineMatcher.group(2).trim();
            String airlineCode = resolveAirlineCode(airline);
            
            if (airlineCode != null) {
                intent.setType(QueryType.AIRLINE);
                intent.setAirline(airlineCode);
                intent.setLimit(extractLimit(query));
                return intent;
            }
        }
        
        // Check for general flight queries
        if (query.contains("flight") || query.contains("plane") || query.contains("aircraft")) {
            intent.setType(QueryType.ACTIVE_FLIGHTS);
            intent.setLimit(extractLimit(query));
            return intent;
        }
        
        // Default to unknown
        intent.setType(QueryType.UNKNOWN);
        return intent;
    }
    
    private boolean isGreeting(String query) {
        return query.matches(".*\\b(hello|hi|hey|good morning|good afternoon|good evening|greetings)\\b.*");
    }
    
    private boolean isHelp(String query) {
        return query.matches(".*\\b(help|what can you do|how does this work|commands|options)\\b.*");
    }
    
    private int extractLimit(String query) {
        Pattern limitPattern = Pattern.compile("\\b(\\d+)\\s+(flight|result)s?\\b");
        Matcher matcher = limitPattern.matcher(query);
        if (matcher.find()) {
            int limit = Integer.parseInt(matcher.group(1));
            return Math.min(limit, 20); // Cap at 20
        }
        
        if (query.contains("few")) return 3;
        if (query.contains("several")) return 5;
        if (query.contains("many") || query.contains("all")) return 15;
        
        return 5; // Default
    }
    
    private String resolveAirportCode(String input) {
        String normalized = input.toLowerCase().trim();
        
        // Direct lookup
        if (airportCodes.containsKey(normalized)) {
            return airportCodes.get(normalized);
        }
        
        // Check if it's already a code
        if (normalized.length() == 3 && normalized.matches("[a-z]{3}")) {
            return normalized.toUpperCase();
        }
        
        // Partial matching
        for (Map.Entry<String, String> entry : airportCodes.entrySet()) {
            if (entry.getKey().contains(normalized) || normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    private String resolveAirlineCode(String input) {
        String normalized = input.toLowerCase().trim();
        
        // Direct lookup
        if (airlineCodes.containsKey(normalized)) {
            return airlineCodes.get(normalized);
        }
        
        // Check if it's already a code
        if (normalized.length() >= 2 && normalized.length() <= 3 && normalized.matches("[a-z0-9]+")) {
            return normalized.toUpperCase();
        }
        
        return null;
    }
    
    public enum QueryType {
        GREETING,
        HELP,
        FLIGHT_NUMBER,
        ROUTE,
        DEPARTURE,
        ARRIVAL,
        AIRLINE,
        ACTIVE_FLIGHTS,
        UNKNOWN
    }
    
    public static class QueryIntent {
        private QueryType type;
        private String flightNumber;
        private String departureAirport;
        private String arrivalAirport;
        private String airline;
        private int limit = 5;
        
        // Getters and setters
        public QueryType getType() { return type; }
        public void setType(QueryType type) { this.type = type; }
        
        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
        
        public String getDepartureAirport() { return departureAirport; }
        public void setDepartureAirport(String departureAirport) { this.departureAirport = departureAirport; }
        
        public String getArrivalAirport() { return arrivalAirport; }
        public void setArrivalAirport(String arrivalAirport) { this.arrivalAirport = arrivalAirport; }
        
        public String getAirline() { return airline; }
        public void setAirline(String airline) { this.airline = airline; }
        
        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }
    }
}
