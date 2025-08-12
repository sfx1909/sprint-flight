# 🛩️ Sprint Flight - Spring Boot Flight API Integration

A Spring Boot application that integrates with flight APIs to retrieve real-time flight information.

## 🚀 Features

- **Real-time Flight Data**: Get current flight information from AviationStack API
- **Multiple Search Options**: Search by airline, airport, route, or flight number
- **RESTful API**: Clean REST endpoints for easy integration
- **Web Interface**: Built-in test interface to try all endpoints
- **Error Handling**: Comprehensive error handling and logging
- **Rate Limiting**: Built-in protections against API rate limits

## 📋 Prerequisites

- Java 24 or higher
- Spring Boot 3.5.4
- AviationStack API key (free tier available)

## 🔧 Setup

### 1. Get API Key
1. Visit [AviationStack](https://aviationstack.com/)
2. Sign up for a free account
3. Get your API access key

### 2. Configure Application
Update `src/main/resources/application.properties` with your API key:
```properties
flight.api.key=YOUR_ACTUAL_API_KEY_HERE
```

### 3. Build and Run
```bash
# Using Gradle wrapper
./gradlew bootRun

# Or build JAR and run
./gradlew build
java -jar build/libs/sprint-flight-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## 🔍 API Endpoints

### Base URL: `http://localhost:8080/api/flights`

| Endpoint | Method | Description | Example |
|----------|--------|-------------|---------|
| `/` | GET | API information | `/api/flights/` |
| `/airline/{code}` | GET | Flights by airline IATA code | `/api/flights/airline/AA?limit=10` |
| `/departure/{code}` | GET | Flights from departure airport | `/api/flights/departure/JFK?limit=10` |
| `/arrival/{code}` | GET | Flights to arrival airport | `/api/flights/arrival/LAX?limit=10` |
| `/route/{from}/{to}` | GET | Flights by route | `/api/flights/route/JFK/LAX?limit=5` |
| `/number/{flight}` | GET | Flight by number | `/api/flights/number/AA100?limit=3` |
| `/active` | GET | All active flights | `/api/flights/active?limit=20` |
| `/health` | GET | Health check | `/api/flights/health` |

### Query Parameters
- `limit`: Number of results to return (default: varies by endpoint, max: 50-100)

## 🖥️ Web Interface

Visit `http://localhost:8080` to access the built-in web interface for testing all API endpoints.

## 📝 Example API Calls

### Get Flights by Airline
```bash
curl "http://localhost:8080/api/flights/airline/AA?limit=5"
```

### Get Flights by Route
```bash
curl "http://localhost:8080/api/flights/route/JFK/LAX?limit=3"
```

### Get Flight by Number
```bash
curl "http://localhost:8080/api/flights/number/AA100?limit=1"
```

## 📊 Response Format

All endpoints return JSON with the following structure:
```json
{
  "count": 2,
  "flights": [
    {
      "flight_date": "2025-08-12",
      "flight_status": "active",
      "departure": {
        "airport": "John F Kennedy International Airport",
        "timezone": "America/New_York",
        "iata": "JFK",
        "icao": "KJFK",
        "terminal": "4",
        "gate": "A1",
        "scheduled": "2025-08-12T14:30:00+00:00"
      },
      "arrival": {
        "airport": "Los Angeles International Airport",
        "timezone": "America/Los_Angeles",
        "iata": "LAX",
        "icao": "KLAX",
        "terminal": "4",
        "scheduled": "2025-08-12T17:45:00+00:00"
      },
      "airline": {
        "name": "American Airlines",
        "iata": "AA",
        "icao": "AAL"
      },
      "flight": {
        "number": "100",
        "iata": "AA100",
        "icao": "AAL100"
      }
    }
  ]
}
```

## ⚠️ Important Notes

### API Rate Limits
- **Free Tier**: 1,000 requests/month
- **Paid Plans**: Higher limits available
- The application includes built-in rate limiting protections

### Common IATA Codes
**Airlines**: AA (American), DL (Delta), UA (United), BA (British Airways)
**Airports**: JFK (New York), LAX (Los Angeles), ORD (Chicago), LHR (London)

## 🔧 Configuration Options

In `application.properties`:
```properties
# Server configuration
server.port=8080

# Flight API Configuration
flight.api.base-url=http://api.aviationstack.com/v1
flight.api.key=YOUR_API_KEY_HERE
flight.api.timeout=10000

# Logging
logging.level.github.sfx.sprint_flight=DEBUG
```

## 🏗️ Architecture

- **Controller Layer**: REST endpoints (`FlightController`)
- **Service Layer**: Business logic and API integration (`FlightService`)
- **Model Layer**: Data models for flight information
- **Configuration**: WebClient setup for HTTP calls

## 🧪 Testing

The application includes:
- Built-in web interface for manual testing
- Comprehensive error handling
- Logging for debugging
- Health check endpoint

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Troubleshooting

### Common Issues

1. **API Key Error (401)**: Check your API key in `application.properties`
2. **Rate Limit (429)**: Wait before making more requests or upgrade your plan
3. **Connection Timeout**: Check your internet connection and API availability
4. **No Data Returned**: Try different IATA codes or check if flights are available

### Support

- Check the logs for detailed error information
- Verify your API key is active at AviationStack
- Ensure you're using valid IATA codes