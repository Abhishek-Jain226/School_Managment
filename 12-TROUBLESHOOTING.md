# Troubleshooting Guide Documentation

## 🚨 **Comprehensive Troubleshooting Guide**

This guide provides solutions to common issues encountered during development, deployment, and operation of the Kids Vehicle Tracking Application.

---

## 🔧 **Backend Issues**

### **Application Startup Issues**

#### **Issue: Application Won't Start**
**Symptoms:**
- Spring Boot application fails to start
- Port already in use error
- Database connection errors

**Solutions:**
```bash
# Check if port is already in use
netstat -ano | findstr :9001
# Windows
taskkill /PID <PID> /F

# Linux/macOS
lsof -ti:9001 | xargs kill -9

# Check Java version
java -version
# Should be Java 17 or higher

# Check Maven version
mvn -version

# Clean and rebuild
mvn clean install

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

#### **Issue: Database Connection Failed**
**Symptoms:**
- `HikariPool-1 - Connection is not available`
- `Communications link failure`
- `Access denied for user`

**Solutions:**
```bash
# Check MySQL service
sudo systemctl status mysql
# Start MySQL if not running
sudo systemctl start mysql

# Test database connection
mysql -u root -p -h localhost

# Check database exists
mysql -u root -p -e "SHOW DATABASES;"

# Create database if missing
mysql -u root -p -e "CREATE DATABASE Kids_Vehicle_tracking_Db;"

# Update application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/Kids_Vehicle_tracking_Db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
```

#### **Issue: JWT Token Issues**
**Symptoms:**
- `JWT signature does not match`
- `JWT expired`
- `Invalid JWT token`

**Solutions:**
```properties
# Check JWT configuration in application.properties
app.jwt.secret=mySecretKey
app.jwt.expiration=86400000

# Verify token format
# Should be: Bearer <token>

# Check token expiration
# Default: 24 hours (86400000 ms)

# Regenerate secret key if needed
# Use a strong, random secret key
```

### **API Issues**

#### **Issue: 401 Unauthorized**
**Symptoms:**
- All API calls return 401
- Authentication fails
- Token validation errors

**Solutions:**
```bash
# Check if user exists
mysql -u root -p -e "SELECT * FROM users WHERE user_name='appadmin';"

# Verify password
# Default: admin123

# Check user roles
mysql -u root -p -e "SELECT u.user_name, r.role_name FROM users u JOIN user_roles ur ON u.u_id = ur.user_id JOIN roles r ON ur.role_id = r.role_id WHERE u.user_name='appadmin';"

# Test login API
curl -X POST http://localhost:9001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginId":"appadmin","password":"admin123"}'
```

#### **Issue: 403 Forbidden**
**Symptoms:**
- API calls return 403
- Insufficient permissions
- Role-based access denied

**Solutions:**
```bash
# Check user roles
mysql -u root -p -e "SELECT u.user_name, r.role_name FROM users u JOIN user_roles ur ON u.u_id = ur.user_id JOIN roles r ON ur.role_id = r.role_id;"

# Verify role assignments
# Ensure user has correct role for the endpoint

# Check SecurityConfig
# Verify endpoint permissions in SecurityConfig.java
```

#### **Issue: 500 Internal Server Error**
**Symptoms:**
- Server errors in API responses
- Application crashes
- Database errors

**Solutions:**
```bash
# Check application logs
tail -f logs/application.log

# Check database logs
sudo tail -f /var/log/mysql/error.log

# Verify database schema
mysql -u root -p -e "USE Kids_Vehicle_tracking_Db; SHOW TABLES;"

# Check for missing tables
# Run schema.sql if tables are missing

# Verify data integrity
mysql -u root -p -e "USE Kids_Vehicle_tracking_Db; SELECT COUNT(*) FROM users;"
```

### **WebSocket Issues**

#### **Issue: WebSocket Connection Failed**
**Symptoms:**
- WebSocket connection refused
- STOMP connection errors
- Real-time notifications not working

**Solutions:**
```bash
# Check WebSocket endpoint
curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" -H "Sec-WebSocket-Version: 13" -H "Sec-WebSocket-Key: x3JJHMbDL1EzLkh9GBhXDw==" http://localhost:9001/ws

# Verify WebSocket configuration
# Check WebSocketConfig.java

# Test STOMP connection
# Use browser developer tools or WebSocket client

# Check CORS configuration
# Verify CORS settings in SecurityConfig.java
```

#### **Issue: Notifications Not Received**
**Symptoms:**
- WebSocket connected but no notifications
- Messages not delivered
- Real-time updates missing

**Solutions:**
```bash
# Check notification service
# Verify WebSocketNotificationService is working

# Check message routing
# Verify STOMP destinations

# Test notification manually
# Use WebSocket client to send test messages

# Check user subscriptions
# Verify user is subscribed to correct channels
```

---

## 📱 **Frontend Issues**

### **Flutter Build Issues**

#### **Issue: Flutter Build Fails**
**Symptoms:**
- `flutter build` command fails
- Compilation errors
- Dependency issues

**Solutions:**
```bash
# Check Flutter version
flutter --version
# Should be Flutter 3.x

# Clean and rebuild
flutter clean
flutter pub get
flutter build apk

# Check dependencies
flutter pub deps

# Update dependencies
flutter pub upgrade

# Check for conflicts
flutter pub deps --style=tree
```

#### **Issue: Android Build Issues**
**Symptoms:**
- Android build fails
- Gradle errors
- SDK issues

**Solutions:**
```bash
# Check Android SDK
flutter doctor -v

# Install missing components
flutter doctor --android-licenses

# Clean Android build
cd android
./gradlew clean
cd ..
flutter clean
flutter pub get
flutter build apk

# Check Gradle version
# Update gradle-wrapper.properties if needed
```

#### **Issue: iOS Build Issues**
**Symptoms:**
- iOS build fails
- Xcode errors
- CocoaPods issues

**Solutions:**
```bash
# Check iOS setup
flutter doctor -v

# Install CocoaPods
sudo gem install cocoapods

# Clean iOS build
cd ios
pod deintegrate
pod install
cd ..
flutter clean
flutter pub get
flutter build ios

# Check Xcode version
# Should be Xcode 12+
```

### **Runtime Issues**

#### **Issue: App Crashes on Startup**
**Symptoms:**
- App crashes immediately
- White screen
- Error messages

**Solutions:**
```bash
# Check logs
flutter logs

# Run in debug mode
flutter run --debug

# Check for null safety issues
# Ensure all variables are properly initialized

# Verify API configuration
# Check AppConfig.baseUrl

# Test API connectivity
curl http://localhost:9001/api/auth/login
```

#### **Issue: API Calls Failing**
**Symptoms:**
- Network errors
- Connection timeouts
- HTTP errors

**Solutions:**
```bash
# Check network connectivity
ping localhost

# Verify API endpoint
curl http://localhost:9001/api/auth/login

# Check CORS configuration
# Verify backend CORS settings

# Test with Postman
# Use Postman to test API endpoints

# Check authentication
# Verify JWT token is valid
```

#### **Issue: WebSocket Connection Issues**
**Symptoms:**
- WebSocket connection fails
- Real-time updates not working
- Connection drops frequently

**Solutions:**
```bash
# Check WebSocket URL
# Verify wsUrl in AppConfig

# Test WebSocket endpoint
# Use browser developer tools

# Check STOMP configuration
# Verify STOMP client setup

# Test connection manually
# Use WebSocket client tools
```

### **UI Issues**

#### **Issue: Widget Rendering Problems**
**Symptoms:**
- Widgets not displaying
- Layout issues
- Styling problems

**Solutions:**
```bash
# Check widget tree
# Use Flutter Inspector

# Verify theme configuration
# Check AppTheme settings

# Test on different devices
# Use different screen sizes

# Check for overflow errors
# Look for RenderFlex overflow errors
```

#### **Issue: Navigation Issues**
**Symptoms:**
- Navigation not working
- Route errors
- Page not found

**Solutions:**
```bash
# Check route definitions
# Verify AppRoutes configuration

# Test navigation manually
# Use Navigator.pushNamed

# Check route arguments
# Verify route parameters

# Test deep linking
# Verify app_links configuration
```

---

## 🗄️ **Database Issues**

### **Connection Issues**

#### **Issue: Database Connection Timeout**
**Symptoms:**
- `Connection timed out`
- `HikariPool-1 - Connection is not available`
- Slow database queries

**Solutions:**
```bash
# Check MySQL service
sudo systemctl status mysql

# Check MySQL configuration
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf

# Increase connection timeout
[mysqld]
wait_timeout = 28800
interactive_timeout = 28800
max_connections = 200

# Restart MySQL
sudo systemctl restart mysql

# Check connection pool settings
# Update application.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.connection-timeout=30000
```

#### **Issue: Database Lock Issues**
**Symptoms:**
- `Lock wait timeout exceeded`
- `Deadlock found`
- Transaction conflicts

**Solutions:**
```sql
-- Check for locked tables
SHOW PROCESSLIST;

-- Kill problematic queries
KILL <process_id>;

-- Check for deadlocks
SHOW ENGINE INNODB STATUS;

-- Optimize queries
EXPLAIN SELECT * FROM users WHERE user_name = 'admin';

-- Add proper indexes
CREATE INDEX idx_users_username ON users(user_name);
```

### **Data Issues**

#### **Issue: Missing Data**
**Symptoms:**
- Tables empty
- Data not loading
- Foreign key errors

**Solutions:**
```sql
-- Check table structure
DESCRIBE users;

-- Check data
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM roles;

-- Insert missing data
INSERT INTO roles (role_name, role_description) VALUES 
('APP_ADMIN', 'Application Administrator'),
('SCHOOL_ADMIN', 'School Administrator'),
('DRIVER', 'Driver'),
('PARENT', 'Parent');

-- Check foreign key constraints
SELECT * FROM information_schema.KEY_COLUMN_USAGE 
WHERE REFERENCED_TABLE_NAME = 'users';
```

#### **Issue: Data Corruption**
**Symptoms:**
- Inconsistent data
- Foreign key violations
- Data integrity errors

**Solutions:**
```sql
-- Check data integrity
CHECK TABLE users;
CHECK TABLE roles;
CHECK TABLE user_roles;

-- Repair tables if needed
REPAIR TABLE users;
REPAIR TABLE roles;

-- Check foreign key constraints
SELECT * FROM information_schema.REFERENTIAL_CONSTRAINTS;

-- Fix data inconsistencies
UPDATE users SET is_active = 1 WHERE is_active IS NULL;
```

---

## 🔒 **Security Issues**

### **Authentication Issues**

#### **Issue: Login Not Working**
**Symptoms:**
- Login fails
- Invalid credentials error
- User not found

**Solutions:**
```bash
# Check user exists
mysql -u root -p -e "SELECT * FROM users WHERE user_name='appadmin';"

# Verify password hash
# Check if password is properly hashed

# Test with default credentials
# Username: appadmin
# Password: admin123

# Check user status
mysql -u root -p -e "SELECT user_name, is_active FROM users WHERE user_name='appadmin';"

# Reset password if needed
# Use password reset functionality
```

#### **Issue: JWT Token Issues**
**Symptoms:**
- Token validation fails
- Token expired
- Invalid token format

**Solutions:**
```bash
# Check JWT secret
# Verify app.jwt.secret in application.properties

# Check token expiration
# Default: 24 hours

# Verify token format
# Should be: Bearer <token>

# Test token manually
# Use JWT debugger tools

# Regenerate secret if needed
# Use a strong, random secret key
```

### **Authorization Issues**

#### **Issue: Access Denied**
**Symptoms:**
- 403 Forbidden errors
- Insufficient permissions
- Role-based access denied

**Solutions:**
```bash
# Check user roles
mysql -u root -p -e "SELECT u.user_name, r.role_name FROM users u JOIN user_roles ur ON u.u_id = ur.user_id JOIN roles r ON ur.role_id = r.role_id WHERE u.user_name='appadmin';"

# Verify role assignments
# Ensure user has correct role

# Check SecurityConfig
# Verify endpoint permissions

# Test with different user roles
# Use different user accounts
```

---

## 📊 **Performance Issues**

### **Slow Response Times**

#### **Issue: API Response Slow**
**Symptoms:**
- API calls taking too long
- Timeout errors
- Slow database queries

**Solutions:**
```bash
# Check database performance
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Slow_queries';

# Optimize queries
EXPLAIN SELECT * FROM users WHERE user_name = 'admin';

# Add indexes
CREATE INDEX idx_users_username ON users(user_name);
CREATE INDEX idx_drivers_active ON drivers(is_active);

# Check connection pool
# Monitor HikariCP metrics

# Optimize JVM settings
JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"
```

#### **Issue: Database Performance**
**Symptoms:**
- Slow database queries
- High CPU usage
- Memory issues

**Solutions:**
```sql
-- Check slow query log
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';

-- Enable slow query log
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;

-- Check query performance
EXPLAIN SELECT * FROM users WHERE user_name = 'admin';

-- Optimize indexes
CREATE INDEX idx_users_username ON users(user_name);
CREATE INDEX idx_drivers_active ON drivers(is_active);

-- Check table sizes
SELECT table_name, table_rows, data_length, index_length 
FROM information_schema.tables 
WHERE table_schema = 'Kids_Vehicle_tracking_Db';
```

### **Memory Issues**

#### **Issue: Out of Memory**
**Symptoms:**
- OutOfMemoryError
- Application crashes
- High memory usage

**Solutions:**
```bash
# Check JVM memory settings
java -XX:+PrintFlagsFinal -version | grep -i heap

# Increase heap size
JAVA_OPTS="-Xms1g -Xmx4g"

# Check memory usage
jstat -gc <pid>
jmap -histo <pid>

# Optimize garbage collection
JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

---

## 🔄 **Deployment Issues**

### **Production Deployment**

#### **Issue: Application Won't Start in Production**
**Symptoms:**
- Production deployment fails
- Environment-specific errors
- Configuration issues

**Solutions:**
```bash
# Check environment variables
echo $SPRING_PROFILES_ACTIVE

# Verify production configuration
# Check application-prod.properties

# Test database connection
mysql -u app_user -p -h localhost Kids_Vehicle_tracking_Db_Prod

# Check file permissions
ls -la /opt/kids-vehicle-tracking/

# Verify Java version
java -version

# Check system resources
free -h
df -h
```

#### **Issue: SSL Certificate Issues**
**Symptoms:**
- SSL certificate errors
- HTTPS not working
- Certificate expired

**Solutions:**
```bash
# Check certificate status
sudo certbot certificates

# Renew certificate
sudo certbot renew

# Test SSL
openssl s_client -connect your-domain.com:443

# Check Nginx configuration
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx
```

### **Docker Deployment**

#### **Issue: Docker Container Issues**
**Symptoms:**
- Container won't start
- Port mapping issues
- Volume mounting problems

**Solutions:**
```bash
# Check container status
docker ps -a

# Check container logs
docker logs <container_id>

# Check port mapping
docker port <container_id>

# Check volume mounts
docker inspect <container_id>

# Restart container
docker restart <container_id>

# Rebuild container
docker-compose up -d --build
```

---

## 📋 **Common Error Messages**

### **Backend Error Messages**

#### **Database Errors**
```
HikariPool-1 - Connection is not available, request timed out after 30000ms
```
**Solution:** Check database connection, increase connection pool size

```
Communications link failure
```
**Solution:** Check MySQL service, verify connection string

```
Access denied for user 'root'@'localhost'
```
**Solution:** Check database credentials, verify user permissions

#### **Authentication Errors**
```
JWT signature does not match locally computed signature
```
**Solution:** Check JWT secret key, verify token format

```
JWT expired
```
**Solution:** Check token expiration, refresh token

```
Invalid JWT token
```
**Solution:** Verify token format, check token generation

#### **Application Errors**
```
Port 9001 was already in use
```
**Solution:** Kill existing process, use different port

```
Bean creation failed
```
**Solution:** Check dependency injection, verify configuration

```
Circular dependency detected
```
**Solution:** Refactor code to remove circular dependencies

### **Frontend Error Messages**

#### **Flutter Errors**
```
RenderFlex overflowed by X pixels
```
**Solution:** Use Expanded/Flexible widgets, check layout constraints

```
No Material widget found
```
**Solution:** Wrap widget with MaterialApp or Material

```
setState() called after dispose()
```
**Solution:** Check widget lifecycle, use mounted check

#### **Network Errors**
```
Connection refused
```
**Solution:** Check backend server, verify API endpoint

```
TimeoutException
```
**Solution:** Increase timeout, check network connectivity

```
FormatException: Unexpected character
```
**Solution:** Check JSON parsing, verify API response format

---

## 🛠️ **Debugging Tools**

### **Backend Debugging**

#### **Logging Configuration**
```properties
# application.properties
logging.level.com.app=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

#### **JVM Debugging**
```bash
# Enable JVM debugging
JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Connect debugger
# Use IDE debugger on port 5005
```

#### **Database Debugging**
```sql
-- Enable query log
SET GLOBAL general_log = 'ON';
SET GLOBAL general_log_file = '/var/log/mysql/mysql.log';

-- Check slow queries
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';
```

### **Frontend Debugging**

#### **Flutter Debugging**
```bash
# Enable verbose logging
flutter run --verbose

# Check widget tree
# Use Flutter Inspector

# Check network requests
# Use browser developer tools
```

#### **Dart Debugging**
```dart
// Add debug prints
print('Debug: $variable');

// Use debugger
import 'dart:developer';
debugger();

// Check stack trace
try {
  // code
} catch (e, stackTrace) {
  print('Error: $e');
  print('Stack trace: $stackTrace');
}
```

---

## 📞 **Getting Help**

### **Log Files Location**
```bash
# Backend logs
tail -f logs/application.log

# MySQL logs
sudo tail -f /var/log/mysql/error.log

# Nginx logs
sudo tail -f /var/log/nginx/error.log

# System logs
sudo journalctl -u kids-vehicle-tracking -f
```

### **Useful Commands**
```bash
# Check system resources
top
htop
free -h
df -h

# Check network
netstat -tlnp
ss -tlnp

# Check processes
ps aux | grep java
ps aux | grep mysql

# Check services
sudo systemctl status mysql
sudo systemctl status nginx
```

### **Emergency Procedures**
```bash
# Restart all services
sudo systemctl restart mysql
sudo systemctl restart nginx
sudo systemctl restart kids-vehicle-tracking

# Check application health
curl http://localhost:9001/api/health

# Check database connectivity
mysql -u root -p -e "SELECT 1;"

# Check disk space
df -h

# Check memory usage
free -h
```

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Status**: Production Ready  
**Coverage**: Common issues and solutions  
**Tools**: Logs, debugging tools, monitoring  
**Support**: Comprehensive troubleshooting guide
