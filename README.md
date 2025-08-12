# 🛩️ Sprint Flight API

## 🚀 Quick Start

### 1. **Secure Setup** (Required)
```bash
# Copy environment template
cp .env.example .env

# Edit .env with your actual API keys (NEVER commit this file!)
nano .env
```

### 2. **Get API Key** (AviationStack - Required)
1. Visit [AviationStack.com](https://aviationstack.com/)
2. Sign up for free account  
3. Get your API access key
4. Add to `.env` file: `FLIGHT_API_KEY=your_actual_key_here`

````markdown
# 🛩️ Sprint Flight API

A secure Spring Boot application that integrates with flight APIs to retrieve real-time flight information with AI-powered natural language processing.

## 🔒 **Security First!**

**⚠️ IMPORTANT:** This application uses environment variables to protect your API keys. **Never commit API keys to version control!**

📖 **[Read the complete Security Guide →](SECURITY.md)**

## 🚀 **Deployment on Railway**

This application is designed for deployment on [Railway](https://railway.app/). The deployment is already configured with:

- ✅ Railway configuration (`railway.json`)
- ✅ Optimized build configuration for Railway
- ✅ Environment variable security
- ✅ Health check endpoint
- ✅ Automatic HTTPS and custom domains

**Live Application**: [https://sprint-flight-production.up.railway.app](https://sprint-flight-production.up.railway.app)

### Deploy to Railway:
```bash
# Install Railway CLI
npm install -g @railway/cli

# Login and deploy
railway login
railway up
```

### Required Environment Variables:
Set these in Railway's dashboard or CLI:
- `FLIGHT_API_KEY`: Your AviationStack API key
- `GEMINI_API_KEY`: Your Google Gemini API key (optional)

## 🔒 **Security First!**

**⚠️ IMPORTANT:** This application uses environment variables to protect your API keys. **Never commit API keys to version control!**

📖 **[Read the complete Security Guide →](SECURITY.md)**

## 🚀 Features

- **🤖 AI-Powered Conversations**: Natural language flight queries using Google Gemini 2.0 Flash
- **✈️ Real-time Flight Data**: Current flight information from AviationStack API
- **🔍 Intelligent Search**: Find flights by airline, airport, route, or natural language
- **🌐 Modern Web Interface**: Interactive chat assistant and testing tools
- **🔒 Secure Configuration**: Environment variables protect API keys
- **⚡ Production Ready**: Deployed on Railway with automatic HTTPS

## 🔧 API Setup

### 1. AviationStack API (Required)
1. Visit [AviationStack.com](https://aviationstack.com/)
2. Sign up for free account
3. Get your API access key

### 2. Google Gemini API (Optional - for AI features)
1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create API key for enhanced natural language processing

## 🔍 API Endpoints

### Base URL: `https://sprint-flight-production.up.railway.app/api`

| Endpoint | Method | Description | Example |
|----------|--------|-------------|---------|
| `/flights/` | GET | API information | `/api/flights/` |
| `/flights/airline/{code}` | GET | Flights by airline | `/api/flights/airline/AA?limit=10` |
| `/flights/departure/{code}` | GET | Flights from airport | `/api/flights/departure/JFK?limit=10` |
| `/flights/arrival/{code}` | GET | Flights to airport | `/api/flights/arrival/LAX?limit=10` |
| `/flights/route/{from}/{to}` | GET | Flights by route | `/api/flights/route/JFK/LAX?limit=5` |
| `/flights/active` | GET | All active flights | `/api/flights/active?limit=20` |
| `/conversation/query` | POST | AI conversation query | See AI Features section |
| `/flights/health` | GET | Health check | `/api/flights/health` |

### AI-Powered Natural Language Queries

Send natural language queries to `/api/conversation/query`:

```bash
curl -X POST https://sprint-flight-production.up.railway.app/api/conversation/query \
  -H "Content-Type: application/json" \
  -d '{
    "query": "Show me flights from London to New York",
    "conversationId": "unique-session-id"
  }'
```

**Example queries:**
- "Flights from JFK to LAX"
- "Emirates flights from Dubai"
- "British Airways departures from London"
- "Show me active flights"

## 🖥️ Web Interface

Visit **[https://sprint-flight-production.up.railway.app](https://sprint-flight-production.up.railway.app)** to access the interactive web interface featuring:

- 🤖 **AI Chat Assistant**: Ask about flights in natural language
- 🧪 **API Testing Tools**: Interactive endpoint testing
- 📱 **Responsive Design**: Works on all devices
- ⚡ **Real-time Results**: Instant flight data and AI responses

## 📝 Example API Calls

### Get Flights by Airline
```bash
curl "https://sprint-flight-production.up.railway.app/api/flights/airline/AA?limit=5"
```

### Get Flights by Route
```bash
curl "https://sprint-flight-production.up.railway.app/api/flights/route/JFK/LAX?limit=3"
```

### AI Natural Language Query
```bash
curl -X POST https://sprint-flight-production.up.railway.app/api/conversation/query \
  -H "Content-Type: application/json" \
  -d '{"query": "Show me Emirates flights from Dubai", "conversationId": "test-123"}'
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

## ⚠️ Important Notes

### API Rate Limits
- **AviationStack Free**: 1,000 requests/month
- **Google Gemini**: Generous free tier for AI features
- Application includes built-in protections and error handling

### Common IATA Codes
**Airlines**: AA (American), DL (Delta), UA (United), BA (British Airways), EK (Emirates)
**Airports**: JFK (New York), LAX (Los Angeles), LHR (London), DXB (Dubai)

## 🏗️ Architecture

- **🎯 Controller Layer**: REST endpoints (`FlightController`, `ConversationController`)
- **🧠 Service Layer**: Business logic, API integration, and AI processing
- **📊 Model Layer**: Flight data models and conversation handling
- **🔧 Configuration**: Secure environment setup and WebClient configuration
- **🤖 AI Integration**: Google Gemini 2.0 Flash for natural language processing

## 🔒 Security Features

✅ **Environment Variables**: API keys never hardcoded  
✅ **Secure Logging**: Automatic API key redaction in logs  
✅ **Railway Deployment**: HTTPS by default, secure environment variables  
✅ **Input Validation**: Comprehensive request validation and sanitization  

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

1. **API Key Error (401)**: Verify API key in Railway environment variables
2. **Rate Limit (429)**: Wait before making more requests or upgrade your plan
3. **Connection Issues**: Check Railway deployment status
4. **No Data Returned**: Try different IATA codes or check flight availability

### Support Resources

- **Railway Console**: Monitor deployment and logs
- **Health Check**: Visit `/api/flights/health` to verify service status
- **API Documentation**: Built into the web interface
- **Security Guide**: See `SECURITY.md` for security best practices
````