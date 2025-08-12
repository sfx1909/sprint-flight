package github.sfx.sprint_flight.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Secure environment configuration that loads .env files
 * while maintaining security best practices.
 */
public class EnvironmentConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        // Load .env file if it exists (for local development)
        loadDotEnvFile(environment);
        
        // Validate that required API keys are not using placeholder values
        validateApiKeys(environment);
    }
    
    private void loadDotEnvFile(ConfigurableEnvironment environment) {
        try {
            Path envFile = Paths.get(".env");
            if (Files.exists(envFile)) {
                Map<String, Object> envVars = new HashMap<>();
                Files.lines(envFile)
                    .filter(line -> !line.trim().isEmpty() && !line.startsWith("#"))
                    .forEach(line -> {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            // Remove quotes if present
                            if (value.startsWith("\"") && value.endsWith("\"")) {
                                value = value.substring(1, value.length() - 1);
                            }
                            envVars.put(key, value);
                        }
                    });
                
                if (!envVars.isEmpty()) {
                    environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envVars));
                    System.out.println("✅ Loaded .env file with " + envVars.size() + " variables");
                }
            }
        } catch (IOException e) {
            System.out.println("ℹ️  No .env file found - using system environment variables");
        }
    }
    
    private void validateApiKeys(ConfigurableEnvironment environment) {
        String flightApiKey = environment.getProperty("FLIGHT_API_KEY", "");
        String geminiApiKey = environment.getProperty("GEMINI_API_KEY", "");
        
        // Check for placeholder values that indicate keys haven't been configured
        if (flightApiKey.contains("your_") || flightApiKey.contains("YOUR_") || flightApiKey.isEmpty()) {
            System.err.println("⚠️  WARNING: Flight API key not properly configured!");
            System.err.println("   Create a .env file or set FLIGHT_API_KEY environment variable");
        } else {
            System.out.println("✅ Flight API key configured");
        }
        
        if (geminiApiKey.contains("your_") || geminiApiKey.contains("YOUR_") || geminiApiKey.isEmpty()) {
            System.out.println("ℹ️  Gemini API key not configured - AI features will use fallback responses");
        } else {
            System.out.println("✅ Gemini API key configured");
        }
    }
}
