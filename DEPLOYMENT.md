# 🚀 Sprint Flight Deployment Guide

Your Spring Boot application is ready for deployment! Here are multiple deployment options with security best practices.

---

## 🐳 **1. Docker Deployment (Recommended)**

### **Create Dockerfile**
```dockerfile
FROM openjdk:24-jdk-slim

# Create app directory
WORKDIR /app

# Copy gradle files
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src/ src/

# Build the application
RUN chmod +x gradlew && ./gradlew build -x test

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/api/flights/health || exit 1

# Run application
CMD ["java", "-jar", "build/libs/sprint-flight-0.0.1-SNAPSHOT.jar"]
```

### **Docker Compose for Development**
```yaml
version: '3.8'
services:
  sprint-flight:
    build: .
    ports:
      - "8080:8080"
    environment:
      - FLIGHT_API_KEY=${FLIGHT_API_KEY}
      - GEMINI_API_KEY=${GEMINI_API_KEY}
      - SERVER_PORT=8080
    volumes:
      - ./logs:/app/logs
    restart: unless-stopped
```

### **Deploy with Docker**
```bash
# 1. Build the image
docker build -t sprint-flight:latest .

# 2. Run with environment variables
docker run -d \
  -p 8080:8080 \
  -e FLIGHT_API_KEY="your_actual_key" \
  -e GEMINI_API_KEY="your_actual_key" \
  --name sprint-flight \
  sprint-flight:latest

# 3. Check logs
docker logs sprint-flight

# 4. Access application
curl http://localhost:8080/api/flights/health
```

---

## ☁️ **2. Heroku Deployment**

### **Create Heroku Configuration Files**

**Procfile:**
```
web: java -Dserver.port=$PORT -jar build/libs/sprint-flight-0.0.1-SNAPSHOT.jar
```

**system.properties:**
```
java.runtime.version=24
```

### **Deploy to Heroku**
```bash
# 1. Install Heroku CLI
# Visit: https://devcenter.heroku.com/articles/heroku-cli

# 2. Login and create app
heroku login
heroku create sprint-flight-app

# 3. Set environment variables (SECURE!)
heroku config:set FLIGHT_API_KEY="your_actual_key"
heroku config:set GEMINI_API_KEY="your_actual_key"
heroku config:set JAVA_OPTS="-Xmx512m"

# 4. Deploy
git add .
git commit -m "Deploy to Heroku"
git push heroku main

# 5. Scale and check
heroku ps:scale web=1
heroku logs --tail
heroku open
```

---

## 🌐 **3. Railway Deployment**

### **railway.json**
```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS"
  },
  "deploy": {
    "startCommand": "java -jar build/libs/sprint-flight-0.0.1-SNAPSHOT.jar",
    "healthcheckPath": "/api/flights/health",
    "healthcheckTimeout": 100
  }
}
```

### **Deploy to Railway**
```bash
# 1. Install Railway CLI
npm install -g @railway/cli

# 2. Login and deploy
railway login
railway init
railway add

# 3. Set environment variables
railway variables:set FLIGHT_API_KEY="your_actual_key"
railway variables:set GEMINI_API_KEY="your_actual_key"

# 4. Deploy
git add .
git commit -m "Deploy to Railway"
git push origin main
railway up
```

---

## 🔵 **4. DigitalOcean App Platform**

### **Create .do/app.yaml**
```yaml
name: sprint-flight
services:
- name: web
  source_dir: /
  github:
    repo: your-username/sprint-flight
    branch: main
  run_command: java -jar build/libs/sprint-flight-0.0.1-SNAPSHOT.jar
  environment_slug: java
  instance_count: 1
  instance_size_slug: basic-xxs
  http_port: 8080
  health_check:
    http_path: /api/flights/health
  envs:
  - key: FLIGHT_API_KEY
    scope: RUN_TIME
    type: SECRET
  - key: GEMINI_API_KEY
    scope: RUN_TIME
    type: SECRET
```

---

## 🏠 **5. Self-Hosted VPS Deployment**

### **Production Deployment Script**
```bash
#!/bin/bash
# deploy.sh - Production deployment script

set -e

echo "🚀 Deploying Sprint Flight Application..."

# Variables
APP_NAME="sprint-flight"
APP_USER="springboot"
APP_HOME="/opt/sprint-flight"
JAR_FILE="sprint-flight-0.0.1-SNAPSHOT.jar"

# Create application user
sudo useradd -r -m -U -d $APP_HOME -s /bin/false $APP_USER || true

# Create directories
sudo mkdir -p $APP_HOME/{bin,logs,config}
sudo chown -R $APP_USER:$APP_USER $APP_HOME

# Copy application
sudo cp build/libs/$JAR_FILE $APP_HOME/bin/
sudo chown $APP_USER:$APP_USER $APP_HOME/bin/$JAR_FILE

# Create systemd service
sudo tee /etc/systemd/system/$APP_NAME.service > /dev/null <<EOF
[Unit]
Description=Sprint Flight Application
After=syslog.target network.target

[Service]
User=$APP_USER
Type=simple
WorkingDirectory=$APP_HOME
ExecStart=/usr/bin/java -jar $APP_HOME/bin/$JAR_FILE
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=$APP_NAME

# Security settings
NoNewPrivileges=yes
PrivateTmp=yes
ProtectSystem=strict
ProtectHome=yes
ReadWritePaths=$APP_HOME

# Environment variables (set these in /etc/environment or systemd override)
Environment="FLIGHT_API_KEY=your_actual_key"
Environment="GEMINI_API_KEY=your_actual_key"
Environment="SERVER_PORT=8080"

[Install]
WantedBy=multi-user.target
EOF

# Reload systemd and start service
sudo systemctl daemon-reload
sudo systemctl enable $APP_NAME
sudo systemctl start $APP_NAME

echo "✅ Deployment complete!"
echo "🔍 Check status: sudo systemctl status $APP_NAME"
echo "📋 View logs: sudo journalctl -u $APP_NAME -f"
echo "🌐 Access: http://your-server:8080"
```

---

## 🔧 **6. Kubernetes Deployment**

### **k8s-deployment.yaml**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: sprint-flight-secrets
type: Opaque
stringData:
  FLIGHT_API_KEY: "your_actual_key"
  GEMINI_API_KEY: "your_actual_key"

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sprint-flight
  labels:
    app: sprint-flight
spec:
  replicas: 2
  selector:
    matchLabels:
      app: sprint-flight
  template:
    metadata:
      labels:
        app: sprint-flight
    spec:
      containers:
      - name: sprint-flight
        image: sprint-flight:latest
        ports:
        - containerPort: 8080
        envFrom:
        - secretRef:
            name: sprint-flight-secrets
        livenessProbe:
          httpGet:
            path: /api/flights/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /api/flights/health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"

---
apiVersion: v1
kind: Service
metadata:
  name: sprint-flight-service
spec:
  selector:
    app: sprint-flight
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

---

## 📊 **7. Monitoring & Production Setup**

### **Add Actuator for Monitoring**
Add to `build.gradle`:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
```

### **Production Application Properties**
Create `application-prod.properties`:
```properties
# Production configuration
server.port=${PORT:8080}
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s

# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true

# Logging
logging.level.github.sfx.sprint_flight=INFO
logging.pattern.file=%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n
logging.file.name=logs/sprint-flight.log
```

---

## 🛡️ **Security Checklist for Production**

### **✅ Before Deploying:**
- [ ] API keys are in environment variables (not in code)
- [ ] `.env` file is NOT committed to git
- [ ] Logs don't expose sensitive data
- [ ] Health check endpoint works
- [ ] Application starts successfully locally

### **✅ After Deploying:**
- [ ] Environment variables are properly set
- [ ] Application responds to health checks
- [ ] Logs are clean (no API keys visible)
- [ ] SSL/TLS certificate configured (HTTPS)
- [ ] Firewall rules properly configured

---

## 🚀 **Quick Deploy Commands**

Choose your deployment method:

```bash
# Docker (Local)
docker build -t sprint-flight . && docker run -p 8080:8080 --env-file .env sprint-flight

# Heroku
heroku create your-app && git push heroku main

# Railway
railway init && railway up

# VPS
./deploy.sh
```

---

## 📞 **Need Help?**

- 🐳 **Docker Issues**: Check `docker logs sprint-flight`
- ☁️ **Cloud Platform Issues**: Check platform-specific logs
- 🔧 **Application Issues**: Check `/api/flights/health`
- 🔒 **Security Concerns**: Review `SECURITY.md`

Your application is now ready for production deployment! 🎉
