package github.sfx.sprint_flight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SprintFlightApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SprintFlightApplication.class, args);
		Environment env = context.getEnvironment();
		
		// Get the port and determine the base URL
		String port = env.getProperty("server.port", "8080");
		String baseUrl = determineBaseUrl(env, port);
		
		System.out.println("🛩️ Sprint Flight Application Started!");
		System.out.println("📡 API Base URL: " + baseUrl + "/api/flights");
		System.out.println("🌐 Web Interface: " + baseUrl);
		System.out.println("🏥 Health Check: " + baseUrl + "/api/flights/health");
	}
	
	private static String determineBaseUrl(Environment env, String port) {
		// Check for Railway environment
		String railwayUrl = env.getProperty("RAILWAY_PUBLIC_DOMAIN");
		if (railwayUrl != null && !railwayUrl.isEmpty()) {
			return "https://" + railwayUrl;
		}
		
		// Check for Heroku environment
		String herokuUrl = env.getProperty("HEROKU_APP_NAME");
		if (herokuUrl != null && !herokuUrl.isEmpty()) {
			return "https://" + herokuUrl + ".herokuapp.com";
		}
		
		// Check for custom domain
		String customDomain = env.getProperty("APP_DOMAIN");
		if (customDomain != null && !customDomain.isEmpty()) {
			return customDomain.startsWith("http") ? customDomain : "https://" + customDomain;
		}
		
		// Check if PORT is set (indicates cloud deployment)
		String deployPort = env.getProperty("PORT");
		if (deployPort != null && !port.equals("8080")) {
			return "https://your-deployed-app.com"; // Generic cloud message
		}
		
		// Default to localhost for local development
		return "http://localhost:" + port;
	}

}
