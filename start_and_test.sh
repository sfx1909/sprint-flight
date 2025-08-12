#!/bin/bash

echo "🛩️ Starting Sprint Flight Application..."
cd /home/ivyboon/sandbox/sprint-flight
nohup java -jar build/libs/sprint-flight-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
APP_PID=$!
echo "Application started with PID: $APP_PID"

echo "Waiting for application to start..."
sleep 10

echo "Testing endpoints..."

echo "📡 Testing Health Check:"
curl -s http://localhost:8080/api/flights/health | head -5

echo -e "\n📋 Testing Welcome Endpoint:"
curl -s http://localhost:8080/api/flights/ | head -10

echo -e "\n📊 API Documentation is available at the endpoints"

echo -e "\n✅ Application is running!"
echo "🌐 Web Interface: http://localhost:8080"
echo "📡 API Base URL: http://localhost:8080/api/flights"
echo "🏥 Health Check: http://localhost:8080/api/flights/health"

echo -e "\n⚠️  To use the flight API features:"
echo "1. Sign up at https://aviationstack.com/ for a free API key"
echo "2. Edit src/main/resources/application.properties"
echo "3. Replace 'YOUR_API_KEY_HERE' with your actual API key"
echo "4. Restart the application"

echo -e "\n🛑 To stop the application: kill $APP_PID"
