# Database Schema Documentation

## 🗄️ **Database Overview**

**Database Name**: `Kids_Vehicle_tracking_Db`  
**Engine**: MySQL 8.0  
**Character Set**: utf8mb4  
**Collation**: utf8mb4_0900_ai_ci  
**Connection Pool**: HikariCP  

---

## 📊 **Table Structure**

### **1. User Management Tables**

#### **`users` Table**
```sql
CREATE TABLE `users` (
  `u_id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `contact_number` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`u_id`),
  UNIQUE KEY `uk_users_username` (`user_name`),
  UNIQUE KEY `uk_users_email` (`email`),
  UNIQUE KEY `uk_users_contact` (`contact_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

#### **`roles` Table**
```sql
CREATE TABLE `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  `role_description` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_roles_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

#### **`user_roles` Table**
```sql
CREATE TABLE `user_roles` (
  `user_role_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`user_role_id`),
  UNIQUE KEY `uk_user_roles` (`user_id`,`role_id`),
  KEY `FK_user_roles_user` (`user_id`),
  KEY `FK_user_roles_role` (`role_id`),
  CONSTRAINT `FK_user_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`),
  CONSTRAINT `FK_user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`u_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

---

### **2. School Management Tables**

#### **`school` Table**
```sql
CREATE TABLE `school` (
  `school_id` int NOT NULL AUTO_INCREMENT,
  `school_code` varchar(50) NOT NULL,
  `school_name` varchar(200) NOT NULL,
  `school_type` varchar(100) DEFAULT NULL,
  `affiliation_board` varchar(100) DEFAULT NULL,
  `registration_number` varchar(100) DEFAULT NULL,
  `address` varchar(300) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `district` varchar(100) DEFAULT NULL,
  `state` varchar(100) DEFAULT NULL,
  `pincode` varchar(20) DEFAULT NULL,
  `contact_no` varchar(20) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `school_photo` longtext,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`school_id`),
  UNIQUE KEY `uk_school_code` (`school_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

#### **`school_users` Table**
```sql
CREATE TABLE `school_users` (
  `school_user_id` int NOT NULL AUTO_INCREMENT,
  `school_id` int NOT NULL,
  `user_id` int NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`school_user_id`),
  UNIQUE KEY `uk_school_users` (`school_id`,`user_id`),
  KEY `FK_school_users_school` (`school_id`),
  KEY `FK_school_users_user` (`user_id`),
  CONSTRAINT `FK_school_users_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`),
  CONSTRAINT `FK_school_users_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`u_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

---

### **3. Vehicle Management Tables**

#### **`vehicles` Table**
```sql
CREATE TABLE `vehicles` (
  `vehicle_id` int NOT NULL AUTO_INCREMENT,
  `vehicle_number` varchar(20) NOT NULL,
  `vehicle_type` enum('BUS','VAN','CAR','AUTO') NOT NULL,
  `capacity` int NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`vehicle_id`),
  UNIQUE KEY `uk_vehicles_number` (`vehicle_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

#### **`vehicle_owner` Table**
```sql
CREATE TABLE `vehicle_owner` (
  `owner_id` int NOT NULL AUTO_INCREMENT,
  `u_id` int DEFAULT NULL,
  `name` varchar(150) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `owner_photo` longtext,
  `address` varchar(300) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`owner_id`),
  UNIQUE KEY `uk_vehicle_owner_user` (`u_id`),
  KEY `FK_vehicle_owner_user` (`u_id`),
  CONSTRAINT `FK_vehicle_owner_user` FOREIGN KEY (`u_id`) REFERENCES `users` (`u_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

#### **`drivers` Table**
```sql
CREATE TABLE `drivers` (
  `driver_id` int NOT NULL AUTO_INCREMENT,
  `u_id` int DEFAULT NULL,
  `driver_name` varchar(150) NOT NULL,
  `driver_contact_number` varchar(20) NOT NULL,
  `driver_address` varchar(300) DEFAULT NULL,
  `driver_photo` longtext,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`driver_id`),
  UNIQUE KEY `uk_drivers_user` (`u_id`),
  UNIQUE KEY `uk_drivers_contact` (`driver_contact_number`),
  KEY `FK_drivers_user` (`u_id`),
  CONSTRAINT `FK_drivers_user` FOREIGN KEY (`u_id`) REFERENCES `users` (`u_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

#### **`vehicle_driver` Table**
```sql
CREATE TABLE `vehicle_driver` (
  `vehicle_driver_id` int NOT NULL AUTO_INCREMENT,
  `vehicle_id` int NOT NULL,
  `driver_id` int NOT NULL,
  `school_id` int NOT NULL,
  `is_primary` tinyint(1) DEFAULT '0',
  `is_active` tinyint(1) DEFAULT '1',
  `is_backup_driver` tinyint(1) DEFAULT '0',
  `is_available` tinyint(1) DEFAULT '1',
  `unavailability_reason` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`vehicle_driver_id`),
  KEY `idx_vehicle_driver_driver_id` (`driver_id`),
  KEY `idx_vehicle_driver_vehicle_id` (`vehicle_id`),
  KEY `idx_vehicle_driver_school_id` (`school_id`),
  KEY `idx_vehicle_driver_is_active` (`is_active`),
  KEY `idx_vehicle_driver_is_primary` (`is_primary`),
  KEY `FK_vehicle_driver_vehicle` (`vehicle_id`),
  KEY `FK_vehicle_driver_driver` (`driver_id`),
  KEY `FK_vehicle_driver_school` (`school_id`),
  CONSTRAINT `FK_vehicle_driver_driver` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`driver_id`),
  CONSTRAINT `FK_vehicle_driver_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`),
  CONSTRAINT `FK_vehicle_driver_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

---

### **4. Student Management Tables**

#### **`students` Table**
```sql
CREATE TABLE `students` (
  `student_id` int NOT NULL AUTO_INCREMENT,
  `student_name` varchar(150) NOT NULL,
  `student_photo` longtext,
  `student_address` varchar(300) DEFAULT NULL,
  `class_name` varchar(50) DEFAULT NULL,
  `section` varchar(50) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

#### **`student_parents` Table**
```sql
CREATE TABLE `student_parents` (
  `student_parent_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `parent_id` int NOT NULL,
  `relationship` varchar(50) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`student_parent_id`),
  UNIQUE KEY `uk_student_parents` (`student_id`,`parent_id`),
  KEY `FK_student_parents_student` (`student_id`),
  KEY `FK_student_parents_parent` (`parent_id`),
  CONSTRAINT `FK_student_parents_parent` FOREIGN KEY (`parent_id`) REFERENCES `users` (`u_id`),
  CONSTRAINT `FK_student_parents_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

---

### **5. Trip Management Tables**

#### **`trips` Table**
```sql
CREATE TABLE `trips` (
  `trip_id` int NOT NULL AUTO_INCREMENT,
  `trip_name` varchar(100) NOT NULL,
  `trip_number` varchar(50) NOT NULL,
  `driver_id` int NOT NULL,
  `school_id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `trip_type` enum('MORNING_PICKUP','AFTERNOON_DROP') NOT NULL,
  `trip_status` enum('NOT_STARTED','IN_PROGRESS','COMPLETED','CANCELLED') DEFAULT 'NOT_STARTED',
  `start_time` time DEFAULT NULL,
  `end_time` time DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`trip_id`),
  UNIQUE KEY `uk_trip_school_vehicle_number` (`school_id`,`vehicle_id`,`trip_number`),
  KEY `idx_trips_driver_id` (`driver_id`),
  KEY `idx_trips_school_id` (`school_id`),
  KEY `idx_trips_vehicle_id` (`vehicle_id`),
  KEY `idx_trips_trip_type` (`trip_type`),
  KEY `FK_trips_driver` (`driver_id`),
  KEY `FK_trips_school` (`school_id`),
  KEY `FK_trips_vehicle` (`vehicle_id`),
  CONSTRAINT `FK_trips_driver` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`driver_id`),
  CONSTRAINT `FK_trips_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`),
  CONSTRAINT `FK_trips_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

#### **`trip_students` Table**
```sql
CREATE TABLE `trip_students` (
  `trip_student_id` int NOT NULL AUTO_INCREMENT,
  `trip_id` int NOT NULL,
  `student_id` int NOT NULL,
  `pickup_order` int DEFAULT NULL,
  `attendance_status` varchar(20) DEFAULT 'PENDING',
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`trip_student_id`),
  UNIQUE KEY `uk_trip_student` (`trip_id`,`student_id`),
  KEY `idx_trip_students_trip_id` (`trip_id`),
  KEY `idx_trip_students_student_id` (`student_id`),
  KEY `idx_trip_students_attendance_status` (`attendance_status`),
  KEY `FK_trip_students_trip` (`trip_id`),
  KEY `FK_trip_students_student` (`student_id`),
  CONSTRAINT `FK_trip_students_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  CONSTRAINT `FK_trip_students_trip` FOREIGN KEY (`trip_id`) REFERENCES `trips` (`trip_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

---

### **6. Tracking & Logging Tables**

#### **`dispatch_logs` Table**
```sql
CREATE TABLE `dispatch_logs` (
  `dispatch_log_id` int NOT NULL AUTO_INCREMENT,
  `trip_id` int NOT NULL,
  `student_id` int NOT NULL,
  `school_id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `driver_id` int DEFAULT NULL,
  `event_type` enum('DROP_TO_PARENT','DROP_TO_SCHOOL','GATE_ENTRY','GATE_EXIT','PICKUP_FROM_PARENT','PICKUP_FROM_SCHOOL','ARRIVAL_NOTIFICATION','PICKUP_CONFIRMATION','DROP_CONFIRMATION','DELAY_NOTIFICATION') NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`dispatch_log_id`),
  KEY `idx_dispatch_logs_created_date` (`created_date`),
  KEY `idx_dispatch_logs_event_type` (`event_type`),
  KEY `idx_dispatch_logs_driver_id` (`driver_id`),
  KEY `idx_dispatch_logs_trip_student` (`trip_id`,`student_id`),
  KEY `FK_dispatch_logs_trip` (`trip_id`),
  KEY `FK_dispatch_logs_student` (`student_id`),
  KEY `FK_dispatch_logs_school` (`school_id`),
  KEY `FK_dispatch_logs_vehicle` (`vehicle_id`),
  KEY `FK_dispatch_logs_driver` (`driver_id`),
  CONSTRAINT `FK_dispatch_logs_driver` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`driver_id`),
  CONSTRAINT `FK_dispatch_logs_school` FOREIGN KEY (`school_id`) REFERENCES `school` (`school_id`),
  CONSTRAINT `FK_dispatch_logs_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  CONSTRAINT `FK_dispatch_logs_trip` FOREIGN KEY (`trip_id`) REFERENCES `trips` (`trip_id`),
  CONSTRAINT `FK_dispatch_logs_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

#### **`notifications` Table**
```sql
CREATE TABLE `notifications` (
  `notification_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(200) NOT NULL,
  `message` text NOT NULL,
  `notification_type` enum('TRIP_UPDATE','ATTENDANCE_UPDATE','SYSTEM_ALERT','ARRIVAL_NOTIFICATION','PICKUP_CONFIRMATION','DROP_CONFIRMATION','DELAY_NOTIFICATION') NOT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  `priority` enum('LOW','MEDIUM','HIGH','URGENT') DEFAULT 'MEDIUM',
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`notification_id`),
  KEY `idx_notifications_user_id` (`user_id`),
  KEY `idx_notifications_type` (`notification_type`),
  KEY `idx_notifications_is_read` (`is_read`),
  KEY `FK_notifications_user` (`user_id`),
  CONSTRAINT `FK_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`u_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

---

### **7. System Tables**

#### **`password_reset_tokens` Table**
```sql
CREATE TABLE `password_reset_tokens` (
  `token_id` int NOT NULL AUTO_INCREMENT,
  `login_id` varchar(100) NOT NULL,
  `otp` varchar(10) NOT NULL,
  `expiry` datetime(6) NOT NULL,
  `used` tinyint(1) DEFAULT '0',
  `created_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  KEY `idx_password_reset_login_otp` (`login_id`,`otp`,`used`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

#### **`trip_status` Table**
```sql
CREATE TABLE `trip_status` (
  `trip_status_id` int NOT NULL AUTO_INCREMENT,
  `trip_id` int NOT NULL,
  `status` enum('NOT_STARTED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL,
  `status_date` datetime(6) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`trip_status_id`),
  KEY `FK_trip_status_trip` (`trip_id`),
  CONSTRAINT `FK_trip_status_trip` FOREIGN KEY (`trip_id`) REFERENCES `trips` (`trip_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

---

## 🔗 **Entity Relationships**

### **Primary Relationships**
```
Users (1) ──────── (M) UserRoles (M) ──────── (1) Roles
   │
   │ (1)
   │
   └── (1) VehicleOwner
   │
   └── (1) Driver ──── (M) VehicleDriver (M) ──── (1) Vehicle
   │                        │
   │                        │ (M)
   │                        │
   │                        └─── (1) School
   │
   └── (M) StudentParents (M) ──── (1) Students
```

### **Trip Relationships**
```
Trips (1) ──── (M) TripStudents (M) ──── (1) Students
  │
  │ (1)
  │
  └── (M) DispatchLogs
  │
  └── (M) TripStatus
```

---

## 📊 **Indexes for Performance**

### **Primary Indexes**
- All primary keys are automatically indexed
- All foreign keys have indexes for join performance

### **Performance Indexes**
```sql
-- Dispatch logs performance
CREATE INDEX idx_dispatch_logs_created_date ON dispatch_logs(created_date);
CREATE INDEX idx_dispatch_logs_event_type ON dispatch_logs(event_type);
CREATE INDEX idx_dispatch_logs_driver_id ON dispatch_logs(driver_id);
CREATE INDEX idx_dispatch_logs_trip_student ON dispatch_logs(trip_id, student_id);

-- Trip performance
CREATE INDEX idx_trips_driver_id ON trips(driver_id);
CREATE INDEX idx_trips_school_id ON trips(school_id);
CREATE INDEX idx_trips_vehicle_id ON trips(vehicle_id);
CREATE INDEX idx_trips_trip_type ON trips(trip_type);

-- Trip students performance
CREATE INDEX idx_trip_students_trip_id ON trip_students(trip_id);
CREATE INDEX idx_trip_students_student_id ON trip_students(student_id);
CREATE INDEX idx_trip_students_attendance_status ON trip_students(attendance_status);

-- Vehicle driver performance
CREATE INDEX idx_vehicle_driver_driver_id ON vehicle_driver(driver_id);
CREATE INDEX idx_vehicle_driver_vehicle_id ON vehicle_driver(vehicle_id);
CREATE INDEX idx_vehicle_driver_school_id ON vehicle_driver(school_id);
CREATE INDEX idx_vehicle_driver_is_active ON vehicle_driver(is_active);
CREATE INDEX idx_vehicle_driver_is_primary ON vehicle_driver(is_primary);

-- Notifications performance
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_type ON notifications(notification_type);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
```

---

## 🔧 **Database Configuration**

### **Connection Pool Settings**
```properties
# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.leak-detection-threshold=60000
```

### **MySQL Configuration**
```sql
-- Character set and collation
ALTER DATABASE Kids_Vehicle_tracking_Db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- Time zone settings
SET time_zone = '+00:00';

-- InnoDB settings for performance
SET innodb_buffer_pool_size = 1G;
SET innodb_log_file_size = 256M;
SET innodb_flush_log_at_trx_commit = 2;
```

---

## 📈 **Data Volume Estimates**

### **Expected Data Volumes**
- **Users**: 10,000 - 50,000 records
- **Schools**: 100 - 1,000 records
- **Vehicles**: 500 - 5,000 records
- **Drivers**: 500 - 5,000 records
- **Students**: 50,000 - 500,000 records
- **Trips**: 1,000 - 10,000 records per day
- **Dispatch Logs**: 10,000 - 100,000 records per day
- **Notifications**: 5,000 - 50,000 records per day

### **Storage Requirements**
- **Database Size**: 1GB - 10GB (including indexes)
- **Image Storage**: 100MB - 1GB (base64 encoded)
- **Log Files**: 100MB - 1GB per month
- **Backup Size**: 2GB - 20GB (compressed)

---

## 🔒 **Security Considerations**

### **Data Protection**
- **Password Encryption**: BCrypt hashing
- **Sensitive Data**: Encrypted storage for PII
- **Access Control**: Role-based permissions
- **Audit Trail**: Created/updated by tracking

### **Backup Strategy**
- **Daily Backups**: Automated database backups
- **Point-in-time Recovery**: Binary log enabled
- **Offsite Storage**: Backup replication
- **Retention Policy**: 30 days for daily, 12 months for weekly

---

## 🚀 **Performance Optimization**

### **Query Optimization**
- **Proper Indexing**: Strategic index placement
- **Query Analysis**: Regular EXPLAIN analysis
- **Connection Pooling**: HikariCP optimization
- **Caching**: Application-level caching

### **Maintenance Tasks**
- **Index Maintenance**: Regular index optimization
- **Statistics Update**: Automatic statistics refresh
- **Log Rotation**: Automated log management
- **Vacuum Operations**: Regular cleanup tasks

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Status**: Production Ready
