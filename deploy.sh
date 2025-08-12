#!/bin/bash
# Production deployment script for Sprint Flight Application

set -e  # Exit on any error

# Configuration
APP_NAME="sprint-flight"
APP_USER="springboot"
APP_HOME="/opt/sprint-flight"
JAR_FILE="sprint-flight-0.0.1-SNAPSHOT.jar"
SERVICE_FILE="/etc/systemd/system/$APP_NAME.service"

echo "🚀 Starting Sprint Flight deployment..."

# Check if running as root
if [ "$EUID" -ne 0 ]; then 
    echo "❌ Please run as root (use sudo)"
    exit 1
fi

# Build the application
echo "📦 Building application..."
./gradlew clean build -x test

# Create application user
echo "👤 Creating application user..."
if ! id "$APP_USER" &>/dev/null; then
    useradd -r -m -U -d $APP_HOME -s /bin/false $APP_USER
    echo "✅ User $APP_USER created"
else
    echo "ℹ️  User $APP_USER already exists"
fi

# Create directories
echo "📁 Creating directories..."
mkdir -p $APP_HOME/{bin,logs,config}
chown -R $APP_USER:$APP_USER $APP_HOME

# Copy application
echo "📋 Copying application files..."
cp build/libs/$JAR_FILE $APP_HOME/bin/
chown $APP_USER:$APP_USER $APP_HOME/bin/$JAR_FILE

# Create environment file (template)
echo "🔧 Creating environment configuration..."
cat > $APP_HOME/config/app.env << EOF
# Sprint Flight Environment Configuration
# SECURITY: Set your actual API keys here!

FLIGHT_API_KEY=your_actual_aviationstack_key_here
GEMINI_API_KEY=your_actual_gemini_key_here
SERVER_PORT=8080
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_APP=INFO
EOF

chown $APP_USER:$APP_USER $APP_HOME/config/app.env
chmod 600 $APP_HOME/config/app.env  # Secure permissions

# Create systemd service
echo "⚙️  Creating systemd service..."
cat > $SERVICE_FILE << EOF
[Unit]
Description=Sprint Flight Application - Secure Flight API Service
Documentation=https://github.com/sfx1909/sprint-flight
After=network.target
Wants=network-online.target

[Service]
Type=simple
User=$APP_USER
Group=$APP_USER
WorkingDirectory=$APP_HOME

# Java application
ExecStart=/usr/bin/java \\
    -server \\
    -XX:+UseContainerSupport \\
    -XX:MaxRAMPercentage=75.0 \\
    -Djava.security.egd=file:/dev/./urandom \\
    -Dspring.profiles.active=prod \\
    -jar $APP_HOME/bin/$JAR_FILE

# Load environment variables securely
EnvironmentFile=$APP_HOME/config/app.env

# Restart policy
Restart=always
RestartSec=10
KillSignal=SIGTERM
TimeoutStopSec=30

# Logging
StandardOutput=journal
StandardError=journal
SyslogIdentifier=$APP_NAME

# Security settings
NoNewPrivileges=yes
PrivateTmp=yes
ProtectSystem=strict
ProtectHome=yes
ReadWritePaths=$APP_HOME
CapabilityBoundingSet=
AmbientCapabilities=
SystemCallFilter=@system-service
SystemCallErrorNumber=EPERM

[Install]
WantedBy=multi-user.target
EOF

# Reload systemd
echo "🔄 Reloading systemd..."
systemctl daemon-reload

# Enable service
echo "✅ Enabling $APP_NAME service..."
systemctl enable $APP_NAME

# Start service
echo "🚀 Starting $APP_NAME service..."
if systemctl is-active --quiet $APP_NAME; then
    systemctl restart $APP_NAME
else
    systemctl start $APP_NAME
fi

# Wait for service to start
echo "⏳ Waiting for service to start..."
sleep 5

# Check service status
if systemctl is-active --quiet $APP_NAME; then
    echo ""
    echo "🎉 ✅ SUCCESS! Sprint Flight deployed successfully!"
    echo ""
    echo "📊 Service Status:"
    systemctl status $APP_NAME --no-pager -l
    echo ""
    echo "🔧 Management Commands:"
    echo "  • Check status: sudo systemctl status $APP_NAME"
    echo "  • View logs:    sudo journalctl -u $APP_NAME -f"
    echo "  • Restart:      sudo systemctl restart $APP_NAME"
    echo "  • Stop:         sudo systemctl stop $APP_NAME"
    echo ""
    echo "🌐 Access your application:"
    echo "  • Health Check: http://localhost:8080/api/flights/health"
    echo "  • Web UI:       http://localhost:8080"
    echo ""
    echo "⚠️  IMPORTANT SECURITY STEPS:"
    echo "  1. Edit API keys in: $APP_HOME/config/app.env"
    echo "  2. Restart service: sudo systemctl restart $APP_NAME"
    echo "  3. Set up SSL/TLS with nginx or Apache"
    echo "  4. Configure firewall rules"
    echo ""
else
    echo ""
    echo "❌ DEPLOYMENT FAILED!"
    echo ""
    echo "🔍 Check logs for errors:"
    echo "  sudo journalctl -u $APP_NAME -f"
    echo ""
    echo "🔧 Debug commands:"
    echo "  sudo systemctl status $APP_NAME"
    echo "  sudo journalctl -u $APP_NAME --no-pager -l"
    exit 1
fi
