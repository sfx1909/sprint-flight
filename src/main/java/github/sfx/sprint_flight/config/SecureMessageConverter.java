package github.sfx.sprint_flight.config;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Custom log message converter that sanitizes sensitive information
 * from log messages to prevent API key leakage.
 */
public class SecureMessageConverter extends MessageConverter {

    private static final String[] SENSITIVE_PATTERNS = {
        "AIzaSy[A-Za-z0-9_-]{33}",  // Google API key pattern
        "access_key=[A-Za-z0-9]+",   // AviationStack API key
        "key=[A-Za-z0-9_-]+",       // Generic API key
        "api[_-]?key[\"'`]?[\\s]*[:=][\\s]*[\"'`]?[A-Za-z0-9_-]+", // Various API key formats
    };
    
    @Override
    public String convert(ILoggingEvent event) {
        String message = super.convert(event);
        return sanitizeMessage(message);
    }
    
    private String sanitizeMessage(String message) {
        if (message == null) return null;
        
        String sanitized = message;
        for (String pattern : SENSITIVE_PATTERNS) {
            sanitized = sanitized.replaceAll(pattern, "[REDACTED_API_KEY]");
        }
        
        return sanitized;
    }
}
