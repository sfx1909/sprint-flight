#!/bin/bash
# Quick deployment helpers for Sprint Flight

set -e

echo "🚀 Sprint Flight Quick Deploy Helper"
echo "===================================="

# Function to check if .env file exists
check_env_file() {
    if [ ! -f ".env" ]; then
        echo "❌ ERROR: .env file not found!"
        echo ""
        echo "📝 Create your .env file first:"
        echo "   cp .env.example .env"
        echo "   nano .env  # Add your actual API keys"
        echo ""
        exit 1
    fi
}

# Function to validate API keys in .env
validate_api_keys() {
    if grep -q "your_actual" .env || grep -q "YOUR_" .env; then
        echo "⚠️  WARNING: Placeholder API keys detected in .env file!"
        echo "   Please update with your actual API keys before deployment."
        echo ""
        read -p "Continue anyway? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
}

# Deploy functions
deploy_docker() {
    echo "🐳 Docker Deployment"
    echo "-------------------"
    check_env_file
    validate_api_keys
    
    echo "📦 Building Docker image..."
    docker build -t sprint-flight:latest .
    
    echo "🚀 Running container..."
    docker run -d \
        --name sprint-flight \
        -p 8080:8080 \
        --env-file .env \
        --restart unless-stopped \
        sprint-flight:latest
    
    echo "✅ Container started! Check: docker logs sprint-flight"
    echo "🌐 Access: http://localhost:8080"
}

deploy_docker_compose() {
    echo "🐳 Docker Compose Deployment"
    echo "----------------------------"
    check_env_file
    validate_api_keys
    
    echo "🚀 Starting with Docker Compose..."
    docker-compose up -d --build
    
    echo "✅ Services started!"
    echo "📋 Check status: docker-compose ps"
    echo "📊 View logs: docker-compose logs -f"
    echo "🌐 Access: http://localhost:8080"
}

deploy_heroku() {
    echo "☁️  Heroku Deployment"
    echo "--------------------"
    
    if ! command -v heroku &> /dev/null; then
        echo "❌ Heroku CLI not installed. Visit: https://devcenter.heroku.com/articles/heroku-cli"
        exit 1
    fi
    
    read -p "Enter your Heroku app name: " APP_NAME
    
    echo "🔧 Setting up Heroku app..."
    heroku create $APP_NAME || heroku git:remote -a $APP_NAME
    
    echo "🔐 Setting environment variables..."
    if [ -f ".env" ]; then
        while IFS= read -r line; do
            if [[ $line == *"="* ]] && [[ $line != "#"* ]]; then
                heroku config:set "$line"
            fi
        done < .env
    else
        echo "⚠️  No .env file found. You'll need to set environment variables manually:"
        echo "   heroku config:set FLIGHT_API_KEY=your_key"
        echo "   heroku config:set GEMINI_API_KEY=your_key"
    fi
    
    echo "🚀 Deploying to Heroku..."
    git add -A
    git commit -m "Deploy to Heroku" || true
    git push heroku HEAD:main
    
    echo "✅ Deployed to Heroku!"
    echo "🌐 Access: https://$APP_NAME.herokuapp.com"
}

deploy_railway() {
    echo "🚂 Railway Deployment"
    echo "--------------------"
    
    if ! command -v railway &> /dev/null; then
        echo "❌ Railway CLI not installed. Run: npm install -g @railway/cli"
        exit 1
    fi
    
    echo "🔧 Initializing Railway project..."
    railway login
    railway init
    
    echo "🔐 Setting environment variables..."
    if [ -f ".env" ]; then
        while IFS= read -r line; do
            if [[ $line == *"="* ]] && [[ $line != "#"* ]]; then
                KEY=$(echo "$line" | cut -d'=' -f1)
                VALUE=$(echo "$line" | cut -d'=' -f2-)
                railway variables:set "$KEY=$VALUE"
            fi
        done < .env
    fi
    
    echo "🚀 Deploying to Railway..."
    railway up
    
    echo "✅ Deployed to Railway!"
}

build_jar() {
    echo "📦 Building JAR File"
    echo "-------------------"
    
    echo "🔨 Building with Gradle..."
    ./gradlew clean build -x test
    
    echo "✅ JAR file built: build/libs/sprint-flight-0.0.1-SNAPSHOT.jar"
    echo ""
    echo "🚀 Run locally with:"
    echo "   java -jar build/libs/sprint-flight-0.0.1-SNAPSHOT.jar"
    echo ""
    echo "💡 For production, use the deploy.sh script for systemd service"
}

# Main menu
case "${1:-}" in
    "docker")
        deploy_docker
        ;;
    "compose")
        deploy_docker_compose
        ;;
    "heroku")
        deploy_heroku
        ;;
    "railway")
        deploy_railway
        ;;
    "jar")
        build_jar
        ;;
    *)
        echo ""
        echo "Choose deployment method:"
        echo "  ./quick-deploy.sh docker    - Deploy with Docker"
        echo "  ./quick-deploy.sh compose   - Deploy with Docker Compose"
        echo "  ./quick-deploy.sh heroku    - Deploy to Heroku"
        echo "  ./quick-deploy.sh railway   - Deploy to Railway"
        echo "  ./quick-deploy.sh jar       - Build JAR for manual deployment"
        echo ""
        echo "📖 For more options, see DEPLOYMENT.md"
        ;;
esac
