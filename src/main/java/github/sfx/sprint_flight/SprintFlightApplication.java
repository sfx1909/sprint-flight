package github.sfx.sprint_flight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SprintFlightApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprintFlightApplication.class, args);
		System.out.println("🛩️ Sprint Flight Application Started!");
		System.out.println("📡 API Base URL: http://localhost:8080/api/flights");
		System.out.println("🌐 Web Interface: http://localhost:8080");
		System.out.println("🏥 Health Check: http://localhost:8080/api/flights/health");
	}

}
