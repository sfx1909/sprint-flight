# 🔒 Sprint Flight Security Guide

## 🚨 **IMPORTANT SECURITY NOTICE**

**This application now uses environment variables to protect your API keys!**
Never commit your actual API keys to version control.

---

## 🔐 **Secure Setup Instructions**

### **Method 1: Using .env file (Recommended for Development)**

1. **Copy the example environment file:**
   ```bash
   cp .env.example .env
   ```

2. **Edit the `.env` file with your actual API keys:**
   ```bash
   # Sprint Flight Application Environment Variables
   FLIGHT_API_KEY=your_actual_aviationstack_api_key_here
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   
   # Optional configurations
   FLIGHT_API_BASE_URL=http://api.aviationstack.com/v1
   GEMINI_MODEL=gemini-2.0-flash
   SERVER_PORT=8080
   ```

3. **NEVER commit the `.env` file to git** - it's already in `.gitignore`

### **Method 2: Using System Environment Variables (Recommended for Production)**

```bash
export FLIGHT_API_KEY="your_actual_aviationstack_api_key_here"
export GEMINI_API_KEY="your_actual_gemini_api_key_here"
```

### **Method 3: Using Railway Environment Variables**

Set environment variables in Railway dashboard:
1. Go to your Railway project dashboard
2. Navigate to Variables section  
3. Add `FLIGHT_API_KEY` and `GEMINI_API_KEY`
4. Redeploy with `railway up`

---

## 🛡️ **Security Features Implemented**

### ✅ **Environment Variable Protection**
- All API keys loaded from environment variables
- No hardcoded keys in source code
- Placeholder detection and warnings

### ✅ **Secure Logging**
- API keys automatically redacted from logs
- Sensitive HTTP request details suppressed
- Rolling log files with size limits

### ✅ **Configuration Validation**
- Startup validation for required API keys
- Clear error messages for missing configuration
- Graceful fallback for optional services

### ✅ **Git Security**
- `.env` files excluded from version control
- Sensitive configuration files ignored
- Log files excluded from commits

---

## 🔧 **Getting Your API Keys**

### **AviationStack API Key (Required)**
1. Visit [AviationStack.com](https://aviationstack.com/)
2. Sign up for a free account
3. Get your API access key from the dashboard
4. Add it to your environment as `FLIGHT_API_KEY`

### **Gemini API Key (Optional)**
1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with Google account
3. Create a new API key
4. Add it to your environment as `GEMINI_API_KEY`

---

## 🚀 **Running the Application**

### **Development (with .env file):**
```bash
# 1. Set up your .env file first
cp .env.example .env
# Edit .env with your actual API keys

# 2. Build and run
./gradlew build
java -jar build/libs/sprint-flight-0.0.1-SNAPSHOT.jar
```

### **Production (with environment variables):**
```bash
export FLIGHT_API_KEY="your_key_here"
export GEMINI_API_KEY="your_key_here"
java -jar sprint-flight.jar
```

---

## ⚠️ **Security Warnings**

### **🚫 DON'T DO THIS:**
- ❌ Commit API keys to git
- ❌ Share .env files
- ❌ Use production keys in development
- ❌ Log API keys in console output
- ❌ Include keys in screenshots or documentation

### **✅ DO THIS:**
- ✅ Use environment variables
- ✅ Rotate API keys regularly  
- ✅ Use separate keys for dev/prod
- ✅ Monitor API key usage
- ✅ Revoke compromised keys immediately

---

## 🔍 **Troubleshooting**

### **"API key not properly configured" Error**
- Check that `FLIGHT_API_KEY` environment variable is set
- Verify the key doesn't contain placeholder text
- Ensure no extra spaces or quotes around the key

### **"Gemini API key using placeholder" Warning**
- This is normal if you haven't set up Gemini AI
- The app works with flight data only
- Set `GEMINI_API_KEY` for enhanced AI features

### **Environment Variables Not Loading**
- Ensure `.env` file is in the project root directory
- Check file permissions (should be readable)
- Try setting variables manually: `export FLIGHT_API_KEY="your_key"`

---

## 📊 **Security Monitoring**

The application logs security events:
- ✅ Successful API key validation
- ⚠️ Missing or invalid API keys  
- 🔒 Sensitive data redaction in logs
- 🛡️ Environment configuration status

Check logs at: `logs/sprint-flight.log`
