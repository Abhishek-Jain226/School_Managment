# 📅 Calendar & Holiday Management Feature - Requirements & Flow

## 📋 Overview

This feature allows schools to manage their academic calendar by setting holidays and special events. These holidays and events are school-specific and will be displayed across all relevant dashboards. 

**Key Functionality:**
- **Holidays**: Block vehicle operations (trips) on holidays (e.g., Sunday, National holidays)
- **Special Events**: Allow vehicle operations even on holidays if a special event is scheduled (e.g., Annual Function, Special Classes)
- **Celebration Events**: Informational only - do NOT affect vehicle operations (e.g., Diwali Celebration, Parent Day Celebration)
  - Display on dashboards to inform parents about school celebrations
  - Parents can see and prepare children accordingly
  - Does not block or allow trips (neutral)
- **Priority Logic**: If both a holiday and special event exist on the same date, special events take priority and trips are allowed
- **Celebration events** can coexist with any other events and are purely informational
- All users can view the calendar across all dashboards

**Important Implementation Notes:**
1. **Do NOT create duplicate refresh mechanisms** - Dashboards already have auto-refresh via Bloc events and WebSocket notifications
2. **Do NOT modify existing EventType enum** - Create a NEW `CalendarEventType` enum for calendar events
3. **Ensure existing functionality is not broken** - Calendar feature should integrate seamlessly without affecting current features
4. **Reuse existing code patterns** - Follow existing dashboard refresh patterns (Bloc events, WebSocket notifications)

---

## 🏗️ Architecture & Data Model

### Backend Entities Required

#### 1. SchoolCalendar (New Entity)
**Purpose**: Store holidays and events for each school

**Fields**:
- `calendarId` (Integer, Primary Key, Auto-generated)
- `school` (School, ManyToOne, Not Null) - Which school this calendar belongs to
- `eventDate` (LocalDate, Not Null) - Date of the holiday/event
- `eventType` (Enum: CalendarEventType - HOLIDAY, SPECIAL_EVENT, CELEBRATION_EVENT) - Type of event
  - **HOLIDAY**: Blocks vehicle operations (trips cannot start)
  - **SPECIAL_EVENT**: Allows vehicle operations even on holidays (e.g., Annual Function, Special Classes)
  - **CELEBRATION_EVENT**: Informational only - does NOT affect vehicle operations (e.g., Diwali Celebration, Parent Day Celebration)
    - Used to inform parents about school celebrations
    - Parents can see on dashboard and prepare children accordingly
    - Does not block or allow trips (neutral - follows holiday status if holiday exists)
- `description` (String, Optional, Max 500) - Additional details about the event
- `allowVehicleOperations` (Boolean, Default: false for HOLIDAY, true for SPECIAL_EVENT, null/neutral for CELEBRATION_EVENT) - Whether vehicle operations are allowed on this date
  - **HOLIDAY**: false (blocks trips)
  - **SPECIAL_EVENT**: true (allows trips, overrides holidays)
  - **CELEBRATION_EVENT**: null/neutral (does not affect trips - informational only)
  - **Note**: Special events automatically allow vehicle operations, overriding any holidays on the same date
  - **Note**: Celebration events are purely informational and do not affect vehicle operations at all
- `isRecurring` (Boolean, Default: false) - Whether this is a recurring event (e.g., every Sunday)
- `recurrencePattern` (String, Optional) - Pattern for recurrence (e.g., "WEEKLY", "YEARLY", "MONTHLY")
- `isActive` (Boolean, Default: true) - Whether this event is active
- `createdBy` (String, Max 50) - Who created this event
- `createdDate` (LocalDateTime, Auto-generated)
- `updatedBy` (String, Max 50)
- `updatedDate` (LocalDateTime, Auto-generated)

**Important Notes**:
- A date can have **multiple events** (holiday + special event + celebration event can all coexist)
- When both holiday and special event exist, **special events take priority** for trip operations
- Special events allow vehicle operations even if the date is also marked as a holiday
- This allows schools to conduct special classes or annual functions on Sundays/holidays
- **Celebration events** (e.g., Diwali Celebration, Parent Day) are **informational only**:
  - Do NOT affect vehicle operations (trips follow holiday/special event status)
  - Display on all dashboards to inform users (especially parents)
  - Parents can see celebrations and prepare children accordingly
  - Can coexist with holidays and special events on the same date

**Indexes**:
- `idx_school_event_date` on `(school_id, event_date)`
- `idx_event_date_type` on `(event_date, event_type)`
- `idx_school_date_type` on `(school_id, event_date, event_type)` - For efficient trip status checks

**Constraints**:
- Multiple events allowed per date (holiday + special event can coexist)
- Unique constraint: `(school_id, event_date, event_type, title)` - Prevent exact duplicates
- Special events automatically allow vehicle operations

#### 2. CalendarEventType (Enum) - NEW ENUM for Calendar Events
**Important**: There is already an `EventType` enum in `com.app.Enum.EventType` that is used for trip events. We need a NEW enum specifically for calendar events.

```java
package com.app.Enum;

/**
 * Enum representing the type of calendar event (separate from trip EventType)
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
     * Celebration events (Diwali Celebration, Parent Day, etc.)
     * Informational only - does NOT affect vehicle operations
     * Used to inform parents about school celebrations
     * Parents can prepare children accordingly
     */
    CELEBRATION_EVENT
}
```

**Note**: This is a NEW enum file `CalendarEventType.java` - do NOT modify the existing `EventType.java` which is used for trip events.

---

## 🔄 Backend API Endpoints

### SchoolCalendarController

#### 1. Calendar Setup (School Admin Only)
- **POST** `/api/school-admin/school/{schoolId}/calendar` - Create holiday/event
- **PUT** `/api/school-admin/school/{schoolId}/calendar/{calendarId}` - Update holiday/event
- **DELETE** `/api/school-admin/school/{schoolId}/calendar/{calendarId}` - Delete holiday/event
- **GET** `/api/school-admin/school/{schoolId}/calendar` - Get all holidays/events for school
- **GET** `/api/school-admin/school/{schoolId}/calendar/{calendarId}` - Get specific holiday/event
- **GET** `/api/school-admin/school/{schoolId}/calendar/today` - Get today's events for school
- **GET** `/api/school-admin/school/{schoolId}/calendar/range` - Get events for date range
  - Query params: `startDate`, `endDate`

#### 2. View Calendar (All Users)
- **GET** `/api/calendar/school/{schoolId}/today` - Get today's events (for any dashboard)
- **GET** `/api/calendar/school/{schoolId}` - Get all events for school
- **GET** `/api/calendar/school/{schoolId}/month/{year}/{month}` - Get events for specific month
- **GET** `/api/calendar/school/{schoolId}/range` - Get events for date range
  - Query params: `startDate`, `endDate`

#### 3. Check Holiday/Trip Status (For Driver Trip Start)
- **GET** `/api/calendar/school/{schoolId}/is-holiday/{date}` - Check if specific date is a holiday
  - Returns: `{ "isHoliday": true/false, "event": { ... } }`
- **GET** `/api/calendar/school/{schoolId}/can-start-trip/{date}` - Check if trips can be started on a date
  - **Logic**: Returns `true` if:
    - No events exist for the date, OR
    - Special event exists (even if holiday or celebration event also exists), OR
    - Only celebration event exists (celebration events do not affect trips)
  - Returns `false` if:
    - Only holiday exists (celebration events do NOT override holidays)
  - Returns: `{ "canStartTrip": true/false, "isHoliday": true/false, "hasSpecialEvent": true/false, "hasCelebrationEvent": true/false, "events": [...] }`
  - **Priority**: Special events override holidays for trip operations
  - **Celebration Events**: Do NOT affect trip operations (neutral - informational only)

---

## 📊 Frontend Flow & Functionality

### 1. School Admin Dashboard

#### 1.1 Calendar Setup Feature

**Location**: New menu item "Calendar Setup" in School Admin Dashboard

**Functionality**:
1. **Calendar Setup Page**:
   - **View Mode**: Calendar grid view showing all months
   - **Holiday Marking**: Click on any date to add/edit holiday
   - **Event Marking**: Different color for special events vs holidays
   - **Bulk Operations**:
     - Mark all Sundays as holidays
     - Mark all Saturdays as holidays (if applicable)
     - Import holidays from CSV/Excel
   - **Recurring Events**: 
     - Option to mark recurring holidays (e.g., every Sunday)
     - Yearly recurring events (e.g., Independence Day)
   - **Event Management**:
     - Add new holiday/event
     - Edit existing holiday/event
     - Delete holiday/event
     - Activate/Deactivate holiday/event

2. **Add/Edit Holiday Dialog**:
   - **Fields**:
     - Date (Date Picker) - Pre-filled if clicked from calendar
     - Event Type (Dropdown: Holiday / Special Event / Celebration Event)
       - **Holiday**: Blocks vehicle operations
       - **Special Event**: Allows vehicle operations (e.g., Annual Function, Special Classes)
       - **Celebration Event**: Informational only - does NOT affect vehicle operations (e.g., Diwali Celebration, Parent Day Celebration)
     - Title (Text Input) - e.g., "Mahatma Gandhi Jayanti", "Sunday", "Annual Function", "Special Classes", "Diwali Celebration", "Parent Day Celebration"
     - Description (Optional Text Area) - Additional details
     - **Allow Vehicle Operations** (Checkbox/Info):
       - For Holiday: Disabled/Unchecked (holidays block trips)
       - For Special Event: Enabled/Checked (special events allow trips)
       - For Celebration Event: Disabled/Neutral (does not affect trips - informational only)
       - **Note**: Special events automatically allow trips, even if date also has a holiday
       - **Note**: Celebration events are purely informational and do not affect vehicle operations
     - Is Recurring (Checkbox)
     - Recurrence Pattern (Dropdown, shown if recurring):
       - Weekly (e.g., every Sunday)
       - Monthly (e.g., first Monday of month)
       - Yearly (e.g., same date every year)
   - **Validation**:
     - Date is required
     - Title is required
     - **Multiple events allowed**: A date can have both a holiday AND a special event
     - **Example**: Sunday (Holiday) + Annual Function (Special Event) on same date
     - Duplicate events of same type on same date are not allowed (unless different titles)
   - **Special Scenarios**:
     - **Sunday + Annual Function**: Mark Sunday as Holiday, then mark same date as Special Event (Annual Function)
     - **Holiday + Special Classes**: Mark holiday, then add Special Event for special classes
     - **Diwali Celebration**: Mark as Celebration Event (informational for parents, does not affect trips)
     - **Parent Day Celebration**: Mark as Celebration Event (parents can prepare children)
     - **Holiday + Celebration**: Mark holiday, then add Celebration Event (shows both, trips still blocked by holiday)
     - System will show all events, but trip operations follow holiday/special event priority
     - Celebration events are always displayed but do not affect trip operations
   - **Actions**:
     - Save
     - Cancel
     - Delete (if editing)

3. **Calendar View Page**:
   - **Display**:
     - Monthly calendar grid
     - Holidays marked in red/orange
     - Special events marked in blue/purple
     - Today's date highlighted
   - **Filters**:
     - Filter by event type (All / Holidays / Special Events)
     - Filter by month/year
     - Search by title
   - **Actions**:
     - Add new event (button)
     - Click date to view/edit events
     - Export calendar (PDF/Excel)

#### 1.2 Dashboard Display

**Location**: Top of School Admin Dashboard (below header, above main content)

**Display**:
```
┌─────────────────────────────────────────────────────────────┐
│ 📅 Today: [Current Date] | 🎉 Holiday: [Holiday Name]      │
│    Example: "Today: 02 October 2024 | 🎉 Holiday: Mahatma   │
│    Gandhi Jayanti"                                          │
└─────────────────────────────────────────────────────────────┘
```

**Logic**:
1. On dashboard load, call API: `GET /api/calendar/school/{schoolId}/today`
   - **Important**: Use existing dashboard refresh mechanism (Bloc events, WebSocket) - do NOT create new refresh timers
2. Check if today has any events:
   - If holiday, special event, and celebration event exist:
     - Display: "🎉 Holiday: [Title] | 🎊 Event: [Title] | 🎈 Celebration: [Title]" in combined colors
   - If holiday and special event exist:
     - Display both: "🎉 Holiday: [Holiday Title] | 🎊 Event: [Event Title]" in combined colors
     - Show info: "Special event allows vehicle operations"
   - If holiday and celebration event exist:
     - Display: "🎉 Holiday: [Title] | 🎈 Celebration: [Title]"
     - Show holiday status (trips blocked)
   - If special event and celebration event exist:
     - Display: "🎊 Event: [Title] | 🎈 Celebration: [Title]"
   - If only holiday: Display "🎉 Holiday: [Title]" in orange/red background
   - If only special event: Display "🎊 Event: [Title]" in blue/purple background
   - If only celebration event: Display "🎈 Celebration: [Title]" in green/pink background
   - If no event: Display "📅 Today: [Date]" in normal color
3. Update this display using existing dashboard refresh mechanism (on Bloc state changes, WebSocket notifications, or app resume)
   - **Do NOT create new auto-refresh timers** - use existing refresh patterns
4. **Special Events Override**: If special event exists on a date with a holiday, both are shown but trips are allowed
5. **Celebration Events**: Always displayed for information, do not affect trip operations

**Design**:
- Single line banner
- Color coding:
  - Holiday: Orange/Red background with white text
  - Special Event: Blue/Purple background with white text
  - Normal Day: Light gray background with dark text
- Responsive design
- Icon indicators (🎉 for holiday, 🎊 for event)

#### 1.3 Menu Option: View Calendar

**Location**: Drawer menu / Navigation menu

**Menu Item**: "View Calendar" or "School Calendar"

**Functionality**:
- Opens full calendar view page
- Shows all holidays and events for the school
- Allows filtering, searching, and viewing details
- Read-only view (editing only in Calendar Setup)

---

### 2. Driver Dashboard

#### 2.1 Dashboard Display

**Location**: Top of Driver Dashboard (below header, above trip section)

**Display**:
```
┌─────────────────────────────────────────────────────────────┐
│ 📅 Today: [Current Date] | 🚫 Holiday: [Holiday Name]      │
│    Example: "Today: 02 October 2024 | 🚫 Holiday: Sunday"  │
│    (Trip operations disabled)                               │
└─────────────────────────────────────────────────────────────┘
```

**Logic**:
1. On dashboard load, call API: `GET /api/calendar/school/{schoolId}/can-start-trip/{today}`
   - This API checks if trips can be started (considers special events overriding holidays)
2. **Trip Operation Decision**:
   - **If Special Event exists** (even if holiday also exists):
     - Display "🎊 Event: [Title]" in blue/purple background
     - Show message: "Special event today - Vehicle operations allowed"
     - **Enable trip operations** ✅ (Special events allow trips even on holidays)
   - **If only Holiday exists** (no special event):
     - Display "🚫 Holiday: [Title]" in red background
     - Show message: "Trip operations are disabled on holidays"
     - **Disable trip operations** ❌
   - **If both Holiday and Special Event exist**:
     - Display "🎉 Holiday: [Holiday Title] | 🎊 Event: [Event Title]" in combined display
     - Show message: "Special event allows vehicle operations"
     - **Enable trip operations** ✅ (Special event takes priority)
   - **If no event**:
     - Display "📅 Today: [Date]" in normal color
     - **Enable trip operations** ✅

**Trip Section Behavior**:
- **If Only Holiday (No Special Event, Celebration events do NOT count)**:
  - "Start Trip" button: Disabled (grayed out)
  - Tooltip on hover: "Trips cannot be started on holidays"
  - Entire trip card/section: Grayed out with overlay
  - Message: "Today is a holiday. Trip operations are not available."
  
- **If Special Event Exists (Even with Holiday or Celebration Event)**:
  - "Start Trip" button: **Enabled** ✅
  - Show info message: "Special event today - Trips allowed"
  - Trip operations enabled as normal
  - Driver can start trips for special classes/annual functions on Sundays/holidays
  
- **If Only Celebration Event (No Holiday, No Special Event)**:
  - Trip operations enabled as normal ✅
  - Celebration events do NOT affect trip operations
  - Show celebration info in banner (informational only)
  
- **If Normal Day**:
  - Trip operations enabled as normal

**Priority Logic**:
- **Special Events > Holidays** for trip operations
- **Celebration Events** are neutral (do not affect trip operations)
- If a date has both holiday and special event, trips are allowed
- Celebration events can coexist with any other events and are purely informational
- This allows schools to conduct special classes/annual functions on Sundays/holidays
- Parents can see celebration events and prepare children accordingly

#### 2.2 Menu Option: View Calendar

**Location**: Drawer menu

**Menu Item**: "School Calendar" or "View Calendar"

**Functionality**:
- Opens calendar view page (read-only)
- Shows all holidays and events for the driver's school
- Driver can see upcoming holidays to plan trips
- Cannot edit (read-only)

---

### 3. Parent Dashboard

#### 3.1 Dashboard Display

**Location**: Top of Parent Dashboard (below header, above main content)

**Display**:
```
┌─────────────────────────────────────────────────────────────┐
│ 📅 Today: [Current Date] | 🎉 Holiday: [Holiday Name]      │
│    Example: "Today: 02 October 2024 | 🎉 Holiday: Sunday"  │
│    (No trips scheduled today)                               │
└─────────────────────────────────────────────────────────────┘
```

**Logic**:
1. On dashboard load, call API: `GET /api/calendar/school/{schoolId}/today`
   - Get schoolId from student's school
   - **Important**: Use existing dashboard refresh mechanism (Bloc events) - do NOT create new refresh timers
2. Check if today has any events:
   - If celebration event (especially important for parents):
     - Display "🎈 Celebration: [Title]" in green/pink background (highlighted)
     - Show message: "School celebration today - Prepare your child accordingly"
     - Example: "🎈 Celebration: Diwali Celebration" or "🎈 Celebration: Parent Day"
   - If holiday: 
     - Display "🎉 Holiday: [Title]" in orange/red background
     - Optional: Show message "No trips scheduled today" (informational)
   - If special event: 
     - Display "🎊 Event: [Title]" in blue/purple background
   - If multiple events: Display all with appropriate icons and colors
   - If no event: 
     - Display "📅 Today: [Date]" in normal color

**Design**:
- Single line banner with proper color combination
- Informational only (does not block any functionality)
- **Celebration events should be prominently displayed** for parents
- Color coding: Holiday (red/orange), Special Event (blue/purple), Celebration (green/pink)
- Celebration events help parents prepare children for school events

#### 3.2 Menu Option: View Calendar

**Location**: Drawer menu

**Menu Item**: "School Calendar"

**Functionality**:
- Opens calendar view page (read-only)
- Shows all holidays and events for the student's school
- Parents can plan ahead for holidays
- Cannot edit (read-only)

---

### 4. Gate Staff Dashboard

#### 4.1 Dashboard Display

**Location**: Top of Gate Staff Dashboard (below header)

**Display**:
```
┌─────────────────────────────────────────────────────────────┐
│ 📅 Today: [Current Date] | 🎉 Holiday: [Holiday Name]      │
│    Example: "Today: 02 October 2024 | 🎉 Holiday: Sunday"  │
└─────────────────────────────────────────────────────────────┘
```

**Logic**:
1. On dashboard load, call API: `GET /api/calendar/school/{schoolId}/today`
   - Get schoolId from gate staff's school
2. Display today's event/holiday information
3. Informational only (does not block functionality)

#### 4.2 Menu Option: View Calendar

**Location**: Drawer menu

**Menu Item**: "School Calendar"

**Functionality**:
- Opens calendar view page (read-only)
- Shows all holidays and events for the school
- Gate staff can be aware of school holidays

---

### 5. Vehicle Owner Dashboard

#### 5.1 Dashboard Display

**Location**: Top of Vehicle Owner Dashboard (below header)

**Display**:
```
┌─────────────────────────────────────────────────────────────┐
│ 📅 Today: [Current Date] | 🎉 Holiday: [Holiday Name]      │
│    Note: Shows holiday for associated school(s)             │
└─────────────────────────────────────────────────────────────┘
```

**Logic**:
1. Vehicle Owner may be associated with multiple schools
2. Display holidays for all associated schools (if any)
3. If multiple schools have holidays, show: "Holidays: School1 - [Holiday], School2 - [Holiday]"
4. Informational only

#### 5.2 Menu Option: View Calendar

**Location**: Drawer menu

**Menu Item**: "School Calendar"

**Functionality**:
- Opens calendar view page
- Shows calendar for selected school (dropdown to switch schools)
- Read-only view

---

### 6. App Admin Dashboard

**No Changes Required**

- App Admin manages multiple schools
- Holidays are school-specific
- App Admin does not need to see individual school holidays on dashboard
- (Optional: Can view calendars through school management if needed)

---

## 🎨 UI/UX Design Specifications

### Calendar Banner Component (All Dashboards)

**Design Requirements**:
1. **Single Line Display**:
   - Height: 40-50px
   - Full width of dashboard
   - Position: Below header, above main content

2. **Color Scheme**:
   - **Holiday**: 
     - Background: `#FF6B6B` (Red) or `#FFA500` (Orange)
     - Text: White
     - Icon: 🎉 or 🚫
   - **Special Event**: 
     - Background: `#4ECDC4` (Teal) or `#9B59B6` (Purple)
     - Text: White
     - Icon: 🎊
   - **Celebration Event**: 
     - Background: `#4CAF50` (Green) or `#E91E63` (Pink)
     - Text: White
     - Icon: 🎈
     - **Important**: Prominently displayed for parents
   - **Normal Day**: 
     - Background: `#E8E8E8` (Light Gray)
     - Text: `#333333` (Dark Gray)
     - Icon: 📅

3. **Typography**:
   - Font Size: 14-16px
   - Font Weight: Medium (500)
   - Padding: 12px horizontal, 10px vertical

4. **Responsive**:
   - Mobile: Stack on small screens if needed
   - Tablet/Desktop: Single line

### Calendar View Page

**Design Requirements**:
1. **Layout**:
   - Monthly calendar grid view
   - Sidebar with filters and actions
   - Event list view (optional)

2. **Calendar Grid**:
   - Standard calendar layout (7 days × 4-5 weeks)
   - Today's date highlighted with border
   - Holidays marked with colored dot/background
   - Special events marked with different colored dot
   - Click date to view/add events

3. **Event Details**:
   - Event card/modal showing:
     - Date
     - Type (Holiday/Event)
     - Title
     - Description
     - Recurrence info (if applicable)

---

## 🔄 User Flows

### Flow 1: School Admin Sets Up Holiday

1. School Admin logs in
2. Navigates to Dashboard
3. Clicks "Calendar Setup" from menu
4. Calendar Setup page opens
5. Clicks on a date (e.g., October 2nd)
6. "Add Holiday" dialog opens
7. Fills in:
   - Event Type: Holiday
   - Title: "Mahatma Gandhi Jayanti"
   - Description: "National Holiday"
   - Allow Vehicle Operations: No (automatically disabled for holidays)
   - Is Recurring: No (or Yes if yearly)
8. Clicks "Save"
9. API call: `POST /api/school-admin/school/{schoolId}/calendar`
10. Holiday saved
11. Calendar updated to show holiday
12. All dashboards will show this holiday on October 2nd

### Flow 1b: School Admin Adds Special Event on Sunday (Overrides Holiday)

1. School Admin logs in
2. Navigates to Calendar Setup
3. Clicks on Sunday date (already marked as holiday)
4. "Add Event" dialog opens
5. Fills in:
   - Event Type: **Special Event**
   - Title: "Annual Function" or "Special Classes"
   - Description: "School annual function - Vehicle operations required"
   - Allow Vehicle Operations: Yes (automatically enabled for special events)
   - Is Recurring: No
6. Clicks "Save"
7. API call: `POST /api/school-admin/school/{schoolId}/calendar`
8. Special event saved (date now has both holiday and special event)
9. Calendar shows both events on the date
10. Driver dashboard will allow trips (special event overrides holiday)
11. Banner shows both: "🎉 Holiday: Sunday | 🎊 Event: Annual Function"

### Flow 2: Driver Views Dashboard on Holiday

#### Scenario 2a: Only Holiday (No Special Event)
1. Driver logs in
2. Dashboard loads
3. API call: `GET /api/calendar/school/{schoolId}/can-start-trip/{today}`
4. Response: `{ "canStartTrip": false, "isHoliday": true, "hasSpecialEvent": false, "events": [{ "title": "Sunday", "type": "HOLIDAY" }] }`
5. Dashboard displays: "🚫 Holiday: Sunday"
6. Trip section is disabled (grayed out)
7. "Start Trip" button is disabled
8. Driver cannot start trips
9. Driver can click "View Calendar" to see all holidays

#### Scenario 2b: Holiday + Special Event (Special Event Overrides)
1. Driver logs in
2. Dashboard loads
3. API call: `GET /api/calendar/school/{schoolId}/can-start-trip/{today}`
4. Response: `{ "canStartTrip": true, "isHoliday": true, "hasSpecialEvent": true, "events": [{ "title": "Sunday", "type": "HOLIDAY" }, { "title": "Annual Function", "type": "SPECIAL_EVENT" }] }`
5. Dashboard displays: "🎉 Holiday: Sunday | 🎊 Event: Annual Function"
6. Show message: "Special event allows vehicle operations"
7. Trip section is **enabled** ✅
8. "Start Trip" button is **enabled** ✅
9. Driver can start trips for annual function/special classes
10. Special event overrides holiday for trip operations

#### Scenario 2c: Only Special Event (No Holiday)
1. Driver logs in
2. Dashboard loads
3. API call: `GET /api/calendar/school/{schoolId}/can-start-trip/{today}`
4. Response: `{ "canStartTrip": true, "isHoliday": false, "hasSpecialEvent": true, "events": [{ "title": "Special Classes", "type": "SPECIAL_EVENT" }] }`
5. Dashboard displays: "🎊 Event: Special Classes"
6. Trip operations enabled as normal
7. Driver can start trips

### Flow 3: Parent Views Calendar

1. Parent logs in
2. Dashboard shows: "🎉 Holiday: Sunday" (if today is holiday)
3. Parent clicks "School Calendar" from menu
4. Calendar view page opens
5. Shows all holidays and events for student's school
6. Parent can see upcoming holidays
7. Can filter by month/year
8. Read-only view

### Flow 4: School Admin Marks All Sundays as Holidays

1. School Admin opens Calendar Setup
2. Clicks "Bulk Operations" button
3. Selects "Mark All Sundays as Holidays"
4. Selects date range (e.g., academic year 2024-2025)
5. Confirms action
6. API call: `POST /api/school-admin/school/{schoolId}/calendar/bulk`
7. All Sundays in range are marked as holidays
8. Calendar updated

---

## 🗄️ Database Schema

### SchoolCalendar Table

```sql
CREATE TABLE school_calendar (
    calendar_id INT PRIMARY KEY AUTO_INCREMENT,
    school_id INT NOT NULL,
    event_date DATE NOT NULL,
    event_type VARCHAR(20) NOT NULL, -- 'HOLIDAY' or 'SPECIAL_EVENT'
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_pattern VARCHAR(50), -- 'WEEKLY', 'MONTHLY', 'YEARLY'
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES school(school_id),
    INDEX idx_school_event_date (school_id, event_date),
    INDEX idx_event_date_type (event_date, event_type),
    UNIQUE KEY uk_school_date_type (school_id, event_date, event_type, title)
);
```

---

## 🔐 Access Control & Permissions

### School Admin
- ✅ Create holidays/events/celebrations
- ✅ Edit holidays/events/celebrations
- ✅ Delete holidays/events/celebrations
- ✅ View all holidays/events/celebrations
- ✅ Bulk operations
- ✅ Activate/Deactivate events
- ✅ Mark celebration events (Diwali, Parent Day, etc.) for parent information

### Driver
- ❌ Cannot create/edit/delete
- ✅ View calendar (read-only)
- ✅ See today's holiday on dashboard
- ❌ Cannot start trips on holidays

### Parent
- ❌ Cannot create/edit/delete
- ✅ View calendar (read-only)
- ✅ See today's holiday/event/celebration on dashboard
- ✅ Celebration events prominently displayed (helps prepare children)
- ✅ Can see celebration events (Diwali, Parent Day, etc.) and prepare accordingly

### Gate Staff
- ❌ Cannot create/edit/delete
- ✅ View calendar (read-only)
- ✅ See today's holiday/event/celebration on dashboard
- ✅ Celebration events displayed (informational)

### Vehicle Owner
- ❌ Cannot create/edit/delete
- ✅ View calendar (read-only) for associated schools
- ✅ See today's holiday/event/celebration on dashboard
- ✅ Celebration events displayed (informational)

### App Admin
- ❌ No changes to dashboard
- ✅ (Optional) Can view calendars through school management

---

## 📱 API Request/Response Examples

### 1. Create Holiday

**Request**:
```http
POST /api/school-admin/school/1/calendar
Content-Type: application/json

{
  "eventDate": "2024-10-02",
  "eventType": "HOLIDAY",
  "title": "Mahatma Gandhi Jayanti",
  "description": "National Holiday",
  "isRecurring": false,
  "isActive": true,
  "createdBy": "admin_username"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Holiday created successfully",
  "data": {
    "calendarId": 1,
    "schoolId": 1,
    "eventDate": "2024-10-02",
    "eventType": "HOLIDAY",
    "title": "Mahatma Gandhi Jayanti",
    "description": "National Holiday",
    "isRecurring": false,
    "isActive": true
  }
}
```

### 2. Get Today's Events

**Request**:
```http
GET /api/calendar/school/1/today
```

**Response** (Holiday):
```json
{
  "success": true,
  "message": "Today's events retrieved",
  "data": {
    "date": "2024-10-02",
    "isHoliday": true,
    "isSpecialEvent": false,
    "events": [
      {
        "calendarId": 1,
        "eventType": "HOLIDAY",
        "title": "Mahatma Gandhi Jayanti",
        "description": "National Holiday"
      }
    ]
  }
}
```

**Response** (Normal Day):
```json
{
  "success": true,
  "message": "Today's events retrieved",
  "data": {
    "date": "2024-10-03",
    "isHoliday": false,
    "isSpecialEvent": false,
    "events": []
  }
}
```

### 3. Check If Date is Holiday (for Driver)

**Request**:
```http
GET /api/calendar/school/1/is-holiday/2024-10-02
```

**Response**:
```json
{
  "success": true,
  "data": {
    "isHoliday": true,
    "event": {
      "calendarId": 1,
      "eventType": "HOLIDAY",
      "title": "Mahatma Gandhi Jayanti",
      "description": "National Holiday"
    }
  }
}
```

### 4. Get Calendar for Month

**Request**:
```http
GET /api/calendar/school/1/month/2024/10
```

**Response**:
```json
{
  "success": true,
  "data": {
    "year": 2024,
    "month": 10,
    "events": [
      {
        "calendarId": 1,
        "eventDate": "2024-10-02",
        "eventType": "HOLIDAY",
        "title": "Mahatma Gandhi Jayanti",
        "description": "National Holiday"
      },
      {
        "calendarId": 2,
        "eventDate": "2024-10-06",
        "eventType": "HOLIDAY",
        "title": "Sunday",
        "description": "Weekly Holiday",
        "isRecurring": true,
        "recurrencePattern": "WEEKLY"
      }
    ]
  }
}
```

---

## 🔄 Step-by-Step Functionality by Dashboard

### School Admin Dashboard

#### Step 1: Calendar Setup Access
- **Location**: Drawer menu / Navigation menu
- **Menu Item**: "Calendar Setup" or "Manage Calendar"
- **Action**: Click to open Calendar Setup page

#### Step 2: Calendar Setup Page
- **View**: Monthly calendar grid
- **Features**:
  1. **Add Holiday**:
     - Click on any date
     - Dialog opens with form
     - Fill: Date, Type, Title, Description, Recurring option
     - Click "Save"
     - Holiday appears on calendar
  
  2. **Edit Holiday**:
     - Click on existing holiday date
     - Dialog opens with pre-filled data
     - Modify fields
     - Click "Save"
  
  3. **Delete Holiday**:
     - Click on holiday date
     - Click "Delete" button
     - Confirm deletion
     - Holiday removed
  
  4. **Bulk Operations**:
     - Button: "Bulk Operations"
     - Options:
       - Mark All Sundays
       - Mark All Saturdays
       - Import from CSV/Excel
     - Select date range
     - Apply
  
  5. **Recurring Events**:
     - Check "Is Recurring"
     - Select pattern: Weekly, Monthly, Yearly
     - System automatically creates events for future dates

#### Step 3: Dashboard Banner Display
- **Location**: Top of dashboard
- **Display Logic**:
  1. On page load, fetch today's events
  2. If holiday: Show "🎉 Holiday: [Title]" with orange/red background
  3. If special event: Show "🎊 Event: [Title]" with blue/purple background
  4. If no event: Show "📅 Today: [Date]" with gray background
  5. Update daily (refresh on app open)

#### Step 4: View Calendar (Menu)
- **Location**: Drawer menu
- **Menu Item**: "View Calendar"
- **Functionality**: Opens read-only calendar view
- **Features**: Filter, search, view details

---

### Driver Dashboard

#### Step 1: Dashboard Banner Display
- **Location**: Top of dashboard
- **Display Logic**:
  1. On page load, fetch today's events for driver's school
  2. If holiday:
     - Show "🚫 Holiday: [Title]" with red background
     - Display message: "Trip operations disabled"
  3. If special event:
     - Show "🎊 Event: [Title]" with blue background
     - Trip operations enabled
  4. If no event:
     - Show "📅 Today: [Date]" with gray background
     - Trip operations enabled

#### Step 2: Trip Section Behavior
- **Check Trip Status**:
  1. Call API: `GET /api/calendar/school/{schoolId}/can-start-trip/{today}`
  2. Response includes: `canStartTrip`, `isHoliday`, `hasSpecialEvent`, `events`

- **If Only Holiday (No Special Event)**:
  1. If `canStartTrip: false` and `hasSpecialEvent: false`:
     - Disable "Start Trip" button
     - Gray out trip section
     - Show overlay message: "Today is a holiday. Trip operations are not available."
     - Add tooltip to disabled button: "Trips cannot be started on holidays"
  2. Disable all trip-related actions

- **If Special Event Exists (Even with Holiday)**:
  1. If `canStartTrip: true` and `hasSpecialEvent: true`:
     - **Enable "Start Trip" button** ✅
     - Show info message: "Special event today - Trips allowed"
     - Enable all trip operations
     - Allow trip start as normal
     - Special event overrides holiday

- **If Only Special Event (No Holiday)**:
  1. If `canStartTrip: true` and `isHoliday: false`:
     - Enable all trip operations
     - Allow trip start as normal

- **If Normal Day (No Events)**:
  1. Enable all trip operations
  2. Allow trip start as normal

#### Step 3: View Calendar (Menu)
- **Location**: Drawer menu
- **Menu Item**: "School Calendar"
- **Functionality**: Opens read-only calendar view
- **Purpose**: Driver can see upcoming holidays to plan trips

---

### Parent Dashboard

#### Step 1: Dashboard Banner Display
- **Location**: Top of dashboard
- **Display Logic**:
  1. Get student's school ID
  2. Fetch today's events: `GET /api/calendar/school/{schoolId}/today`
  3. If holiday:
     - Show "🎉 Holiday: [Title]" with orange/red background
     - Optional message: "No trips scheduled today"
  4. If special event:
     - Show "🎊 Event: [Title]" with blue/purple background
  5. If no event:
     - Show "📅 Today: [Date]" with gray background
  6. **Note**: Informational only, does not block any functionality

#### Step 2: View Calendar (Menu)
- **Location**: Drawer menu
- **Menu Item**: "School Calendar"
- **Functionality**: Opens read-only calendar view
- **Purpose**: Parents can see upcoming holidays to plan ahead

---

### Gate Staff Dashboard

#### Step 1: Dashboard Banner Display
- **Location**: Top of dashboard
- **Display Logic**:
  1. Get gate staff's school ID
  2. Fetch today's events: `GET /api/calendar/school/{schoolId}/today`
  3. Display holiday/event information
  4. Informational only

#### Step 2: View Calendar (Menu)
- **Location**: Drawer menu
- **Menu Item**: "School Calendar"
- **Functionality**: Opens read-only calendar view

---

### Vehicle Owner Dashboard

#### Step 1: Dashboard Banner Display
- **Location**: Top of dashboard
- **Display Logic**:
  1. Get vehicle owner's associated schools
  2. Fetch today's events for all schools
  3. Display holidays (if any)
  4. If multiple schools: Show all holidays
  5. Informational only

#### Step 2: View Calendar (Menu)
- **Location**: Drawer menu
- **Menu Item**: "School Calendar"
- **Functionality**: Opens calendar view with school selector
- **Purpose**: View calendar for different schools

---

## 🎯 Key Features Summary

### School Admin
1. ✅ Create, edit, delete holidays and events
2. ✅ Bulk operations (mark all Sundays, etc.)
3. ✅ Recurring events support
4. ✅ View calendar
5. ✅ Dashboard shows today's holiday
6. ✅ Import/Export calendar

### Driver
1. ✅ View today's holiday on dashboard
2. ✅ Trip start disabled on holidays
3. ✅ View calendar (read-only)
4. ✅ See upcoming holidays

### Parent
1. ✅ View today's holiday on dashboard
2. ✅ View calendar (read-only)
3. ✅ See upcoming holidays

### Gate Staff
1. ✅ View today's holiday on dashboard
2. ✅ View calendar (read-only)

### Vehicle Owner
1. ✅ View today's holiday on dashboard
2. ✅ View calendar for associated schools (read-only)

---

## 🔧 Technical Implementation Notes

### Backend Service Layer

#### SchoolCalendarService
- `createCalendarEvent(SchoolCalendarRequestDto)` - Create holiday/event
- `updateCalendarEvent(Integer calendarId, SchoolCalendarRequestDto)` - Update
- `deleteCalendarEvent(Integer calendarId)` - Delete
- `getTodayEvents(Integer schoolId)` - Get today's events (all types)
- `getEventsByDateRange(Integer schoolId, LocalDate start, LocalDate end)` - Get range
- `isHoliday(Integer schoolId, LocalDate date)` - Check if date is holiday
- `canStartTrip(Integer schoolId, LocalDate date)` - **Check if trips can start**
  - **Logic**: Returns `true` if:
    - No events exist, OR
    - Special event exists (even if holiday or celebration event also exists), OR
    - Only celebration event exists (celebration events do not affect trips)
  - Returns `false` only if:
    - Holiday exists and no special event exists (celebration events do NOT override holidays)
  - **Celebration events are ignored for trip status** (informational only)
- `getEventsByMonth(Integer schoolId, int year, int month)` - Get month events
- `createRecurringEvents(SchoolCalendarRequestDto)` - Create recurring events
- `getHolidaysByDate(Integer schoolId, LocalDate date)` - Get only holidays for date
- `getSpecialEventsByDate(Integer schoolId, LocalDate date)` - Get only special events for date

#### Repository Methods
- `findBySchoolAndEventDate(School school, LocalDate date)` - Find by date
- `findBySchoolAndEventDateBetween(School school, LocalDate start, LocalDate end)` - Find range
- `findBySchoolAndEventType(School school, EventType type)` - Find by type
- `existsBySchoolAndEventDateAndEventType(School school, LocalDate date, EventType type)` - Check exists

### Frontend Service Layer

#### CalendarService
- `getTodayEvents(int schoolId)` - Get today's events (all types)
- `getCalendarEvents(int schoolId)` - Get all events
- `getMonthEvents(int schoolId, int year, int month)` - Get month events
- `createEvent(SchoolCalendarRequest request)` - Create (admin only)
- `updateEvent(int calendarId, SchoolCalendarRequest request)` - Update (admin only)
- `deleteEvent(int calendarId)` - Delete (admin only)
- `isHoliday(int schoolId, DateTime date)` - Check if date is holiday
- `canStartTrip(int schoolId, DateTime date)` - **Check if trips can start** (considers special events)
  - Returns: `{ canStartTrip: bool, isHoliday: bool, hasSpecialEvent: bool, events: [...] }`
- `checkHolidayStatus(int schoolId, DateTime date)` - Check holiday status
- `getEventsByDate(int schoolId, DateTime date)` - Get all events for a date (holidays + special events)

### State Management

#### Calendar Bloc (for Calendar Setup)
- Events:
  - `LoadCalendarEvents(schoolId)`
  - `CreateCalendarEvent(event)`
  - `UpdateCalendarEvent(event)`
  - `DeleteCalendarEvent(calendarId)`
  - `CheckTodayEvents(schoolId)`
- States:
  - `CalendarLoading`
  - `CalendarLoaded(events)`
  - `CalendarError(message)`
  - `TodayEventLoaded(event)`

### Dashboard Integration

#### Dashboard Banner Widget (Reusable)
- **Component**: `CalendarBannerWidget`
- **Props**:
  - `schoolId` (required)
  - `showTripDisable` (optional, for driver dashboard)
  - `onTap` (optional, navigate to calendar)
- **Functionality**:
  - Fetches today's events on load
  - Displays appropriate message and color
  - Updates daily

---

## 📋 Implementation Checklist

### Backend
- [ ] Create `SchoolCalendar` entity
- [ ] Create `EventType` enum
- [ ] Create `SchoolCalendarRepository`
- [ ] Create `SchoolCalendarService`
  - [ ] Implement `canStartTrip()` method (checks special events override holidays)
  - [ ] Logic: Return `true` if special event exists (even if holiday also exists)
- [ ] Create `SchoolCalendarController`
  - [ ] Add endpoint: `GET /api/calendar/school/{schoolId}/can-start-trip/{date}`
- [ ] Create DTOs (Request/Response)
- [ ] Add validation logic
- [ ] Add recurring events logic
- [ ] Add bulk operations logic
- [ ] Add indexes for performance
- [ ] **Implement special event override logic** (priority: SPECIAL_EVENT > HOLIDAY)
- [ ] Allow multiple events per date (holiday + special event)

### Frontend
- [ ] Create `CalendarService`
- [ ] Create `SchoolCalendar` model
- [ ] Create Calendar Setup page (School Admin)
- [ ] Create Calendar View page (All users)
- [ ] Create `CalendarBannerWidget` (reusable)
- [ ] Integrate banner in School Admin Dashboard
- [ ] Integrate banner in Driver Dashboard
- [ ] Integrate banner in Parent Dashboard
- [ ] Integrate banner in Gate Staff Dashboard
- [ ] Integrate banner in Vehicle Owner Dashboard
- [ ] Add menu items for "View Calendar"
- [ ] Implement trip disable logic (Driver Dashboard)
- [ ] Add calendar Bloc (state management)
- [ ] Add error handling
- [ ] Add loading states

### Testing
- [ ] Test calendar creation
- [ ] Test recurring events
- [ ] Test bulk operations
- [ ] Test holiday check for driver
- [ ] **Test special event on holiday date** (trips should be allowed)
- [ ] **Test only holiday** (trips should be blocked)
- [ ] **Test only special event** (trips should be allowed)
- [ ] **Test holiday + special event on same date** (trips should be allowed - special event overrides)
- [ ] Test dashboard displays (with both events)
- [ ] Test trip disable on holidays (when no special event)
- [ ] Test trip enable on special events (even with holiday)
- [ ] Test calendar view on all dashboards
- [ ] Test permissions (who can edit/view)
- [ ] **Test Sunday + Annual Function scenario** (both events, trips allowed)
- [ ] **Test multiple special events on same date**

---

## 🎨 UI Mockup Description

### Calendar Banner (All Dashboards)

```
┌──────────────────────────────────────────────────────────────────────┐
│  🎉  Today: 02 October 2024 | Holiday: Mahatma Gandhi Jayanti       │
│       (Orange/Red Background, White Text)                            │
└──────────────────────────────────────────────────────────────────────┘
```

### Calendar Setup Page (School Admin)

```
┌──────────────────────────────────────────────────────────────────────┐
│  Calendar Setup                              [Add Event] [Bulk Ops]  │
├──────────────────────────────────────────────────────────────────────┤
│  [<] October 2024 [>]                                                │
│                                                                       │
│  Sun  Mon  Tue  Wed  Thu  Fri  Sat                                  │
│       1    2⭐  3    4    5    6                                    │
│   7    8    9   10   11   12   13                                   │
│  14   15   16   17   18   19   20                                   │
│  21   22   23   24   25   26   27                                   │
│  28   29   30   31                                                  │
│                                                                       │
│  Legend: ⭐ Holiday  🎊 Special Event                               │
└──────────────────────────────────────────────────────────────────────┘
```

### Driver Dashboard (Holiday Day - No Special Event)

```
┌──────────────────────────────────────────────────────────────────────┐
│  🚫  Today: 06 October 2024 | Holiday: Sunday                       │
│       (Red Background) Trip operations disabled                      │
├──────────────────────────────────────────────────────────────────────┤
│  [Trip Section - GRAYED OUT]                                         │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  Today's Trip                                                 │   │
│  │  [Start Trip - DISABLED]                                      │   │
│  │  Message: "Today is a holiday. Trip operations unavailable." │   │
│  └──────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────────┘
```

### Driver Dashboard (Holiday + Special Event - Trips Allowed)

```
┌──────────────────────────────────────────────────────────────────────┐
│  🎉 Holiday: Sunday | 🎊 Event: Annual Function                     │
│       (Combined Colors) Special event allows vehicle operations      │
├──────────────────────────────────────────────────────────────────────┤
│  [Trip Section - ENABLED]                                            │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  Today's Trip                                                 │   │
│  │  [Start Trip - ENABLED] ✅                                    │   │
│  │  Info: "Special event today - Trips allowed"                  │   │
│  └──────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Data Flow Diagrams

### Flow 1: School Admin Creates Holiday

```
School Admin → Calendar Setup Page → Click Date → Add Holiday Dialog
    → Fill Form → Save → API Call → Backend Service → Database
    → Response → Update UI → All Dashboards (on next load)
```

### Flow 2: Driver Dashboard on Holiday

#### Scenario A: Only Holiday (No Special Event)
```
Driver Login → Dashboard Load → API: Can Start Trip?
    → Response: canStartTrip = false, isHoliday = true, hasSpecialEvent = false
    → Display Banner: "🚫 Holiday: Sunday"
    → Disable Trip Section
    → Driver cannot start trip
```

#### Scenario B: Holiday + Special Event (Special Event Overrides)
```
Driver Login → Dashboard Load → API: Can Start Trip?
    → Response: canStartTrip = true, isHoliday = true, hasSpecialEvent = true
    → Display Banner: "🎉 Holiday: Sunday | 🎊 Event: Annual Function"
    → Show Message: "Special event allows vehicle operations"
    → Enable Trip Section ✅
    → Driver can start trip (for annual function/special classes)
```

### Flow 3: View Calendar

```
User → Menu → View Calendar → API: Get Calendar Events
    → Display Calendar Grid → Show Holidays/Events → Read-only
```

---

## ✅ Summary

This feature provides:

1. **School Admin**: Full calendar management (create, edit, delete holidays/events)
2. **All Dashboards**: Display today's holiday/event information
3. **Driver Dashboard**: 
   - Disable trip operations on holidays (when no special event)
   - **Enable trip operations when special events exist** (even on holidays)
   - Special events override holidays for trip operations
4. **All Users**: View full calendar (read-only)
5. **School-Specific**: Each school manages its own calendar
6. **Recurring Events**: Support for weekly/monthly/yearly recurring holidays
7. **Bulk Operations**: Mark multiple dates as holidays (e.g., all Sundays)
8. **Special Events Override**: Special events allow vehicle operations on holidays

**Key Feature - Special Events Override Holidays**:
- ✅ Schools can schedule special classes/annual functions on Sundays/holidays
- ✅ A date can have both holiday and special event
- ✅ Special events take priority: trips are allowed even if date is also a holiday
- ✅ This allows flexibility for schools to conduct events on holidays

The implementation ensures:
- ✅ School-specific holidays
- ✅ Special events override holidays for trip operations
- ✅ Proper access control
- ✅ User-friendly UI
- ✅ Real-time updates
- ✅ Performance optimization (indexes, caching)
- ✅ Mobile-responsive design
- ✅ Flexible event management (multiple events per date)

---

**Date**: Current
**Version**: 1.0
**Status**: Requirements Document - Ready for Implementation

