# API Documentation

## 🌐 **API Overview**

**Base URL**: `http://localhost:9001/api` (Development)  
**Protocol**: HTTP/HTTPS  
**Authentication**: JWT Bearer Token  
**Content Type**: `application/json`  
**API Version**: v1  

---

## 🔐 **Authentication APIs**

### **1. User Login**
```http
POST /api/auth/login
```

**Request Body:**
```json
{
  "loginId": "string",     // Username or mobile number
  "password": "string"     // User password
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "userName": "admin",
    "email": "admin@example.com",
    "roles": ["APP_ADMIN"],
    "schoolId": null,
    "schoolName": null,
    "ownerId": null,
    "driverId": null
  }
}
```

### **2. Forgot Password**
```http
POST /api/auth/forgot-password
```

**Request Body:**
```json
{
  "loginId": "string"      // Username, email, or mobile number
}
```

**Response:**
```json
{
  "success": true,
  "message": "OTP sent to registered email/mobile",
  "data": null
}
```

### **3. Reset Password**
```http
POST /api/auth/reset-password
```

**Request Body:**
```json
{
  "loginId": "string",     // Username, email, or mobile number
  "otp": "string",         // 6-digit OTP
  "newPassword": "string"  // New password
}
```

**Response:**
```json
{
  "success": true,
  "message": "Password reset successful",
  "data": null
}
```

---

## 🏫 **School Management APIs**

### **1. Get All Schools (AppAdmin)**
```http
GET /api/app-admin/schools
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Schools retrieved successfully",
  "data": [
    {
      "schoolId": 1,
      "schoolCode": "SCH001",
      "schoolName": "ABC School",
      "schoolType": "Private",
      "address": "123 Main St",
      "city": "Mumbai",
      "district": "Mumbai",
      "state": "Maharashtra",
      "pincode": "400001",
      "contactNo": "9876543210",
      "email": "contact@abcschool.com",
      "isActive": true,
      "startDate": "2024-01-01",
      "endDate": "2024-12-31",
      "hasActiveUser": true
    }
  ]
}
```

### **2. Create School**
```http
POST /api/app-admin/schools
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "schoolCode": "SCH002",
  "schoolName": "XYZ School",
  "schoolType": "Government",
  "affiliationBoard": "CBSE",
  "registrationNumber": "REG123456",
  "address": "456 Park Ave",
  "city": "Delhi",
  "district": "New Delhi",
  "state": "Delhi",
  "pincode": "110001",
  "contactNo": "9876543211",
  "email": "contact@xyzschool.com",
  "schoolPhoto": "base64_encoded_image_string"
}
```

### **3. Update School**
```http
PUT /api/app-admin/schools/{schoolId}
Authorization: Bearer {token}
```

### **4. Activate/Deactivate School**
```http
PUT /api/app-admin/schools/{schoolId}/status
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "isActive": false
}
```

### **5. Resend Activation Link**
```http
POST /api/app-admin/schools/{schoolId}/resend-activation
Authorization: Bearer {token}
```

---

## 🚐 **Vehicle & Driver Management APIs**

### **1. Create Driver**
```http
POST /api/drivers/create
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "driverName": "John Doe",
  "driverContactNumber": "9876543212",
  "driverAddress": "789 Driver St",
  "driverPhoto": "base64_encoded_image_string"
}
```

### **2. Get Driver by User ID**
```http
GET /api/drivers/user/{userId}
Authorization: Bearer {token}
```

### **3. Get Driver Dashboard**
```http
GET /api/drivers/{driverId}/dashboard
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Driver dashboard data retrieved successfully",
  "data": {
    "driverId": 1,
    "driverName": "John Doe",
    "driverContactNumber": "9876543212",
    "driverPhoto": "base64_encoded_image_string",
    "vehicleId": 1,
    "vehicleNumber": "MH01AB1234",
    "vehicleType": "BUS",
    "vehicleCapacity": 50,
    "schoolId": 1,
    "schoolName": "ABC School",
    "totalTripsToday": 2,
    "completedTrips": 1,
    "pendingTrips": 1,
    "totalStudentsToday": 25,
    "studentsPickedUp": 15,
    "studentsDropped": 10,
    "currentTripId": 1,
    "currentTripName": "Morning Pickup",
    "currentTripStatus": "IN_PROGRESS",
    "currentTripStartTime": "08:00:00",
    "currentTripStudentCount": 15,
    "recentActivities": []
  }
}
```

### **4. Get Assigned Trips**
```http
GET /api/drivers/{driverId}/trips
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Assigned trips retrieved successfully",
  "data": [
    {
      "tripId": 1,
      "tripName": "Morning Pickup",
      "tripNumber": "TRIP001",
      "tripType": "MORNING_PICKUP",
      "tripStatus": "IN_PROGRESS",
      "startTime": "08:00:00",
      "endTime": "09:30:00",
      "schoolName": "ABC School",
      "vehicleNumber": "MH01AB1234",
      "studentCount": 15
    }
  ]
}
```

### **5. Get Trip Students**
```http
GET /api/drivers/{driverId}/trip/{tripId}/students
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Trip students retrieved successfully",
  "data": {
    "tripId": 1,
    "tripName": "Morning Pickup",
    "students": [
      {
        "studentId": 1,
        "studentName": "Alice Johnson",
        "studentPhoto": "base64_encoded_image_string",
        "studentAddress": "123 Student St",
        "className": "5th",
        "section": "A",
        "pickupOrder": 1,
        "attendanceStatus": "PENDING"
      }
    ]
  }
}
```

---

## 📚 **Student Attendance APIs**

### **1. Mark Student Attendance**
```http
POST /api/drivers/{driverId}/attendance
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "tripId": 1,
  "studentId": 1,
  "eventType": "PICKUP_FROM_PARENT",
  "remarks": "Student picked up successfully"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Student attendance marked successfully",
  "data": {
    "dispatchLogId": 1,
    "eventType": "PICKUP_FROM_PARENT",
    "timestamp": "2024-12-15T08:30:00",
    "studentName": "Alice Johnson",
    "tripName": "Morning Pickup"
  }
}
```

### **2. Send Parent Notification**
```http
POST /api/drivers/{driverId}/notify-parents
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "tripId": 1,
  "studentId": 1,
  "notificationType": "ARRIVAL_NOTIFICATION",
  "message": "Driver will arrive in 5 minutes",
  "dispatchLogId": null
}
```

---

## 🚗 **Driver Action APIs**

### **1. Send 5-Minute Alert**
```http
POST /api/drivers/{driverId}/trip/{tripId}/alert-5min
Authorization: Bearer {token}
```

### **2. Mark Pickup from Home**
```http
POST /api/drivers/{driverId}/trip/{tripId}/student/{studentId}/pickup-home
Authorization: Bearer {token}
```

### **3. Mark Drop to School**
```http
POST /api/drivers/{driverId}/trip/{tripId}/student/{studentId}/drop-school
Authorization: Bearer {token}
```

### **4. Mark Pickup from School**
```http
POST /api/drivers/{driverId}/trip/{tripId}/student/{studentId}/pickup-school
Authorization: Bearer {token}
```

### **5. Mark Drop to Home**
```http
POST /api/drivers/{driverId}/trip/{tripId}/student/{studentId}/drop-home
Authorization: Bearer {token}
```

### **6. Update Driver Location**
```http
POST /api/drivers/{driverId}/location
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "latitude": 19.0760,
  "longitude": 72.8777
}
```

### **7. Get Driver Location**
```http
GET /api/drivers/{driverId}/location
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Driver location retrieved successfully",
  "data": {
    "latitude": 19.0760,
    "longitude": 72.8777,
    "address": "Mumbai, Maharashtra, India",
    "timestamp": "2024-12-15T08:30:00"
  }
}
```

### **8. End Trip**
```http
POST /api/drivers/{driverId}/trip/{tripId}/end
Authorization: Bearer {token}
```

---

## 👨‍👩‍👧‍👦 **Parent APIs**

### **1. Get Student Trips**
```http
GET /api/students/{studentId}/trips
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Student trips retrieved successfully",
  "data": [
    {
      "tripId": 1,
      "tripName": "Morning Pickup",
      "tripType": "MORNING_PICKUP",
      "tripStatus": "IN_PROGRESS",
      "startTime": "08:00:00",
      "endTime": "09:30:00",
      "schoolName": "ABC School",
      "vehicleNumber": "MH01AB1234",
      "driverName": "John Doe"
    }
  ]
}
```

### **2. Get Driver Location**
```http
GET /api/drivers/{driverId}/location
Authorization: Bearer {token}
```

---

## 🔔 **Notification APIs**

### **1. Get User Notifications**
```http
GET /api/notifications/user/{userId}
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Notifications retrieved successfully",
  "data": [
    {
      "notificationId": 1,
      "title": "Pickup Confirmation",
      "message": "Alice Johnson has been picked up from home",
      "notificationType": "PICKUP_CONFIRMATION",
      "isRead": false,
      "priority": "MEDIUM",
      "createdDate": "2024-12-15T08:30:00"
    }
  ]
}
```

### **2. Mark Notification as Read**
```http
PUT /api/notifications/{notificationId}/read
Authorization: Bearer {token}
```

---

## 🌐 **WebSocket APIs**

### **WebSocket Connection**
```javascript
// WebSocket URL
ws://localhost:9001/ws

// STOMP Configuration
const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:9001/ws',
  connectHeaders: {
    Authorization: 'Bearer ' + token
  }
});
```

### **Subscribe to Notifications**
```javascript
// User-specific notifications
stompClient.subscribe('/user/queue/notifications', (message) => {
  const notification = JSON.parse(message.body);
  // Handle notification
});

// School-wide notifications
stompClient.subscribe('/topic/school/{schoolId}', (message) => {
  const notification = JSON.parse(message.body);
  // Handle school notification
});

// Role-based notifications
stompClient.subscribe('/topic/role/{role}', (message) => {
  const notification = JSON.parse(message.body);
  // Handle role notification
});
```

### **Send Message**
```javascript
stompClient.publish({
  destination: '/app/chat.sendMessage',
  body: JSON.stringify({
    content: 'Hello',
    sender: 'user123'
  })
});
```

---

## 📊 **Response Format**

### **Success Response**
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data
  }
}
```

### **Error Response**
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "details": "Detailed error information"
  }
}
```

---

## 🔒 **Authentication**

### **JWT Token Structure**
```json
{
  "sub": "username",
  "roles": ["APP_ADMIN"],
  "iat": 1640995200,
  "exp": 1641081600
}
```

### **Authorization Header**
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📝 **Error Codes**

| Code | Description |
|------|-------------|
| `BAD_REQUEST` | Invalid request parameters |
| `UNAUTHORIZED` | Authentication required |
| `FORBIDDEN` | Insufficient permissions |
| `NOT_FOUND` | Resource not found |
| `CONFLICT` | Resource already exists |
| `VALIDATION_ERROR` | Input validation failed |
| `INTERNAL_ERROR` | Server internal error |
| `SERVICE_UNAVAILABLE` | Service temporarily unavailable |

---

## 🚀 **Rate Limiting**

### **API Rate Limits**
- **Authentication APIs**: 5 requests per minute
- **General APIs**: 100 requests per minute
- **WebSocket**: 1000 messages per minute
- **File Upload**: 10 requests per minute

---

## 📋 **Testing**

### **Postman Collection**
```json
{
  "info": {
    "name": "Kids Vehicle Tracking API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Authentication",
      "item": [
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"loginId\": \"admin\",\n  \"password\": \"password\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["api", "auth", "login"]
            }
          }
        }
      ]
    }
  ]
}
```

### **cURL Examples**
```bash
# Login
curl -X POST http://localhost:9001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginId":"admin","password":"password"}'

# Get Driver Dashboard
curl -X GET http://localhost:9001/api/drivers/1/dashboard \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Mark Attendance
curl -X POST http://localhost:9001/api/drivers/1/attendance \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"tripId":1,"studentId":1,"eventType":"PICKUP_FROM_PARENT"}'
```

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Status**: Production Ready
