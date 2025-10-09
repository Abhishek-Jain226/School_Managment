# WebSocket + Database Notification Integration

## 🎯 Overview
WebSocket notifications ab database ke saath integrate ho gaye hain. Jab bhi koi notification send hoti hai, wo dono tarah se jayegi:
1. **Real-time WebSocket** - Instant delivery to connected users
2. **Database Storage** - Permanent record in `notifications` table

## 📊 Database Structure

### Existing `notifications` Table
```sql
CREATE TABLE notifications (
    notification_log_id INT PRIMARY KEY AUTO_INCREMENT,
    dispatch_log_id INT NOT NULL,
    notification_type ENUM('SMS', 'PUSH', 'EMAIL') NOT NULL,
    is_sent BOOLEAN DEFAULT FALSE,
    sent_at DATETIME,
    error_msg VARCHAR(255),
    created_by VARCHAR(50),
    created_date DATETIME,
    updated_by VARCHAR(50),
    updated_date DATETIME,
    FOREIGN KEY (dispatch_log_id) REFERENCES dispatch_log(dispatch_log_id)
);
```

## 🔄 How It Works

### 1. WebSocket Only (Real-time)
```java
// Sirf WebSocket pe bhejte hain, database mein save nahi karte
webSocketNotificationService.sendTripUpdateNotification(tripId, message, type);
```

### 2. WebSocket + Database (Recommended)
```java
// Dono WebSocket aur database mein save karte hain
webSocketNotificationService.sendTripUpdateWithDatabase(
    tripId, message, type, dispatchLogId, createdBy
);
```

## 🚀 API Endpoints

### WebSocket Only Endpoints
- `POST /api/notifications/websocket/trip-update`
- `POST /api/notifications/websocket/arrival`
- `POST /api/notifications/websocket/pickup`
- `POST /api/notifications/websocket/drop`

### WebSocket + Database Endpoints
- `POST /api/notifications/websocket/trip-update-with-db`
- `POST /api/notifications/websocket/arrival-with-db`
- `POST /api/notifications/websocket/pickup-with-db`
- `POST /api/notifications/websocket/drop-with-db`

## 📝 Usage Examples

### 1. Trip Update with Database Logging
```bash
curl -X POST "http://localhost:9001/api/notifications/websocket/trip-update-with-db" \
  -d "tripId=1&message=Vehicle is running 10 minutes late&dispatchLogId=1&createdBy=Driver123"
```

### 2. Arrival Notification with Database Logging
```bash
curl -X POST "http://localhost:9001/api/notifications/websocket/arrival-with-db" \
  -d "tripId=1&vehicleId=1&location=School Gate&dispatchLogId=1&createdBy=Driver123"
```

### 3. Pickup Confirmation with Database Logging
```bash
curl -X POST "http://localhost:9001/api/notifications/websocket/pickup-with-db" \
  -d "tripId=1&studentId=1&studentName=John Doe&dispatchLogId=1&createdBy=Driver123"
```

## 🧪 Testing

### 1. Open Test Page
```
http://localhost:9001/websocket-test.html
```

### 2. Test Options
- **Save to Database**: Checkbox to enable/disable database saving
- **Dispatch Log ID**: Required for database integration
- **Created By**: User who created the notification

### 3. Test Flow
1. Click "Connect" to establish WebSocket connection
2. Fill in test data
3. Check "Save to Database" if you want to save to database
4. Click "Send Test Notification"
5. Check both WebSocket delivery and database record

## 📋 Database Queries

### Check Notifications in Database
```sql
SELECT 
    n.notification_log_id,
    n.notification_type,
    n.is_sent,
    n.sent_at,
    n.message,
    n.created_by,
    n.created_date
FROM notifications n
ORDER BY n.created_date DESC;
```

### Check Dispatch Logs
```sql
SELECT 
    dl.dispatch_log_id,
    dl.created_by,
    dl.created_date
FROM dispatch_log dl
ORDER BY dl.created_date DESC;
```

## 🔧 Integration in Your Code

### In Service Classes
```java
@Autowired
private IWebSocketNotificationService webSocketNotificationService;

// Send notification with database logging
public void notifyTripUpdate(Integer tripId, String message) {
    webSocketNotificationService.sendTripUpdateWithDatabase(
        tripId, 
        message, 
        "TRIP_UPDATE", 
        getCurrentDispatchLogId(), 
        getCurrentUser()
    );
}
```

### In Controllers
```java
@PostMapping("/trip/update")
public ResponseEntity<ApiResponse> updateTrip(@RequestBody TripUpdateRequest request) {
    // Your trip update logic here
    
    // Send notification with database logging
    webSocketNotificationService.sendTripUpdateWithDatabase(
        request.getTripId(),
        "Trip status updated",
        "TRIP_UPDATE",
        request.getDispatchLogId(),
        getCurrentUser()
    );
    
    return ResponseEntity.ok(new ApiResponse(true, "Trip updated successfully", null));
}
```

## 📊 Benefits

### 1. **Real-time Delivery**
- Instant notifications via WebSocket
- No page refresh needed
- Live updates on all dashboards

### 2. **Permanent Record**
- All notifications saved in database
- Audit trail for compliance
- Historical data for analytics

### 3. **Reliability**
- If WebSocket fails, database record still exists
- Can resend notifications from database
- Offline users can see notifications when they come online

### 4. **Analytics**
- Track notification delivery rates
- Monitor system usage
- Generate reports

## 🎯 Next Steps

1. **Test the Integration**
   - Use the test HTML page
   - Verify WebSocket delivery
   - Check database records

2. **Integrate with Frontend**
   - Add WebSocket client to Flutter
   - Update dashboard pages
   - Add notification UI components

3. **Production Deployment**
   - Configure proper CORS settings
   - Set up SSL for WebSocket
   - Monitor performance

## 🔍 Troubleshooting

### WebSocket Connection Issues
- Check if port 9001 is accessible
- Verify CORS settings
- Check browser console for errors

### Database Issues
- Ensure `dispatch_log` table has required records
- Check database connection
- Verify foreign key constraints

### Notification Not Received
- Check WebSocket connection status
- Verify subscription to correct topics
- Check server logs for errors

## 📞 Support

Agar koi issue aaye to check karein:
1. Server logs
2. Browser console
3. Database records
4. WebSocket connection status

Database mein jo pehle se `notifications` table bani hai, usi mein data jayega. WebSocket sirf real-time delivery ke liye hai, permanent record database mein hi save hota hai.
