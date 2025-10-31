# Setup Guide Documentation

## 🚀 **Complete Setup Instructions**

This guide provides step-by-step instructions for setting up the Kids Vehicle Tracking Application on your local development environment.

---

## 📋 **Prerequisites**

### **System Requirements**
- **Operating System**: Windows 10/11, macOS 10.15+, or Ubuntu 18.04+
- **RAM**: Minimum 8GB, Recommended 16GB
- **Storage**: Minimum 10GB free space
- **Internet**: Stable internet connection for downloads

### **Software Requirements**
- **Java**: JDK 17 or higher
- **Node.js**: Node.js 16+ (for Flutter)
- **Flutter**: Flutter 3.x
- **MySQL**: MySQL 8.0
- **IDE**: IntelliJ IDEA, VS Code, or Android Studio
- **Git**: Git for version control

---

## 🔧 **Backend Setup (Spring Boot)**

### **Step 1: Install Java Development Kit (JDK)**
```bash
# Download and install JDK 17 from Oracle or OpenJDK
# Verify installation
java -version
javac -version
```

### **Step 2: Install MySQL Database**
```bash
# Download and install MySQL 8.0
# Start MySQL service
sudo systemctl start mysql  # Linux
brew services start mysql   # macOS
net start mysql            # Windows

# Create database
mysql -u root -p
CREATE DATABASE Kids_Vehicle_tracking_Db;
USE Kids_Vehicle_tracking_Db;
```

### **Step 3: Clone and Setup Backend**
```bash
# Clone the repository
git clone <repository-url>
cd Kids-Vehicle-Tracking_Application

# Verify Maven installation
mvn -version

# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

### **Step 4: Database Configuration**
```properties
# Update src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/Kids_Vehicle_tracking_Db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
app.jwt.secret=mySecretKey
app.jwt.expiration=86400000

# Email Configuration (Optional)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### **Step 5: Initialize Database**
```sql
-- Run the following SQL scripts in order:
-- 1. Create database schema
source database/schema.sql

-- 2. Insert initial data
source database/data.sql

-- 3. Apply any migrations
source database/migration/*.sql
```

### **Step 6: Verify Backend Setup**
```bash
# Check if application is running
curl http://localhost:9001/api/auth/login

# Expected response: {"success":false,"message":"Method not allowed"}
# This confirms the server is running
```

---

## 📱 **Frontend Setup (Flutter)**

### **Step 1: Install Flutter**
```bash
# Download Flutter SDK
# Extract to desired location (e.g., C:\flutter)

# Add Flutter to PATH
export PATH="$PATH:/path/to/flutter/bin"

# Verify installation
flutter doctor
```

### **Step 2: Install Flutter Dependencies**
```bash
# Install required dependencies
flutter pub get

# Verify dependencies
flutter pub deps
```

### **Step 3: Configure Flutter App**
```dart
// Update lib/config/app_config.dart
class AppConfig {
  static const String baseUrl = 'http://localhost:9001/api';
  static const String wsUrl = 'ws://localhost:9001/ws';
}
```

### **Step 4: Platform-Specific Setup**

#### **Android Setup**
```bash
# Install Android Studio
# Install Android SDK
# Accept Android licenses
flutter doctor --android-licenses

# Create Android emulator or connect device
flutter devices
```

#### **iOS Setup (macOS only)**
```bash
# Install Xcode
# Install iOS Simulator
# Accept Xcode licenses
sudo xcodebuild -license accept

# Install CocoaPods
sudo gem install cocoapods
```

### **Step 5: Run Flutter App**
```bash
# Run on connected device/emulator
flutter run

# Run on specific platform
flutter run -d android
flutter run -d ios
flutter run -d web
```

---

## 🗄️ **Database Setup**

### **Step 1: Create Database**
```sql
-- Connect to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE Kids_Vehicle_tracking_Db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Use database
USE Kids_Vehicle_tracking_Db;
```

### **Step 2: Run Schema Scripts**
```sql
-- Run schema creation
source database/schema.sql

-- Run initial data
source database/data.sql

-- Run any migration scripts
source database/migration/*.sql
```

### **Step 3: Verify Database Setup**
```sql
-- Check tables
SHOW TABLES;

-- Check initial data
SELECT * FROM roles;
SELECT * FROM users WHERE user_name = 'appadmin';
```

---

## 🔐 **Authentication Setup**

### **Step 1: Default Admin Account**
```sql
-- Default AppAdmin credentials
Username: appadmin
Password: admin123
Email: appadmin@example.com
Mobile: 9876543210
```

### **Step 2: Test Authentication**
```bash
# Test login API
curl -X POST http://localhost:9001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginId":"appadmin","password":"admin123"}'
```

### **Step 3: JWT Token Configuration**
```properties
# Update JWT secret in application.properties
app.jwt.secret=your-secret-key-here
app.jwt.expiration=86400000
```

---

## 🌐 **WebSocket Setup**

### **Step 1: WebSocket Configuration**
```java
// Verify WebSocketConfig.java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
```

### **Step 2: Test WebSocket Connection**
```javascript
// Test WebSocket connection
const socket = new SockJS('http://localhost:9001/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
});
```

---

## 📧 **Email Configuration (Optional)**

### **Step 1: Gmail Setup**
```properties
# Update email configuration in application.properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### **Step 2: Generate App Password**
1. Go to Google Account settings
2. Enable 2-factor authentication
3. Generate app password for the application
4. Use app password in configuration

### **Step 3: Test Email**
```bash
# Test email functionality
curl -X POST http://localhost:9001/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"loginId":"appadmin"}'
```

---

## 🗺️ **Google Maps Setup (Optional)**

### **Step 1: Get Google Maps API Key**
1. Go to Google Cloud Console
2. Create new project or select existing
3. Enable Maps SDK for Android/iOS
4. Create API key
5. Restrict API key to your app

### **Step 2: Configure Flutter App**
```yaml
# Update android/app/src/main/AndroidManifest.xml
<manifest>
  <application>
    <meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="YOUR_API_KEY_HERE"/>
  </application>
</manifest>
```

### **Step 3: Update Flutter Code**
```dart
// Update lib/config/app_config.dart
class AppConfig {
  static const String googleMapsApiKey = 'YOUR_API_KEY_HERE';
}
```

---

## 🧪 **Testing Setup**

### **Step 1: Backend Testing**
```bash
# Run backend tests
mvn test

# Run specific test class
mvn test -Dtest=DriverServiceImplTest

# Run integration tests
mvn verify
```

### **Step 2: Frontend Testing**
```bash
# Run Flutter tests
flutter test

# Run specific test file
flutter test test/widget_test.dart

# Run integration tests
flutter drive --target=test_driver/app.dart
```

### **Step 3: API Testing**
```bash
# Test API endpoints
curl -X GET http://localhost:9001/api/drivers/1/dashboard \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🚀 **Production Setup**

### **Step 1: Production Database**
```sql
-- Create production database
CREATE DATABASE Kids_Vehicle_tracking_Db_Prod CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Create production user
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON Kids_Vehicle_tracking_Db_Prod.* TO 'app_user'@'localhost';
FLUSH PRIVILEGES;
```

### **Step 2: Production Configuration**
```properties
# Update application-prod.properties
spring.datasource.url=jdbc:mysql://localhost:3306/Kids_Vehicle_tracking_Db_Prod
spring.datasource.username=app_user
spring.datasource.password=secure_password

# Production JWT secret
app.jwt.secret=production-secret-key

# Production email configuration
spring.mail.host=smtp.gmail.com
spring.mail.username=production-email@gmail.com
spring.mail.password=production-app-password
```

### **Step 3: Build for Production**
```bash
# Build backend
mvn clean package -Pprod

# Build Flutter app
flutter build apk --release
flutter build ios --release
flutter build web --release
```

---

## 🔧 **Troubleshooting**

### **Common Issues**

#### **Backend Issues**
```bash
# Port already in use
netstat -ano | findstr :9001
taskkill /PID <PID> /F

# Database connection issues
# Check MySQL service
sudo systemctl status mysql

# Check database credentials
mysql -u root -p
```

#### **Frontend Issues**
```bash
# Flutter doctor issues
flutter doctor -v

# Clean and rebuild
flutter clean
flutter pub get
flutter run

# Android build issues
flutter clean
cd android
./gradlew clean
cd ..
flutter run
```

#### **Database Issues**
```sql
-- Check database status
SHOW PROCESSLIST;
SHOW STATUS;

-- Reset database
DROP DATABASE Kids_Vehicle_tracking_Db;
CREATE DATABASE Kids_Vehicle_tracking_Db;
```

### **Performance Issues**
```bash
# Check system resources
top
htop
df -h

# Check database performance
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Threads_connected';
```

---

## 📊 **Verification Checklist**

### **Backend Verification**
- [ ] Java 17 installed and configured
- [ ] MySQL 8.0 running and accessible
- [ ] Spring Boot application starts successfully
- [ ] Database tables created
- [ ] Initial data loaded
- [ ] API endpoints responding
- [ ] JWT authentication working
- [ ] WebSocket connection established

### **Frontend Verification**
- [ ] Flutter SDK installed
- [ ] Dependencies installed
- [ ] App builds successfully
- [ ] App runs on device/emulator
- [ ] Login functionality working
- [ ] Navigation working
- [ ] API calls successful
- [ ] WebSocket connection working

### **Database Verification**
- [ ] Database created
- [ ] Tables created
- [ ] Initial data loaded
- [ ] Foreign key constraints working
- [ ] Indexes created
- [ ] User can connect
- [ ] Queries executing successfully

### **Integration Verification**
- [ ] Frontend-backend communication
- [ ] Authentication flow
- [ ] Real-time notifications
- [ ] File upload functionality
- [ ] Location services
- [ ] Email functionality

---

## 📞 **Support & Help**

### **Documentation Resources**
- **API Documentation**: `/04-API-DOCUMENTATION.md`
- **Database Schema**: `/03-DATABASE-SCHEMA.md`
- **Architecture**: `/02-ARCHITECTURE-DIAGRAM.md`
- **Features**: `/07-FEATURES-FUNCTIONALITY.md`

### **Common Commands**
```bash
# Backend
mvn clean install
mvn spring-boot:run
mvn test

# Frontend
flutter clean
flutter pub get
flutter run
flutter test

# Database
mysql -u root -p
source database/schema.sql
```

### **Log Files**
- **Backend Logs**: `logs/application.log`
- **Flutter Logs**: `flutter logs`
- **Database Logs**: MySQL error log

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Status**: Production Ready  
**Setup Time**: 30-60 minutes  
**Difficulty**: Intermediate  
**Prerequisites**: Basic knowledge of Java, Flutter, and MySQL
