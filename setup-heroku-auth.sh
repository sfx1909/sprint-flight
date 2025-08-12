#!/bin/bash
# Heroku API Key Authentication Helper

echo "🔑 Heroku API Key Authentication Setup"
echo "====================================="
echo ""
echo "📋 Steps to get your Heroku API key:"
echo "1. Go to: https://dashboard.heroku.com/account"
echo "2. Login through your web browser"
echo "3. Scroll to 'API Key' section"
echo "4. Click 'Reveal' to show your API key"
echo "5. Copy the API key"
echo ""

read -p "📝 Enter your Heroku API key: " HEROKU_API_KEY

if [ -z "$HEROKU_API_KEY" ]; then
    echo "❌ No API key provided. Exiting."
    exit 1
fi

echo ""
echo "🔧 Setting up authentication..."

# Method 1: Environment variable (temporary for this session)
export HEROKU_API_KEY="$HEROKU_API_KEY"
echo "✅ Environment variable set for this session"

# Method 2: Add to .bashrc for permanent setup (optional)
read -p "🔄 Make this permanent by adding to ~/.bashrc? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "export HEROKU_API_KEY=\"$HEROKU_API_KEY\"" >> ~/.bashrc
    echo "✅ Added to ~/.bashrc (will take effect in new terminals)"
fi

echo ""
echo "🧪 Testing authentication..."

# Test the authentication
if heroku auth:whoami 2>/dev/null; then
    echo "✅ Successfully authenticated with Heroku!"
    echo ""
    echo "🚀 You can now deploy with:"
    echo "   heroku create your-app-name"
    echo "   git push heroku main"
    echo ""
    echo "Or use the quick deploy script:"
    echo "   ./quick-deploy.sh heroku"
else
    echo "❌ Authentication test failed. Please check your API key."
    echo ""
    echo "💡 Troubleshooting:"
    echo "   - Verify the API key is correct"
    echo "   - Make sure you copied it completely"
    echo "   - Check that MFA is enabled on your Heroku account"
fi
