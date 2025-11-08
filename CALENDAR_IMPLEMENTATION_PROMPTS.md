# 📅 Calendar & Holiday Management - Implementation Prompts

This document contains step-by-step prompts for implementing the Calendar & Holiday Management feature. Each prompt should be executed sequentially in Cursor. Follow the order carefully to ensure proper implementation.

**Reference Document**: `CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md`

---

## 🎯 Implementation Order

### Phase 1: Backend Implementation
1. Create EventType Enum
2. Create SchoolCalendar Entity
3. Create SchoolCalendarRepository
4. Create DTOs (Request/Response)
5. Create SchoolCalendarService
6. Create SchoolCalendarController
7. Test Backend APIs

### Phase 2: Frontend Implementation
8. Create Calendar Models
9. Create CalendarService
10. Create Calendar Banner Widget
11. Create Calendar Setup Page (School Admin)
12. Create Calendar View Page (All Users)
13. Integrate Calendar Banner in Dashboards
14. Integrate Trip Disable Logic (Driver Dashboard)
15. Add Menu Items for Calendar View

---

## 📋 Phase 1: Backend Implementation

### Prompt 1: Create CalendarEventType Enum

**IMPORTANT**: 
- There is already an `EventType.java` enum in `com.app.Enum` that is used for trip events (TRIP_STARTED, PICKUP_FROM_PARENT, etc.)
- Do NOT modify the existing `EventType.java` file
- Create a NEW enum file `CalendarEventType.java` specifically for calendar events

```
Please create a NEW Java enum file `CalendarEventType.java` in the package `com.app.Enum` with the following requirements:

1. **IMPORTANT**: This is a NEW enum - do NOT modify the existing `EventType.java` file
   - Existing `EventType.java` is used for trip events and should remain unchanged
   - Check existing file: `src/main/java/com/app/Enum/EventType.java` (contains TRIP_STARTED, PICKUP_FROM_PARENT, etc.)

2. Enum values:
   - HOLIDAY - Regular holidays (Sunday, National holidays, etc.) that block vehicle operations
   - SPECIAL_EVENT - Special events (Annual Function, Special Classes, etc.) that allow vehicle operations even on holidays
   - CELEBRATION_EVENT - Celebration events (Diwali Celebration, Parent Day Celebration, etc.) that are informational only
     - Does NOT affect vehicle operations (neutral)
     - Used to inform parents about school celebrations
     - Parents can see on dashboard and prepare children accordingly

3. The enum should be placed in: `src/main/java/com/app/Enum/CalendarEventType.java`

4. Include proper JavaDoc comments explaining each enum value and its purpose.

5. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "Backend Entities Required" -> CalendarEventType Enum

Example structure:
```java
package com.app.Enum;

/**
 * Enum representing the type of calendar event (separate from trip EventType)
 * This enum is specifically for school calendar events (holidays, special events, celebrations)
 */
public enum CalendarEventType {
    /**
     * Regular holidays (Sunday, National holidays, etc.)
     * Blocks vehicle operations (trips cannot start)
     */
    HOLIDAY,
    
    /**
     * Special events (Annual Function, Special Classes, etc.)
     * Allows vehicle operations even on holidays
     */
    SPECIAL_EVENT,
    
    /**
     * Celebration events (Diwali Celebration, Parent Day Celebration, etc.)
     * Informational only - does NOT affect vehicle operations
     * Used to inform parents about school celebrations
     * Parents can see on dashboard and prepare children accordingly
     */
    CELEBRATION_EVENT
}
```

6. **Verification**: 
   - Check that existing `EventType.java` is NOT modified
   - New enum should be in separate file `CalendarEventType.java`
   - Both enums can coexist in the same package

Please create this NEW file with proper formatting and documentation. Do NOT modify the existing EventType.java.
```

---

### Prompt 2: Create SchoolCalendar Entity

```
Please create a new JPA entity file `SchoolCalendar.java` in the package `com.app.entity` with the following requirements:

1. Entity Details:
   - Table name: `school_calendar`
   - Primary key: `calendarId` (Integer, Auto-generated)
   - All fields should match the requirements document

2. Fields Required:
   - `calendarId` (Integer, Primary Key, @GeneratedValue)
   - `school` (School entity, @ManyToOne, @JoinColumn, Not Null)
   - `eventDate` (LocalDate, @Column, Not Null)
   - `eventType` (CalendarEventType enum, @Enumerated(EnumType.STRING), Not Null)
   - **IMPORTANT**: Use CalendarEventType (NEW enum), NOT the existing EventType enum
   - `title` (String, Max 200, Not Null)
   - `description` (String, Max 500, Optional)
   - `allowVehicleOperations` (Boolean, Default: false for HOLIDAY, true for SPECIAL_EVENT, null/neutral for CELEBRATION_EVENT)
   - HOLIDAY → false (blocks trips)
   - SPECIAL_EVENT → true (allows trips)
   - CELEBRATION_EVENT → null/neutral (does not affect trips - informational only)
   - `isRecurring` (Boolean, Default: false)
   - `recurrencePattern` (String, Max 50, Optional)
   - `isActive` (Boolean, Default: true)
   - `createdBy` (String, Max 50)
   - `createdDate` (LocalDateTime, Auto-generated on create)
   - `updatedBy` (String, Max 50)
   - `updatedDate` (LocalDateTime, Auto-updated on modify)

3. Indexes Required:
   - `idx_school_event_date` on `(school_id, event_date)`
   - `idx_event_date_type` on `(event_date, event_type)`
   - `idx_school_date_type` on `(school_id, event_date, event_type)`

4. Unique Constraint:
   - `uk_school_date_type_title` on `(school_id, event_date, event_type, title)` - Prevent exact duplicates

5. Annotations:
   - Use Lombok annotations (@Entity, @Table, @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)
   - Use @PrePersist for createdDate
   - Use @PreUpdate for updatedDate
   - Use @Index annotations for database indexes

6. Important Notes:
   - Multiple events allowed per date (holiday + special event can coexist)
   - `allowVehicleOperations` should default based on eventType:
     - HOLIDAY → false (blocks trips)
     - SPECIAL_EVENT → true (allows trips)
   - Special events automatically allow vehicle operations, overriding holidays

7. File Location: `src/main/java/com/app/entity/SchoolCalendar.java`

8. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "Backend Entities Required" -> SchoolCalendar Entity and "Database Schema"

Please create this entity with proper JPA annotations, Lombok, and all specified fields, indexes, and constraints.
```

---

### Prompt 3: Create SchoolCalendarRepository

```
Please create a new JPA repository interface `SchoolCalendarRepository.java` in the package `com.app.repository` with the following requirements:

1. Extend: `JpaRepository<SchoolCalendar, Integer>`

2. Custom Query Methods Required:
   - `List<SchoolCalendar> findBySchoolAndEventDate(School school, LocalDate eventDate)` - Find all events for a school on a specific date
   - `List<SchoolCalendar> findBySchoolAndEventDateBetween(School school, LocalDate startDate, LocalDate endDate)` - Find events in date range
   - `List<SchoolCalendar> findBySchoolAndEventType(School school, EventType eventType)` - Find events by type
   - `List<SchoolCalendar> findBySchoolAndEventDateAndEventType(School school, LocalDate eventDate, EventType eventType)` - Find events by date and type
   - `List<SchoolCalendar> findBySchoolAndEventDateAndIsActiveTrue(School school, LocalDate eventDate)` - Find active events on a date
   - `boolean existsBySchoolAndEventDateAndEventTypeAndTitle(School school, LocalDate eventDate, EventType eventType, String title)` - Check if exact duplicate exists
   - `List<SchoolCalendar> findBySchoolOrderByEventDateAsc(School school)` - Get all events for a school ordered by date
   - `List<SchoolCalendar> findBySchoolAndIsActiveTrueOrderByEventDateAsc(School school)` - Get active events for a school

3. Use proper Spring Data JPA method naming conventions or @Query annotations.

4. File Location: `src/main/java/com/app/repository/SchoolCalendarRepository.java`

5. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "Technical Implementation Notes" -> Repository Methods

Please create this repository interface with all specified query methods.
```

---

### Prompt 4: Create DTOs (Request and Response)

```
Please create DTO classes for SchoolCalendar in the package `com.app.payload.request` and `com.app.payload.response`:

1. Request DTO: `SchoolCalendarRequestDto.java`
   - Fields:
     - `calendarId` (Integer, Optional - for updates)
     - `schoolId` (Integer, Required)
     - `eventDate` (String or LocalDate, Required) - Format: "yyyy-MM-dd"
     - `eventType` (String or CalendarEventType, Required) - "HOLIDAY", "SPECIAL_EVENT", or "CELEBRATION_EVENT"
     - `title` (String, Required, Max 200)
     - `description` (String, Optional, Max 500)
     - `allowVehicleOperations` (Boolean, Optional) - Auto-set based on eventType:
     - HOLIDAY → false
     - SPECIAL_EVENT → true
     - CELEBRATION_EVENT → null/neutral (does not affect trips)
     - `isRecurring` (Boolean, Optional, Default: false)
     - `recurrencePattern` (String, Optional) - "WEEKLY", "MONTHLY", "YEARLY"
     - `isActive` (Boolean, Optional, Default: true)
     - `createdBy` (String, Required)
     - `updatedBy` (String, Optional)
   - Use validation annotations (@NotBlank, @NotNull, @Size, @Pattern if needed)
   - Use Lombok annotations

2. Response DTO: `SchoolCalendarResponseDto.java`
   - Fields:
     - `calendarId` (Integer)
     - `schoolId` (Integer)
     - `schoolName` (String, Optional)
     - `eventDate` (String, Format: "yyyy-MM-dd")
     - `eventType` (String)
     - `title` (String)
     - `description` (String, Optional)
     - `allowVehicleOperations` (Boolean)
     - `isRecurring` (Boolean)
     - `recurrencePattern` (String, Optional)
     - `isActive` (Boolean)
     - `createdBy` (String)
     - `createdDate` (String, Format: ISO-8601)
     - `updatedBy` (String, Optional)
     - `updatedDate` (String, Optional, Format: ISO-8601)
   - Use Lombok annotations

3. Trip Status Response DTO: `TripStatusResponseDto.java`
   - Fields:
     - `canStartTrip` (Boolean) - Whether trips can start on this date
     - `isHoliday` (Boolean) - Whether date has a holiday
   - `hasSpecialEvent` (Boolean) - Whether date has a special event
   - `hasCelebrationEvent` (Boolean) - Whether date has a celebration event
   - `date` (String, Format: "yyyy-MM-dd")
   - `reason` (String, Optional) - Reason for trip status
   - `events` (List<SchoolCalendarResponseDto>) - All events on this date
   - Use Lombok annotations

4. Today Events Response DTO: `TodayEventsResponseDto.java`
   - Fields:
     - `date` (String, Format: "yyyy-MM-dd")
   - `isHoliday` (Boolean)
   - `isSpecialEvent` (Boolean)
   - `isCelebrationEvent` (Boolean) - Whether date has celebration events
   - `canStartTrip` (Boolean)
   - `events` (List<SchoolCalendarResponseDto>)
   - Use Lombok annotations

5. File Locations:
   - `src/main/java/com/app/payload/request/SchoolCalendarRequestDto.java`
   - `src/main/java/com/app/payload/response/SchoolCalendarResponseDto.java`
   - `src/main/java/com/app/payload/response/TripStatusResponseDto.java`
   - `src/main/java/com/app/payload/response/TodayEventsResponseDto.java`

6. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "API Request/Response Examples"

Please create all DTOs with proper validation, Lombok annotations, and builder pattern support.
```

---

### Prompt 5: Create SchoolCalendarService

```
Please create a service interface and implementation for SchoolCalendar in the package `com.app.service` and `com.app.service.impl`:

1. Interface: `ISchoolCalendarService.java`
   - Methods:
     - `SchoolCalendarResponseDto createCalendarEvent(SchoolCalendarRequestDto request, String createdBy)`
     - `SchoolCalendarResponseDto updateCalendarEvent(Integer calendarId, SchoolCalendarRequestDto request, String updatedBy)`
     - `void deleteCalendarEvent(Integer calendarId)`
     - `List<SchoolCalendarResponseDto> getAllEventsBySchool(Integer schoolId)`
     - `SchoolCalendarResponseDto getEventById(Integer calendarId)`
     - `TodayEventsResponseDto getTodayEvents(Integer schoolId)`
     - `TripStatusResponseDto canStartTrip(Integer schoolId, LocalDate date)`
     - `List<SchoolCalendarResponseDto> getEventsByDateRange(Integer schoolId, LocalDate startDate, LocalDate endDate)`
     - `List<SchoolCalendarResponseDto> getEventsByMonth(Integer schoolId, int year, int month)`
     - `List<SchoolCalendarResponseDto> getHolidaysByDate(Integer schoolId, LocalDate date)`
     - `List<SchoolCalendarResponseDto> getSpecialEventsByDate(Integer schoolId, LocalDate date)`
     - `boolean isHoliday(Integer schoolId, LocalDate date)`

2. Implementation: `SchoolCalendarServiceImpl.java`
   - Use @Service annotation
   - Autowire: SchoolCalendarRepository, SchoolRepository
   - Implement all interface methods
   
3. Key Logic for `canStartTrip()`:
   - Returns `true` if:
     - No events exist for the date, OR
     - Special event exists (even if holiday or celebration event also exists), OR
     - Only celebration event exists (celebration events do not affect trips)
   - Returns `false` only if:
     - Holiday exists and no special event exists (celebration events do NOT override holidays)
   - Priority: SPECIAL_EVENT > HOLIDAY
   - Celebration Events: Neutral (do NOT affect trip operations - informational only)
   - This allows trips on holidays if special events are scheduled
   - Celebration events are purely informational and do not block or allow trips

4. Key Logic for `createCalendarEvent()`:
   - Validate school exists
   - Validate no exact duplicate (school + date + type + title)
   - Set `allowVehicleOperations` based on eventType:
     - HOLIDAY → false (blocks trips)
     - SPECIAL_EVENT → true (allows trips, overrides holidays)
     - CELEBRATION_EVENT → null/neutral (does not affect trips - informational only)
   - Save entity
   - Return response DTO
   - Celebration events can coexist with holidays and special events

5. Key Logic for `getTodayEvents()`:
   - Get current date
   - Fetch all events for school on today's date
   - Determine if holiday exists (check for HOLIDAY type)
   - Determine if special event exists (check for SPECIAL_EVENT type)
   - Determine if celebration event exists (check for CELEBRATION_EVENT type)
   - Determine canStartTrip based on priority logic (celebration events are ignored)
   - Return TodayEventsResponseDto with all event types
   - Celebration events are included in response but do not affect canStartTrip

6. Error Handling:
   - Throw ResourceNotFoundException if school/calendar not found
   - Throw BadRequestException for validation errors
   - Use proper logging

7. File Locations:
   - `src/main/java/com/app/service/ISchoolCalendarService.java`
   - `src/main/java/com/app/service/impl/SchoolCalendarServiceImpl.java`

8. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "Technical Implementation Notes" -> SchoolCalendarService

Please create the service interface and implementation with all specified methods and proper error handling.
```

---

### Prompt 6: Create SchoolCalendarController

```
Please create a REST controller `SchoolCalendarController.java` in the package `com.app.controller` with the following requirements:

1. Base Mapping: `@RequestMapping("/api/calendar")`

2. Endpoints for School Admin (Calendar Setup):
   - `POST /api/school-admin/school/{schoolId}/calendar` - Create holiday/event
     - Authorization: School Admin only
     - Body: SchoolCalendarRequestDto
     - Returns: ApiResponse<SchoolCalendarResponseDto>
   
   - `PUT /api/school-admin/school/{schoolId}/calendar/{calendarId}` - Update holiday/event
     - Authorization: School Admin only
     - Body: SchoolCalendarRequestDto
     - Returns: ApiResponse<SchoolCalendarResponseDto>
   
   - `DELETE /api/school-admin/school/{schoolId}/calendar/{calendarId}` - Delete holiday/event
     - Authorization: School Admin only
     - Returns: ApiResponse<Void>
   
   - `GET /api/school-admin/school/{schoolId}/calendar` - Get all holidays/events for school
     - Authorization: School Admin only
     - Returns: ApiResponse<List<SchoolCalendarResponseDto>>
   
   - `GET /api/school-admin/school/{schoolId}/calendar/{calendarId}` - Get specific holiday/event
     - Authorization: School Admin only
     - Returns: ApiResponse<SchoolCalendarResponseDto>
   
   - `GET /api/school-admin/school/{schoolId}/calendar/today` - Get today's events for school
     - Authorization: School Admin only
     - Returns: ApiResponse<TodayEventsResponseDto>
   
   - `GET /api/school-admin/school/{schoolId}/calendar/range` - Get events for date range
     - Authorization: School Admin only
     - Query params: startDate (String, Format: "yyyy-MM-dd"), endDate (String, Format: "yyyy-MM-dd")
     - Returns: ApiResponse<List<SchoolCalendarResponseDto>>

3. Endpoints for All Users (View Calendar):
   - `GET /api/calendar/school/{schoolId}/today` - Get today's events (for any dashboard)
     - Authorization: Authenticated users
     - Returns: ApiResponse<TodayEventsResponseDto>
   
   - `GET /api/calendar/school/{schoolId}` - Get all events for school
     - Authorization: Authenticated users
     - Returns: ApiResponse<List<SchoolCalendarResponseDto>>
   
   - `GET /api/calendar/school/{schoolId}/month/{year}/{month}` - Get events for specific month
     - Authorization: Authenticated users
     - Path params: year (int), month (int, 1-12)
     - Returns: ApiResponse<List<SchoolCalendarResponseDto>>
   
   - `GET /api/calendar/school/{schoolId}/range` - Get events for date range
     - Authorization: Authenticated users
     - Query params: startDate (String), endDate (String)
     - Returns: ApiResponse<List<SchoolCalendarResponseDto>>

4. Endpoint for Driver Trip Status:
   - `GET /api/calendar/school/{schoolId}/can-start-trip/{date}` - Check if trips can start on a date
     - Authorization: Authenticated users (especially drivers)
     - Path param: date (String, Format: "yyyy-MM-dd")
     - Returns: ApiResponse<TripStatusResponseDto>
     - **Important**: This endpoint uses the priority logic (special events override holidays)

5. Use proper HTTP status codes:
   - 200 OK for successful GET requests
   - 201 Created for successful POST requests
   - 400 Bad Request for validation errors
   - 404 Not Found for missing resources
   - 403 Forbidden for unauthorized access

6. Use @RestController, @RequestMapping, @PostMapping, @GetMapping, @PutMapping, @DeleteMapping annotations

7. Add proper validation using @Valid for request bodies

8. Extract createdBy/updatedBy from authentication context (SecurityContextHolder)

9. File Location: `src/main/java/com/app/controller/SchoolCalendarController.java`

10. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "Backend API Endpoints"

Please create the controller with all specified endpoints, proper authorization, validation, and error handling.
```

---

### Prompt 7: Test Backend APIs

```
Please test the backend APIs for SchoolCalendar feature:

1. Create a test document or use Postman/curl to test all endpoints:
   - POST /api/school-admin/school/{schoolId}/calendar - Create holiday
   - POST /api/school-admin/school/{schoolId}/calendar - Create special event
   - GET /api/calendar/school/{schoolId}/today - Get today's events
   - GET /api/calendar/school/{schoolId}/can-start-trip/{date} - Check trip status
   - Test with both holiday only, special event only, and both on same date

2. Verify the priority logic:
   - Create a holiday on a date → canStartTrip should be false
   - Create a special event on the same date → canStartTrip should be true
   - Delete special event → canStartTrip should be false again

3. Verify database constraints:
   - Try creating duplicate events (same school + date + type + title) → should fail
   - Verify indexes are created properly

4. Verify authorization:
   - Test with different user roles
   - School Admin should access all endpoints
   - Other users should only access view endpoints

5. Document any issues found and fix them before proceeding to frontend implementation.

Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "API Request/Response Examples"

Please test all endpoints and verify the functionality works as expected.
```

---

## 📋 Phase 2: Frontend Implementation

### Prompt 8: Create Calendar Models

```
Please create Dart model classes for Calendar in the Flutter app:

1. Model: `school_calendar.dart` in `lib/data/models/`
   - Fields:
     - `calendarId` (int?)
     - `schoolId` (int)
     - `schoolName` (String?)
     - `eventDate` (String) - Format: "yyyy-MM-dd"
     - `eventType` (String) - "HOLIDAY", "SPECIAL_EVENT", or "CELEBRATION_EVENT"
     - `title` (String)
     - `description` (String?)
     - `allowVehicleOperations` (bool)
     - `isRecurring` (bool)
     - `recurrencePattern` (String?)
     - `isActive` (bool)
     - `createdBy` (String)
     - `createdDate` (String?)
     - `updatedBy` (String?)
     - `updatedDate` (String?)
   - Methods:
     - `fromJson(Map<String, dynamic> json)`
     - `Map<String, dynamic> toJson()`
   - Constants for event types:
     - `static const String eventTypeHoliday = 'HOLIDAY';`
     - `static const String eventTypeSpecialEvent = 'SPECIAL_EVENT';`
     - `static const String eventTypeCelebrationEvent = 'CELEBRATION_EVENT';`

2. Model: `today_events_response.dart` in `lib/data/models/`
   - Fields:
     - `date` (String)
   - `isHoliday` (bool)
   - `isSpecialEvent` (bool)
   - `isCelebrationEvent` (bool) - Whether date has celebration events
   - `canStartTrip` (bool)
   - `events` (List<SchoolCalendar>)
   - Methods:
     - `fromJson(Map<String, dynamic> json)`
     - `Map<String, dynamic> toJson()`

3. Model: `trip_status_response.dart` in `lib/data/models/`
   - Fields:
     - `canStartTrip` (bool)
     - `isHoliday` (bool)
     - `hasSpecialEvent` (bool)
     - `date` (String)
     - `reason` (String?)
     - `events` (List<SchoolCalendar>)
   - Methods:
     - `fromJson(Map<String, dynamic> json)`
     - `Map<String, dynamic> toJson()`

4. Request Model: `school_calendar_request.dart` in `lib/data/models/`
   - Fields:
     - `calendarId` (int?)
     - `schoolId` (int)
     - `eventDate` (String) - Format: "yyyy-MM-dd"
     - `eventType` (String)
     - `title` (String)
     - `description` (String?)
     - `isRecurring` (bool)
     - `recurrencePattern` (String?)
     - `isActive` (bool)
     - `createdBy` (String)
     - `updatedBy` (String?)
   - Methods:
     - `Map<String, dynamic> toJson()`

5. File Locations:
   - `lib/data/models/school_calendar.dart`
   - `lib/data/models/today_events_response.dart`
   - `lib/data/models/trip_status_response.dart`
   - `lib/data/models/school_calendar_request.dart`

6. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "API Request/Response Examples"

Please create all models with proper JSON serialization/deserialization.
```

---

### Prompt 9: Create CalendarService

```
Please create a service class `CalendarService.dart` in `lib/services/` for handling calendar API calls:

1. Class: `CalendarService`
   - Singleton pattern or use dependency injection

2. Methods Required:
   - `Future<Map<String, dynamic>> getTodayEvents(int schoolId)` - Get today's events
     - Endpoint: GET /api/calendar/school/{schoolId}/today
     - Returns: Map with success, message, data (TodayEventsResponse)
   
   - `Future<Map<String, dynamic>> getAllEvents(int schoolId)` - Get all events for school
     - Endpoint: GET /api/calendar/school/{schoolId}
     - Returns: Map with success, message, data (List<SchoolCalendar>)
   
   - `Future<Map<String, dynamic>> getMonthEvents(int schoolId, int year, int month)` - Get events for month
     - Endpoint: GET /api/calendar/school/{schoolId}/month/{year}/{month}
     - Returns: Map with success, message, data (List<SchoolCalendar>)
   
   - `Future<Map<String, dynamic>> getEventsByDateRange(int schoolId, DateTime startDate, DateTime endDate)` - Get events for date range
     - Endpoint: GET /api/calendar/school/{schoolId}/range?startDate=...&endDate=...
     - Returns: Map with success, message, data (List<SchoolCalendar>)
   
   - `Future<Map<String, dynamic>> canStartTrip(int schoolId, DateTime date)` - Check if trips can start
     - Endpoint: GET /api/calendar/school/{schoolId}/can-start-trip/{date}
     - Format date as "yyyy-MM-dd"
     - Returns: Map with success, message, data (TripStatusResponse)
   
   - `Future<Map<String, dynamic>> createEvent(SchoolCalendarRequest request)` - Create event (Admin only)
     - Endpoint: POST /api/school-admin/school/{schoolId}/calendar
     - Requires authentication token
     - Returns: Map with success, message, data (SchoolCalendar)
   
   - `Future<Map<String, dynamic>> updateEvent(int calendarId, SchoolCalendarRequest request)` - Update event (Admin only)
     - Endpoint: PUT /api/school-admin/school/{schoolId}/calendar/{calendarId}
     - Requires authentication token
     - Returns: Map with success, message, data (SchoolCalendar)
   
   - `Future<Map<String, dynamic>> deleteEvent(int schoolId, int calendarId)` - Delete event (Admin only)
     - Endpoint: DELETE /api/school-admin/school/{schoolId}/calendar/{calendarId}
     - Requires authentication token
     - Returns: Map with success, message

3. Error Handling:
   - Handle network errors
   - Handle API errors (400, 404, 403, 500)
   - Return appropriate error messages
   - Use try-catch blocks

4. Base URL:
   - Use AppConstants.baseUrl from constants.dart
   - Add authorization header from SharedPreferences (token)

5. Date Formatting:
   - Use DateFormat('yyyy-MM-dd') for date formatting
   - Parse dates from API responses

6. File Location: `lib/services/calendar_service.dart`

7. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "Technical Implementation Notes" -> CalendarService

Please create the service with all specified methods, proper error handling, and authentication.
```

---

### Prompt 10: Create Calendar Banner Widget

```
Please create a reusable widget `CalendarBannerWidget.dart` in `lib/presentation/widgets/` for displaying today's calendar events:

1. Widget: `CalendarBannerWidget`
   - StatefulWidget or StatelessWidget (use StatefulWidget if fetching data)

2. Parameters:
   - `schoolId` (int, required) - School ID to fetch events for
   - `showTripDisable` (bool, optional, default: false) - Show trip disable message (for driver dashboard)
   - `onTap` (VoidCallback?, optional) - Callback when banner is tapped (navigate to calendar)
   - `onTripStatusChange` (Function(bool)?, optional) - Callback when trip status changes (for driver)

3. Functionality:
   - Fetch today's events using CalendarService.getTodayEvents()
   - **IMPORTANT**: Use existing dashboard refresh mechanism - do NOT create new auto-refresh timers
   - Display banner based on event types:
     - **Holiday only**: Orange/Red background, "🎉 Holiday: [Title]"
     - **Special Event only**: Blue/Purple background, "🎊 Event: [Title]"
     - **Celebration Event only**: Green/Pink background, "🎈 Celebration: [Title]" (prominently displayed)
     - **Multiple events**: Combined display with all event types
       - "🎉 Holiday: [Title] | 🎊 Event: [Title] | 🎈 Celebration: [Title]"
     - **No events**: Gray background, "📅 Today: [Date]"
   - Show trip disable message if showTripDisable is true and canStartTrip is false
   - Show trip enable message if special event exists
   - Celebration events are always displayed (informational for parents)
   - Celebration events do NOT affect trip operations (neutral)

4. Design Specifications:
   - Height: 40-50px
   - Full width
   - Single line display (responsive: stack on small screens)
   - Font size: 14-16px
   - Font weight: Medium (500)
   - Padding: 12px horizontal, 10px vertical
   - Color scheme:
     - Holiday: Background #FF6B6B or #FFA500, White text, Icon 🎉 or 🚫
     - Special Event: Background #4ECDC4 or #9B59B6, White text, Icon 🎊
     - Celebration Event: Background #4CAF50 or #E91E63, White text, Icon 🎈 (prominently displayed)
     - Normal: Background #E8E8E8, Dark text #333333, Icon 📅

5. Loading State:
   - Show loading indicator while fetching events
   - Handle errors gracefully

6. Refresh Mechanism:
   - **IMPORTANT**: Do NOT create new auto-refresh timers
   - Use existing dashboard refresh patterns:
     - Refresh on widget init (when dashboard loads)
     - Refresh when Bloc state changes (if integrated with Bloc)
     - Refresh on app resume (if existing dashboard has this)
     - Refresh via WebSocket notifications (if existing dashboard uses WebSocket)
   - Do NOT create Timer.periodic or similar refresh mechanisms
   - Follow existing dashboard refresh patterns in the codebase
   - Check existing dashboard files for refresh patterns before implementing

7. File Location: `lib/presentation/widgets/calendar_banner_widget.dart`

8. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "UI/UX Design Specifications" -> Calendar Banner Component

Please create the widget with proper state management, error handling, and responsive design.
```

---

### Prompt 11: Create Calendar Setup Page (School Admin)

```
Please create a calendar setup page `calendar_setup_page.dart` in `lib/presentation/pages/` for School Admin:

1. Page: `CalendarSetupPage`
   - StatefulWidget
   - Accessible from School Admin Dashboard menu

2. Features Required:
   - **Calendar Grid View**:
     - Monthly calendar display (7 days × 4-5 weeks)
     - Show current month by default
     - Navigation: Previous/Next month buttons
     - Today's date highlighted
     - Holidays marked with colored dot/background (red/orange)
     - Special events marked with different colored dot (blue/purple)
     - Celebration events marked with different colored dot (green/pink)
     - Dates with multiple events show all indicators
     - Celebration events prominently displayed (important for parents)
   
   - **Add/Edit Event Dialog**:
     - Triggered by clicking on a date
     - Fields:
       - Date (Date Picker, pre-filled from calendar click)
       - Event Type (Dropdown: Holiday / Special Event / Celebration Event)
         - **Holiday**: Blocks vehicle operations
         - **Special Event**: Allows vehicle operations (e.g., Annual Function, Special Classes)
         - **Celebration Event**: Informational only - does NOT affect vehicle operations (e.g., Diwali Celebration, Parent Day Celebration)
       - Title (Text Input, required) - e.g., "Mahatma Gandhi Jayanti", "Sunday", "Annual Function", "Diwali Celebration", "Parent Day"
       - Description (Text Area, optional)
       - Is Recurring (Checkbox)
       - Recurrence Pattern (Dropdown, shown if recurring):
         - Weekly
         - Monthly
         - Yearly
       - Allow Vehicle Operations (Info/Checkbox - auto-set based on type)
     - Actions: Save, Cancel, Delete (if editing)
     - Validation: Date and Title required
     - Show error if exact duplicate exists
   
   - **Bulk Operations**:
     - Button: "Bulk Operations"
     - Options:
       - Mark All Sundays as Holidays
       - Mark All Saturdays as Holidays
       - Mark All Weekends as Holidays
     - Date range selector (start date, end date)
     - Confirmation dialog
     - Show progress indicator during bulk creation
   
   - **Event List View** (Optional):
     - List of all events for the month
     - Filter by event type
     - Search by title
     - Click to edit/delete

3. State Management:
   - Fetch events for current month on page load
   - Refresh after create/update/delete
   - Use CalendarService for all API calls
   - Show loading indicators
   - Handle errors with SnackBar
   - **IMPORTANT**: Do NOT create new auto-refresh timers
   - Use manual refresh (user-initiated) or existing refresh patterns

4. UI Components:
   - AppBar with title "Calendar Setup"
   - Calendar grid widget (can use `table_calendar` package or custom)
   - FloatingActionButton for "Add Event"
   - Event dialog/form
   - Bulk operations dialog

5. File Location: `lib/presentation/pages/calendar_setup_page.dart`

6. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "School Admin Dashboard" -> Calendar Setup Feature

Please create the page with all specified features, proper state management, and error handling.
```

---

### Prompt 12: Create Calendar View Page (All Users)

```
Please create a calendar view page `calendar_view_page.dart` in `lib/presentation/pages/` for viewing calendar (read-only):

1. Page: `CalendarViewPage`
   - StatefulWidget
   - Accessible from all dashboard menus (read-only)

2. Features Required:
   - **Calendar Grid View**:
     - Monthly calendar display
     - Navigation: Previous/Next month buttons
     - Today's date highlighted
   - Holidays marked with colored indicator (red/orange)
   - Special events marked with different colored indicator (blue/purple)
   - Celebration events marked with different colored indicator (green/pink)
   - Click date to view event details
   - Celebration events prominently displayed for parents
   
   - **Event Details**:
     - Modal/Dialog showing event details when date is clicked
     - Display: Date, Type, Title, Description, Recurrence info
     - Read-only (no edit/delete options)
   
   - **Filters** (Optional):
     - Filter by event type (All / Holidays / Special Events)
     - Filter by month/year
     - Search by title
   
   - **Event List View** (Optional):
     - List of events for selected month
     - Grouped by date
     - Click to view details

3. State Management:
   - Fetch events for selected month
   - Update when month changes
   - Use CalendarService for API calls
   - Show loading indicators
   - Handle errors gracefully
   - **IMPORTANT**: Do NOT create new auto-refresh timers
   - Use manual refresh or existing refresh patterns

4. UI Components:
   - AppBar with title "School Calendar"
   - Calendar grid widget
   - Event details dialog
   - Filter options (if implemented)

5. File Location: `lib/presentation/pages/calendar_view_page.dart`

6. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "All Dashboards" -> View Calendar Menu Option

Please create the page with calendar display, event details, and proper state management.
```

---

### Prompt 13: Integrate Calendar Banner in Dashboards

```
Please integrate the CalendarBannerWidget into all dashboards:

1. **School Admin Dashboard** (`bloc_school_admin_dashboard.dart`):
   - Add CalendarBannerWidget below AppBar, above main content
   - Pass schoolId from auth state
   - Show both holidays and special events
   - Add onTap callback to navigate to Calendar Setup page

2. **Driver Dashboard** (`bloc_driver_dashboard.dart`):
   - Add CalendarBannerWidget below AppBar, above trip section
   - Pass schoolId from driver's school
   - Set showTripDisable = true
   - Add onTripStatusChange callback to disable/enable trip section
   - Banner should show trip disable message if holiday exists without special event

3. **Parent Dashboard** (`bloc_parent_dashboard.dart`):
   - Add CalendarBannerWidget below AppBar, above main content
   - Get schoolId from student's school (from dashboard data)
   - Informational only (no trip disable logic)
   - **Celebration events should be prominently displayed** for parents
   - Show message for celebration events: "School celebration today - Prepare your child accordingly"
   - Add onTap callback to navigate to Calendar View page
   - **IMPORTANT**: Use existing dashboard refresh mechanism (Bloc events) - do NOT create new refresh timers

4. **Gate Staff Dashboard** (`bloc_gate_staff_dashboard.dart`):
   - Add CalendarBannerWidget below AppBar
   - Get schoolId from gate staff's school
   - Informational only
   - Add onTap callback to navigate to Calendar View page

5. **Vehicle Owner Dashboard** (`bloc_vehicle_owner_dashboard.dart`):
   - Add CalendarBannerWidget below AppBar
   - Get schoolId from associated school(s)
   - If multiple schools, show holidays for all (or primary school)
   - Informational only
   - Add onTap callback to navigate to Calendar View page

6. Implementation:
   - Import CalendarBannerWidget
   - Add widget in build method at appropriate location
   - Handle schoolId extraction from dashboard data
   - Add navigation to calendar pages
   - **IMPORTANT**: 
     - Do NOT create new auto-refresh mechanisms
     - Use existing dashboard refresh patterns (Bloc events, WebSocket notifications)
     - Ensure existing functionality is not broken
     - Reuse existing code patterns and refresh mechanisms
   - Test banner display and functionality
   - Verify existing dashboard features still work correctly

7. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "Frontend Flow & Functionality" for each dashboard

Please integrate the calendar banner into all specified dashboards with proper schoolId and callbacks.
```

---

### Prompt 14: Integrate Trip Disable Logic (Driver Dashboard)

```
Please implement trip disable logic in Driver Dashboard based on calendar events:

1. **Location**: `bloc_driver_dashboard.dart`

2. **Logic Implementation**:
   - On dashboard load, call `CalendarService.canStartTrip(schoolId, today)`
   - **IMPORTANT**: Use existing dashboard refresh mechanism - do NOT create new refresh timers
   - Store trip status in state: `bool _canStartTrip = true;`
   - Update `_canStartTrip` based on API response:
     - If `canStartTrip: false` → Set `_canStartTrip = false` (holiday exists, no special event)
     - If `canStartTrip: true` → Set `_canStartTrip = true` (no holiday, or special event exists)
     - Celebration events do NOT affect `_canStartTrip` (informational only)
   
3. **Trip Section Behavior**:
   - **If `_canStartTrip = false`** (holiday without special event):
     - Disable "Start Trip" button (grayed out, non-clickable)
     - Add tooltip: "Trips cannot be started on holidays"
     - Gray out entire trip card/section
     - Show overlay message: "Today is a holiday. Trip operations are not available."
     - Disable all trip-related actions
   
   - **If `_canStartTrip = true`** (normal day or special event exists):
     - Enable "Start Trip" button
     - Show info message if special event exists: "Special event today - Trips allowed"
     - Enable all trip operations as normal
   
4. **Integration with CalendarBannerWidget**:
   - Use onTripStatusChange callback from CalendarBannerWidget
   - Update `_canStartTrip` when banner receives trip status
   - Sync trip section state with calendar status

5. **UI Updates**:
   - Conditionally disable/enable Start Trip button based on `_canStartTrip`
   - Show/hide overlay message
   - Change button styling (enabled/disabled states)
   - Update trip card appearance

6. **Error Handling**:
   - If API call fails, default to allowing trips (fail-safe)
   - Show error message in banner if calendar check fails
   - Log errors for debugging

7. **Testing**:
   - Test with holiday only (trips disabled)
   - Test with special event on holiday (trips enabled)
   - Test with no events (trips enabled)
   - Test API failure scenario

8. File Location: `lib/presentation/pages/bloc_driver_dashboard.dart`

9. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "Driver Dashboard" -> Trip Section Behavior

10. **IMPORTANT Notes**:
    - Do NOT create new auto-refresh timers
    - Use existing dashboard refresh mechanism (Bloc events, WebSocket notifications)
    - Ensure existing trip functionality is not broken
    - Celebration events do NOT affect trip operations (informational only)
    - Test that existing driver dashboard features still work correctly

Please implement the trip disable logic with proper state management and UI updates, ensuring existing functionality is preserved.
```

---

### Prompt 15: Add Menu Items for Calendar View

```
Please add menu items for "View Calendar" in all dashboard drawer menus:

1. **School Admin Dashboard** (`bloc_school_admin_dashboard.dart`):
   - Add menu item: "Calendar Setup" (navigates to CalendarSetupPage)
   - Add menu item: "View Calendar" (navigates to CalendarViewPage)
   - Place in appropriate section of drawer menu

2. **Driver Dashboard** (`bloc_driver_dashboard.dart`):
   - Add menu item: "School Calendar" (navigates to CalendarViewPage)
   - Place in drawer menu

3. **Parent Dashboard** (`bloc_parent_dashboard.dart`):
   - Add menu item: "School Calendar" (navigates to CalendarViewPage)
   - Place in drawer menu

4. **Gate Staff Dashboard** (`bloc_gate_staff_dashboard.dart`):
   - Add menu item: "School Calendar" (navigates to CalendarViewPage)
   - Place in drawer menu

5. **Vehicle Owner Dashboard** (`bloc_vehicle_owner_dashboard.dart`):
   - Add menu item: "School Calendar" (navigates to CalendarViewPage)
   - Place in drawer menu

6. **App Admin Dashboard**:
   - No changes (as per requirements)

7. **Navigation**:
   - Use Navigator.pushNamed with route to calendar pages
   - Or use Navigator.push with MaterialPageRoute
   - Add routes to app_routes.dart if using named routes

8. **Menu Item Icons**:
   - Use appropriate icons (Calendar icon for calendar items)
   - Match existing menu item styling

9. **File Locations**:
   - Update all dashboard files in `lib/presentation/pages/`
   - Update `lib/app_routes.dart` if adding named routes

10. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "Menu Option: View Calendar" for each dashboard

Please add menu items to all specified dashboards with proper navigation and styling.
```

---

## 🧪 Testing & Validation

### Prompt 16: Test Complete Feature

```
Please test the complete Calendar & Holiday Management feature:

1. **Backend Testing**:
   - Test all API endpoints (create, update, delete, get)
   - Test priority logic (special events override holidays)
   - Test trip status endpoint with different scenarios
   - Verify database constraints and indexes

2. **Frontend Testing**:
   - Test Calendar Setup page (School Admin):
     - Create holiday
     - Create special event
     - Create both on same date
     - Edit events
     - Delete events
     - Bulk operations (mark all Sundays)
   
   - Test Calendar View page (All users):
     - View calendar
     - Click dates to see events
     - Navigate months
     - Filter events (if implemented)
   
   - Test Calendar Banner on all dashboards:
     - School Admin: Shows today's events
     - Driver: Shows events, disables trips on holidays
     - Parent: Shows events (informational)
     - Gate Staff: Shows events (informational)
     - Vehicle Owner: Shows events (informational)
   
   - Test Driver Dashboard Trip Disable:
     - Holiday only → Trip button disabled
     - Holiday + Special Event → Trip button enabled
     - Special Event only → Trip button enabled
     - No events → Trip button enabled

3. **Integration Testing**:
   - Test end-to-end flow: Create holiday → View on dashboard → Driver cannot start trip
   - Test end-to-end flow: Create special event on holiday → Driver can start trip
   - Test menu navigation to calendar pages
   - Test banner navigation to calendar pages

4. **Edge Cases**:
   - Multiple events on same date (holiday + special event + celebration)
   - Celebration event on holiday (trips should still be blocked)
   - Celebration event with special event (trips should be allowed)
   - Only celebration event (trips should be allowed)
   - Recurring events
   - Date range queries
   - Invalid dates
   - Network errors
   - Unauthorized access
   - Existing functionality not broken

5. **Performance Testing**:
   - Test with large number of events
   - Test calendar rendering performance
   - Test API response times

6. **Documentation**:
   - Document any issues found
   - Document configuration needed
   - Update user guide if needed

7. Reference: CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md section "Testing Checklist"

Please test all functionality and document any issues or improvements needed.
```

---

## 📝 Notes for Implementation

1. **Order**: Execute prompts in sequence. Each prompt builds on the previous one.

2. **Existing Code**: 
   - **Do NOT modify existing `EventType.java`** - Create NEW `CalendarEventType.java` enum
   - Check existing code structure before creating new files
   - Reuse existing patterns and mechanisms (refresh, state management, etc.)

3. **Do NOT Break Existing Functionality**:
   - Ensure current dashboard features continue to work
   - Do NOT create duplicate refresh mechanisms
   - Use existing auto-refresh patterns (Bloc events, WebSocket notifications)
   - Test that existing features are not affected

4. **Auto-Refresh**:
   - **IMPORTANT**: Dashboards already have auto-refresh via Bloc events and WebSocket
   - Do NOT create new Timer.periodic or auto-refresh mechanisms
   - Use existing refresh patterns in the codebase
   - Calendar banner should refresh when dashboard refreshes (via existing mechanisms)

5. **Celebration Events**:
   - New event type: CELEBRATION_EVENT
   - Informational only - does NOT affect vehicle operations
   - Prominently displayed for parents
   - Helps parents prepare children for school celebrations (Diwali, Parent Day, etc.)

6. **Testing**: Test after each major step (especially after backend and frontend service creation).

7. **Error Handling**: Ensure proper error handling at each step.

8. **Validation**: Validate all inputs on both backend and frontend.

9. **Authorization**: Ensure proper role-based access control.

10. **Performance**: Consider caching for frequently accessed data (e.g., today's events).

11. **Documentation**: Keep the requirements document handy for reference.

12. **Dependencies**: Install any required packages (e.g., `table_calendar` for Flutter calendar widget).

---

## 🔗 Related Files

- Requirements Document: `CALENDAR_HOLIDAY_MANAGEMENT_REQUIREMENTS.md`
- Backend Entity: `SchoolCalendar.java`
- Backend Service: `SchoolCalendarServiceImpl.java`
- Backend Controller: `SchoolCalendarController.java`
- Frontend Service: `calendar_service.dart`
- Frontend Widget: `calendar_banner_widget.dart`
- Frontend Pages: `calendar_setup_page.dart`, `calendar_view_page.dart`

---

**Implementation Order**: Follow prompts 1-16 in sequence.

**Version**: 1.0
**Last Updated**: Current Date

