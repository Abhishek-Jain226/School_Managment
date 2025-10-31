# Deployment Guide Documentation

## 🚀 **Production Deployment Guide**

This guide provides comprehensive instructions for deploying the Kids Vehicle Tracking Application to production environments.

---

## 📋 **Deployment Overview**

### **Deployment Architecture**
```
┌─────────────────────────────────────────────────────────────────┐
│                    Production Environment                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │   Load      │    │   Web       │    │   Database  │         │
│  │  Balancer   │    │   Server    │    │   Server    │         │
│  │   (Nginx)   │    │ (Spring Boot)│    │  (MySQL)   │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│         │                   │                   │               │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │   CDN       │    │   File      │    │   Backup    │         │
│  │  (Static)   │    │  Storage    │    │   System    │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
└─────────────────────────────────────────────────────────────────┘
```

### **Deployment Options**
1. **Cloud Deployment**: AWS, Google Cloud, Azure
2. **VPS Deployment**: DigitalOcean, Linode, Vultr
3. **Dedicated Server**: On-premise deployment
4. **Container Deployment**: Docker, Kubernetes

---

## ☁️ **Cloud Deployment (AWS)**

### **Step 1: AWS Infrastructure Setup**

#### **EC2 Instance**
```bash
# Launch EC2 instance
# Instance Type: t3.medium or larger
# OS: Ubuntu 20.04 LTS
# Storage: 20GB+ SSD
# Security Groups: HTTP (80), HTTPS (443), SSH (22), Custom (9001)
```

#### **RDS Database**
```bash
# Create RDS MySQL instance
# Engine: MySQL 8.0
# Instance Class: db.t3.micro or larger
# Storage: 20GB+ SSD
# Multi-AZ: Enabled for production
# Backup: Enabled
```

#### **S3 Bucket**
```bash
# Create S3 bucket for file storage
# Enable versioning
# Configure lifecycle policies
# Set up CloudFront distribution
```

### **Step 2: Server Configuration**
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Install MySQL client
sudo apt install mysql-client -y

# Install Nginx
sudo apt install nginx -y

# Install Certbot for SSL
sudo apt install certbot python3-certbot-nginx -y
```

### **Step 3: Application Deployment**
```bash
# Create application directory
sudo mkdir -p /opt/kids-vehicle-tracking
sudo chown $USER:$USER /opt/kids-vehicle-tracking

# Clone repository
cd /opt/kids-vehicle-tracking
git clone <repository-url> .

# Build application
mvn clean package -Pprod

# Create systemd service
sudo nano /etc/systemd/system/kids-vehicle-tracking.service
```

#### **Systemd Service Configuration**
```ini
[Unit]
Description=Kids Vehicle Tracking Application
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/opt/kids-vehicle-tracking
ExecStart=/usr/bin/java -jar target/kids-vehicle-tracking-1.0.0.jar
Restart=always
RestartSec=10
Environment=SPRING_PROFILES_ACTIVE=prod

[Install]
WantedBy=multi-user.target
```

### **Step 4: Nginx Configuration**
```nginx
# /etc/nginx/sites-available/kids-vehicle-tracking
server {
    listen 80;
    server_name your-domain.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    ssl_prefer_server_ciphers off;

    # API Proxy
    location /api/ {
        proxy_pass http://localhost:9001/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # WebSocket Proxy
    location /ws/ {
        proxy_pass http://localhost:9001/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Static Files
    location / {
        root /var/www/kids-vehicle-tracking;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
}
```

### **Step 5: SSL Certificate**
```bash
# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com

# Test certificate renewal
sudo certbot renew --dry-run
```

---

## 🐳 **Docker Deployment**

### **Step 1: Dockerfile for Backend**
```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/kids-vehicle-tracking-1.0.0.jar app.jar

EXPOSE 9001

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### **Step 2: Docker Compose**
```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "9001:9001"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/Kids_Vehicle_tracking_Db
      - SPRING_DATASOURCE_USERNAME=app_user
      - SPRING_DATASOURCE_PASSWORD=secure_password
    depends_on:
      - db
    restart: unless-stopped

  db:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=root_password
      - MYSQL_DATABASE=Kids_Vehicle_tracking_Db
      - MYSQL_USER=app_user
      - MYSQL_PASSWORD=secure_password
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/schema.sql:/docker-entrypoint-initdb.d/schema.sql
      - ./database/data.sql:/docker-entrypoint-initdb.d/data.sql
    ports:
      - "3306:3306"
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - app
    restart: unless-stopped

volumes:
  mysql_data:
```

### **Step 3: Deploy with Docker**
```bash
# Build and deploy
docker-compose up -d

# View logs
docker-compose logs -f

# Scale application
docker-compose up -d --scale app=3
```

---

## 📱 **Flutter App Deployment**

### **Step 1: Android App Deployment**

#### **Build APK**
```bash
# Build release APK
flutter build apk --release

# Build App Bundle (recommended for Play Store)
flutter build appbundle --release
```

#### **Google Play Store**
```bash
# Sign the app bundle
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore release-key.keystore app-release.aab alias_name

# Upload to Play Console
# Go to Google Play Console
# Create new application
# Upload signed app bundle
# Complete store listing
# Submit for review
```

### **Step 2: iOS App Deployment**

#### **Build iOS App**
```bash
# Build iOS app
flutter build ios --release

# Archive in Xcode
# Open ios/Runner.xcworkspace in Xcode
# Select Product > Archive
# Upload to App Store Connect
```

#### **App Store**
```bash
# Upload to App Store Connect
# Go to App Store Connect
# Create new app
# Upload build
# Complete app information
# Submit for review
```

### **Step 3: Web App Deployment**

#### **Build Web App**
```bash
# Build web app
flutter build web --release

# Deploy to web server
# Copy build/web/* to web server directory
```

#### **Deploy to Firebase Hosting**
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize Firebase
firebase init hosting

# Deploy
firebase deploy
```

---

## 🗄️ **Database Deployment**

### **Step 1: Production Database Setup**
```sql
-- Create production database
CREATE DATABASE Kids_Vehicle_tracking_Db_Prod CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Create production user
CREATE USER 'app_user'@'%' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON Kids_Vehicle_tracking_Db_Prod.* TO 'app_user'@'%';
FLUSH PRIVILEGES;
```

### **Step 2: Database Migration**
```bash
# Run migration scripts
mysql -u app_user -p Kids_Vehicle_tracking_Db_Prod < database/schema.sql
mysql -u app_user -p Kids_Vehicle_tracking_Db_Prod < database/data.sql
```

### **Step 3: Database Backup**
```bash
# Create backup script
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u app_user -p Kids_Vehicle_tracking_Db_Prod > backup_$DATE.sql

# Schedule backup
crontab -e
# Add: 0 2 * * * /path/to/backup_script.sh
```

---

## 🔒 **Security Configuration**

### **Step 1: Firewall Configuration**
```bash
# Configure UFW
sudo ufw enable
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw deny 9001/tcp
```

### **Step 2: SSL/TLS Configuration**
```nginx
# Enhanced SSL configuration
ssl_protocols TLSv1.2 TLSv1.3;
ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
ssl_prefer_server_ciphers off;
ssl_session_cache shared:SSL:10m;
ssl_session_timeout 10m;
ssl_stapling on;
ssl_stapling_verify on;
```

### **Step 3: Security Headers**
```nginx
# Security headers
add_header X-Frame-Options DENY;
add_header X-Content-Type-Options nosniff;
add_header X-XSS-Protection "1; mode=block";
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
```

---

## 📊 **Monitoring & Logging**

### **Step 1: Application Monitoring**
```bash
# Install monitoring tools
sudo apt install htop iotop nethogs -y

# Configure log rotation
sudo nano /etc/logrotate.d/kids-vehicle-tracking
```

#### **Log Rotation Configuration**
```
/opt/kids-vehicle-tracking/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 ubuntu ubuntu
}
```

### **Step 2: Database Monitoring**
```sql
-- Enable slow query log
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;
SET GLOBAL log_queries_not_using_indexes = 'ON';
```

### **Step 3: Health Checks**
```bash
# Create health check script
#!/bin/bash
curl -f http://localhost:9001/api/health || exit 1
```

---

## 🚀 **CI/CD Pipeline**

### **Step 1: GitHub Actions**
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Setup Java
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'
    
    - name: Build Application
      run: mvn clean package -Pprod
    
    - name: Deploy to Server
      uses: appleboy/ssh-action@v0.1.5
      with:
        host: ${{ secrets.HOST }}
        username: ${{ secrets.USERNAME }}
        key: ${{ secrets.SSH_KEY }}
        script: |
          cd /opt/kids-vehicle-tracking
          git pull origin main
          mvn clean package -Pprod
          sudo systemctl restart kids-vehicle-tracking
```

### **Step 2: Automated Testing**
```yaml
# .github/workflows/test.yml
name: Test Application

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Setup Java
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'
    
    - name: Run Tests
      run: mvn test
    
    - name: Run Integration Tests
      run: mvn verify
```

---

## 📈 **Performance Optimization**

### **Step 1: JVM Tuning**
```bash
# JVM parameters for production
JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication"
```

### **Step 2: Database Optimization**
```sql
-- Optimize MySQL configuration
SET GLOBAL innodb_buffer_pool_size = 1G;
SET GLOBAL max_connections = 200;
SET GLOBAL query_cache_size = 64M;
```

### **Step 3: Nginx Optimization**
```nginx
# Nginx optimization
worker_processes auto;
worker_connections 1024;

gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
```

---

## 🔄 **Backup & Recovery**

### **Step 1: Database Backup**
```bash
# Automated backup script
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/backups"
DB_NAME="Kids_Vehicle_tracking_Db_Prod"

# Create backup
mysqldump -u app_user -p$DB_PASSWORD $DB_NAME > $BACKUP_DIR/db_backup_$DATE.sql

# Compress backup
gzip $BACKUP_DIR/db_backup_$DATE.sql

# Remove old backups (keep 30 days)
find $BACKUP_DIR -name "db_backup_*.sql.gz" -mtime +30 -delete
```

### **Step 2: Application Backup**
```bash
# Application backup script
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/backups"
APP_DIR="/opt/kids-vehicle-tracking"

# Create application backup
tar -czf $BACKUP_DIR/app_backup_$DATE.tar.gz -C $APP_DIR .

# Remove old backups (keep 7 days)
find $BACKUP_DIR -name "app_backup_*.tar.gz" -mtime +7 -delete
```

### **Step 3: Disaster Recovery**
```bash
# Recovery script
#!/bin/bash
BACKUP_FILE=$1
DB_NAME="Kids_Vehicle_tracking_Db_Prod"

# Restore database
mysql -u app_user -p$DB_PASSWORD $DB_NAME < $BACKUP_FILE

# Restart application
sudo systemctl restart kids-vehicle-tracking
```

---

## 📊 **Deployment Checklist**

### **Pre-Deployment**
- [ ] Code reviewed and tested
- [ ] Database schema updated
- [ ] Environment variables configured
- [ ] SSL certificates obtained
- [ ] Domain configured
- [ ] Backup procedures tested

### **Deployment**
- [ ] Application deployed
- [ ] Database migrated
- [ ] SSL configured
- [ ] Firewall configured
- [ ] Monitoring configured
- [ ] Health checks passing

### **Post-Deployment**
- [ ] Application accessible
- [ ] All features working
- [ ] Performance acceptable
- [ ] Logs being generated
- [ ] Backups running
- [ ] Monitoring active

---

## 🚨 **Troubleshooting**

### **Common Issues**

#### **Application Won't Start**
```bash
# Check logs
sudo journalctl -u kids-vehicle-tracking -f

# Check Java version
java -version

# Check port availability
netstat -tlnp | grep :9001
```

#### **Database Connection Issues**
```bash
# Test database connection
mysql -u app_user -p -h localhost Kids_Vehicle_tracking_Db_Prod

# Check MySQL status
sudo systemctl status mysql

# Check database logs
sudo tail -f /var/log/mysql/error.log
```

#### **SSL Certificate Issues**
```bash
# Check certificate
sudo certbot certificates

# Renew certificate
sudo certbot renew

# Test SSL
openssl s_client -connect your-domain.com:443
```

---

## 📞 **Support & Maintenance**

### **Regular Maintenance**
- **Daily**: Check application logs and health
- **Weekly**: Review performance metrics
- **Monthly**: Update dependencies and security patches
- **Quarterly**: Review and update backup procedures

### **Monitoring Alerts**
- **Application Down**: Immediate notification
- **High CPU/Memory**: Alert when > 80%
- **Database Issues**: Alert on connection failures
- **SSL Expiry**: Alert 30 days before expiry

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Status**: Production Ready  
**Deployment Time**: 2-4 hours  
**Difficulty**: Advanced  
**Prerequisites**: System administration knowledge, cloud platform experience
