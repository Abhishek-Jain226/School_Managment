# Testing Guide Documentation

## 🧪 **Comprehensive Testing Guide**

This guide provides detailed testing procedures for the Kids Vehicle Tracking Application, covering unit tests, integration tests, API testing, and end-to-end testing.

---

## 📋 **Testing Overview**

### **Testing Pyramid**
```
┌─────────────────────────────────────────────────────────────────┐
│                    E2E Tests (10%)                             │
│                 End-to-End User Scenarios                      │
├─────────────────────────────────────────────────────────────────┤
│                  Integration Tests (20%)                       │
│                API & Service Integration                       │
├─────────────────────────────────────────────────────────────────┤
│                    Unit Tests (70%)                            │
│                Individual Component Testing                     │
└─────────────────────────────────────────────────────────────────┘
```

### **Testing Types**
1. **Unit Tests**: Individual component testing
2. **Integration Tests**: API and service integration
3. **End-to-End Tests**: Complete user workflows
4. **Performance Tests**: Load and stress testing
5. **Security Tests**: Security vulnerability testing

---

## 🔧 **Backend Testing (Spring Boot)**

### **Unit Testing Setup**

#### **Test Dependencies**
```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mysql</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### **Test Configuration**
```java
// src/test/java/com/app/config/TestConfig.java
@Configuration
@TestPropertySource(locations = "classpath:application-test.properties")
public class TestConfig {
    
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### **Service Layer Testing**

#### **Driver Service Test**
```java
// src/test/java/com/app/service/impl/DriverServiceImplTest.java
@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {
    
    @Mock
    private DriverRepository driverRepository;
    
    @Mock
    private TripRepository tripRepository;
    
    @Mock
    private DispatchLogRepository dispatchLogRepository;
    
    @Mock
    private WebSocketNotificationService webSocketService;
    
    @InjectMocks
    private DriverServiceImpl driverService;
    
    @Test
    void getDriverDashboard_ShouldReturnDashboard_WhenDriverExists() {
        // Given
        Integer driverId = 1;
        Driver driver = Driver.builder()
            .driverId(driverId)
            .driverName("John Doe")
            .driverContactNumber("9876543210")
            .isActive(true)
            .build();
        
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(tripRepository.findByDriverAndIsActive(driver, true)).thenReturn(Collections.emptyList());
        
        // When
        ApiResponse response = driverService.getDriverDashboard(driverId);
        
        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Driver dashboard retrieved successfully");
        assertThat(response.getData()).isNotNull();
    }
    
    @Test
    void getDriverDashboard_ShouldReturnError_WhenDriverNotFound() {
        // Given
        Integer driverId = 999;
        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());
        
        // When
        ApiResponse response = driverService.getDriverDashboard(driverId);
        
        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Driver not found");
    }
    
    @Test
    void markStudentAttendance_ShouldCreateDispatchLog_WhenValidRequest() {
        // Given
        Integer driverId = 1;
        StudentAttendanceRequestDto request = StudentAttendanceRequestDto.builder()
            .tripId(1)
            .studentId(1)
            .eventType(EventType.PICKUP_FROM_PARENT)
            .remarks("Student picked up successfully")
            .build();
        
        Driver driver = Driver.builder().driverId(driverId).build();
        Trip trip = Trip.builder().tripId(1).build();
        Student student = Student.builder().studentId(1).build();
        
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(tripRepository.findById(1)).thenReturn(Optional.of(trip));
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(dispatchLogRepository.save(any(DispatchLog.class))).thenReturn(new DispatchLog());
        
        // When
        ApiResponse response = driverService.markStudentAttendance(driverId, request);
        
        // Then
        assertThat(response.isSuccess()).isTrue();
        verify(dispatchLogRepository).save(any(DispatchLog.class));
        verify(webSocketService).sendNotificationToUser(anyString(), any(WebSocketNotificationDto.class));
    }
}
```

#### **Auth Service Test**
```java
// src/test/java/com/app/service/impl/AuthServiceImplTest.java
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider tokenProvider;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    @Test
    void login_ShouldReturnToken_WhenValidCredentials() {
        // Given
        LoginRequestDto request = LoginRequestDto.builder()
            .loginId("admin")
            .password("password")
            .build();
        
        User user = User.builder()
            .userId(1)
            .userName("admin")
            .email("admin@example.com")
            .password("encoded_password")
            .isActive(true)
            .build();
        
        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded_password")).thenReturn(true);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("jwt_token");
        
        // When
        ApiResponse response = authService.login(request);
        
        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNotNull();
    }
    
    @Test
    void login_ShouldReturnError_WhenInvalidCredentials() {
        // Given
        LoginRequestDto request = LoginRequestDto.builder()
            .loginId("admin")
            .password("wrong_password")
            .build();
        
        User user = User.builder()
            .userName("admin")
            .password("encoded_password")
            .build();
        
        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);
        
        // When
        ApiResponse response = authService.login(request);
        
        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Invalid credentials");
    }
}
```

### **Repository Testing**

#### **Driver Repository Test**
```java
// src/test/java/com/app/repository/DriverRepositoryTest.java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class DriverRepositoryTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Test
    void findByUserId_ShouldReturnDriver_WhenUserExists() {
        // Given
        User user = User.builder()
            .userName("testuser")
            .email("test@example.com")
            .contactNumber("9876543210")
            .password("password")
            .build();
        entityManager.persistAndFlush(user);
        
        Driver driver = Driver.builder()
            .userId(user.getUserId())
            .driverName("Test Driver")
            .driverContactNumber("9876543211")
            .build();
        entityManager.persistAndFlush(driver);
        
        // When
        Optional<Driver> result = driverRepository.findByUserId(user.getUserId());
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDriverName()).isEqualTo("Test Driver");
    }
    
    @Test
    void findByIsActiveTrue_ShouldReturnActiveDrivers() {
        // Given
        Driver activeDriver = Driver.builder()
            .driverName("Active Driver")
            .driverContactNumber("9876543212")
            .isActive(true)
            .build();
        entityManager.persistAndFlush(activeDriver);
        
        Driver inactiveDriver = Driver.builder()
            .driverName("Inactive Driver")
            .driverContactNumber("9876543213")
            .isActive(false)
            .build();
        entityManager.persistAndFlush(inactiveDriver);
        
        // When
        List<Driver> result = driverRepository.findByIsActiveTrue();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDriverName()).isEqualTo("Active Driver");
    }
}
```

### **Controller Testing**

#### **Driver Controller Test**
```java
// src/test/java/com/app/controller/DriverControllerTest.java
@WebMvcTest(DriverController.class)
@Import(SecurityConfig.class)
class DriverControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private IDriverService driverService;
    
    @Test
    @WithMockUser(roles = "DRIVER")
    void getDriverDashboard_ShouldReturnDashboard_WhenAuthorized() throws Exception {
        // Given
        Integer driverId = 1;
        DriverDashboardResponseDto dashboard = DriverDashboardResponseDto.builder()
            .driverId(driverId)
            .driverName("John Doe")
            .build();
        
        ApiResponse response = ApiResponse.builder()
            .success(true)
            .message("Dashboard retrieved successfully")
            .data(dashboard)
            .build();
        
        when(driverService.getDriverDashboard(driverId)).thenReturn(response);
        
        // When & Then
        mockMvc.perform(get("/api/drivers/{driverId}/dashboard", driverId)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.driverName").value("John Doe"));
    }
    
    @Test
    void getDriverDashboard_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/drivers/1/dashboard"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(roles = "DRIVER")
    void markStudentAttendance_ShouldCreateAttendance_WhenValidRequest() throws Exception {
        // Given
        Integer driverId = 1;
        StudentAttendanceRequestDto request = StudentAttendanceRequestDto.builder()
            .tripId(1)
            .studentId(1)
            .eventType(EventType.PICKUP_FROM_PARENT)
            .remarks("Student picked up")
            .build();
        
        ApiResponse response = ApiResponse.builder()
            .success(true)
            .message("Attendance marked successfully")
            .build();
        
        when(driverService.markStudentAttendance(driverId, request)).thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/drivers/{driverId}/attendance", driverId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
```

---

## 📱 **Frontend Testing (Flutter)**

### **Unit Testing Setup**

#### **Test Dependencies**
```yaml
# pubspec.yaml
dev_dependencies:
  flutter_test:
    sdk: flutter
  mockito: ^5.4.2
  build_runner: ^2.4.6
  http_mock_adapter: ^0.6.1
  integration_test:
    sdk: flutter
```

#### **Test Configuration**
```dart
// test/test_config.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';

class TestConfig {
  static void setup() {
    TestWidgetsFlutterBinding.ensureInitialized();
  }
}
```

### **Service Testing**

#### **Driver Service Test**
```dart
// test/services/driver_service_test.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';
import 'package:http/http.dart' as http;
import 'package:school_tracker/services/driver_service.dart';
import 'package:school_tracker/data/models/driver_dashboard.dart';

import 'driver_service_test.mocks.dart';

@GenerateMocks([http.Client])
void main() {
  group('DriverService', () {
    late DriverService driverService;
    late MockClient mockClient;

    setUp(() {
      mockClient = MockClient();
      driverService = DriverService();
    });

    test('getDriverDashboard should return dashboard data', () async {
      // Given
      final responseBody = '''
      {
        "success": true,
        "message": "Dashboard retrieved successfully",
        "data": {
          "driverId": 1,
          "driverName": "John Doe",
          "driverContactNumber": "9876543210",
          "totalTripsToday": 2,
          "completedTrips": 1,
          "pendingTrips": 1
        }
      }
      ''';

      when(mockClient.get(
        any,
        headers: anyNamed('headers'),
      )).thenAnswer((_) async => http.Response(responseBody, 200));

      // When
      final result = await driverService.getDriverDashboard(1);

      // Then
      expect(result, isA<DriverDashboard>());
      expect(result.driverName, equals('John Doe'));
      expect(result.totalTripsToday, equals(2));
    });

    test('getDriverDashboard should throw exception on error', () async {
      // Given
      when(mockClient.get(
        any,
        headers: anyNamed('headers'),
      )).thenAnswer((_) async => http.Response('{"success": false}', 500));

      // When & Then
      expect(
        () => driverService.getDriverDashboard(1),
        throwsA(isA<Exception>()),
      );
    });
  });
}
```

#### **Auth Service Test**
```dart
// test/services/auth_service_test.dart
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:school_tracker/services/auth_service.dart';

void main() {
  group('AuthService', () {
    late AuthService authService;

    setUp(() {
      SharedPreferences.setMockInitialValues({});
      authService = AuthService();
    });

    test('login should return user data on success', () async {
      // Given
      final loginData = {
        'loginId': 'admin',
        'password': 'password',
      };

      // When
      final result = await authService.login(loginData);

      // Then
      expect(result, isNotNull);
      expect(result['success'], isTrue);
    });

    test('logout should clear stored data', () async {
      // Given
      await authService.saveToken('test-token');

      // When
      await authService.logout();

      // Then
      final token = await authService.getToken();
      expect(token, isNull);
    });
  });
}
```

### **Widget Testing**

#### **Login Screen Test**
```dart
// test/pages/login_screen_test.dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:school_tracker/presentation/pages/login_screen.dart';

void main() {
  group('LoginScreen', () {
    testWidgets('should display login form', (WidgetTester tester) async {
      // Given
      await tester.pumpWidget(MaterialApp(home: LoginScreen()));

      // Then
      expect(find.byType(TextFormField), findsNWidgets(2));
      expect(find.byType(ElevatedButton), findsOneWidget);
      expect(find.text('Login'), findsOneWidget);
    });

    testWidgets('should validate required fields', (WidgetTester tester) async {
      // Given
      await tester.pumpWidget(MaterialApp(home: LoginScreen()));

      // When
      await tester.tap(find.byType(ElevatedButton));
      await tester.pump();

      // Then
      expect(find.text('Please enter username or mobile number'), findsOneWidget);
      expect(find.text('Please enter password'), findsOneWidget);
    });

    testWidgets('should show loading state during login', (WidgetTester tester) async {
      // Given
      await tester.pumpWidget(MaterialApp(home: LoginScreen()));

      // When
      await tester.enterText(find.byType(TextFormField).first, 'admin');
      await tester.enterText(find.byType(TextFormField).last, 'password');
      await tester.tap(find.byType(ElevatedButton));
      await tester.pump();

      // Then
      expect(find.byType(CircularProgressIndicator), findsOneWidget);
    });
  });
}
```

#### **Driver Dashboard Test**
```dart
// test/pages/driver_dashboard_test.dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:school_tracker/presentation/pages/simplified_driver_dashboard.dart';

void main() {
  group('SimplifiedDriverDashboard', () {
    testWidgets('should display driver information', (WidgetTester tester) async {
      // Given
      await tester.pumpWidget(MaterialApp(home: SimplifiedDriverDashboard()));

      // Then
      expect(find.text('Driver Dashboard'), findsOneWidget);
      expect(find.byType(CircularProgressIndicator), findsOneWidget);
    });

    testWidgets('should display trip selection', (WidgetTester tester) async {
      // Given
      await tester.pumpWidget(MaterialApp(home: SimplifiedDriverDashboard()));

      // When
      await tester.pumpAndSettle();

      // Then
      expect(find.text('Morning Pickup'), findsOneWidget);
      expect(find.text('Afternoon Drop'), findsOneWidget);
    });
  });
}
```

---

## 🌐 **API Testing**

### **Postman Collection**

#### **Authentication Tests**
```json
{
  "info": {
    "name": "Kids Vehicle Tracking API Tests",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Authentication",
      "item": [
        {
          "name": "Login Success",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"loginId\": \"appadmin\",\n  \"password\": \"admin123\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["api", "auth", "login"]
            }
          },
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test('Status code is 200', function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "pm.test('Response has success field', function () {",
                  "    var jsonData = pm.response.json();",
                  "    pm.expect(jsonData).to.have.property('success');",
                  "});",
                  "",
                  "pm.test('Login is successful', function () {",
                  "    var jsonData = pm.response.json();",
                  "    pm.expect(jsonData.success).to.be.true;",
                  "});",
                  "",
                  "if (pm.response.json().success) {",
                  "    pm.test('Token is present', function () {",
                  "        var jsonData = pm.response.json();",
                  "        pm.expect(jsonData.data).to.have.property('token');",
                  "        pm.environment.set('authToken', jsonData.data.token);",
                  "    });",
                  "}"
                ]
              }
            }
          ]
        }
      ]
    }
  ]
}
```

#### **Driver API Tests**
```json
{
  "name": "Driver Dashboard",
  "request": {
    "method": "GET",
    "header": [
      {
        "key": "Authorization",
        "value": "Bearer {{authToken}}"
      }
    ],
    "url": {
      "raw": "{{baseUrl}}/api/drivers/1/dashboard",
      "host": ["{{baseUrl}}"],
      "path": ["api", "drivers", "1", "dashboard"]
    }
  },
  "event": [
    {
      "listen": "test",
      "script": {
        "exec": [
          "pm.test('Status code is 200', function () {",
          "    pm.response.to.have.status(200);",
          "});",
          "",
          "pm.test('Dashboard data is present', function () {",
          "    var jsonData = pm.response.json();",
          "    pm.expect(jsonData.data).to.have.property('driverId');",
          "    pm.expect(jsonData.data).to.have.property('driverName');",
          "});"
        ]
      }
    }
  ]
}
```

### **cURL Testing Scripts**

#### **Authentication Test**
```bash
#!/bin/bash
# test_auth.sh

BASE_URL="http://localhost:9001/api"

echo "Testing Authentication..."

# Test login
echo "1. Testing login..."
LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginId":"appadmin","password":"admin123"}')

echo "Login Response: $LOGIN_RESPONSE"

# Extract token
TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.token')
echo "Token: $TOKEN"

# Test protected endpoint
echo "2. Testing protected endpoint..."
DASHBOARD_RESPONSE=$(curl -s -X GET $BASE_URL/drivers/1/dashboard \
  -H "Authorization: Bearer $TOKEN")

echo "Dashboard Response: $DASHBOARD_RESPONSE"
```

#### **Driver API Test**
```bash
#!/bin/bash
# test_driver_api.sh

BASE_URL="http://localhost:9001/api"
TOKEN="your-jwt-token-here"

echo "Testing Driver APIs..."

# Test get dashboard
echo "1. Testing get dashboard..."
curl -X GET $BASE_URL/drivers/1/dashboard \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"

# Test mark attendance
echo "2. Testing mark attendance..."
curl -X POST $BASE_URL/drivers/1/attendance \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tripId": 1,
    "studentId": 1,
    "eventType": "PICKUP_FROM_PARENT",
    "remarks": "Student picked up successfully"
  }'
```

---

## 🔄 **Integration Testing**

### **Database Integration Tests**

#### **Test Configuration**
```java
// src/test/java/com/app/integration/IntegrationTestConfig.java
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class IntegrationTestConfig {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
}
```

#### **Driver Service Integration Test**
```java
// src/test/java/com/app/integration/DriverServiceIntegrationTest.java
@SpringBootTest
@Testcontainers
@Transactional
class DriverServiceIntegrationTest extends IntegrationTestConfig {
    
    @Autowired
    private IDriverService driverService;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    @Test
    void getDriverDashboard_ShouldReturnCompleteDashboard_WhenDriverHasTrips() {
        // Given
        Driver driver = createTestDriver();
        Trip trip = createTestTrip(driver);
        
        // When
        ApiResponse response = driverService.getDriverDashboard(driver.getDriverId());
        
        // Then
        assertThat(response.isSuccess()).isTrue();
        DriverDashboardResponseDto dashboard = (DriverDashboardResponseDto) response.getData();
        assertThat(dashboard.getDriverId()).isEqualTo(driver.getDriverId());
        assertThat(dashboard.getTotalTripsToday()).isGreaterThan(0);
    }
    
    private Driver createTestDriver() {
        Driver driver = Driver.builder()
            .driverName("Test Driver")
            .driverContactNumber("9876543210")
            .isActive(true)
            .build();
        return driverRepository.save(driver);
    }
    
    private Trip createTestTrip(Driver driver) {
        Trip trip = Trip.builder()
            .tripName("Test Trip")
            .tripNumber("TRIP001")
            .driver(driver)
            .tripType(TripType.MORNING_PICKUP)
            .tripStatus(TripStatusType.NOT_STARTED)
            .isActive(true)
            .build();
        return tripRepository.save(trip);
    }
}
```

---

## 🚀 **Performance Testing**

### **Load Testing with JMeter**

#### **JMeter Test Plan**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan testname="Kids Vehicle Tracking Load Test">
      <elementProp name="TestPlan.arguments" elementType="Arguments" guiclass="ArgumentsPanel">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
      <stringProp name="TestPlan.user_define_classpath"></stringProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
      <elementProp name="TestPlan.arguments" elementType="Arguments" guiclass="ArgumentsPanel">
        <collectionProp name="Arguments.arguments">
          <elementProp name="baseUrl" elementType="Argument">
            <stringProp name="Argument.name">baseUrl</stringProp>
            <stringProp name="Argument.value">http://localhost:9001/api</stringProp>
          </elementProp>
        </collectionProp>
      </elementProp>
    </TestPlan>
    <hashTree>
      <ThreadGroup testname="Driver Dashboard Load Test">
        <stringProp name="ThreadGroup.num_threads">100</stringProp>
        <stringProp name="ThreadGroup.ramp_time">60</stringProp>
        <stringProp name="ThreadGroup.duration">300</stringProp>
        <stringProp name="ThreadGroup.delay"></stringProp>
        <boolProp name="ThreadGroup.scheduler">true</boolProp>
      </ThreadGroup>
    </hashTree>
  </hashTree>
</jmeterTestPlan>
```

### **API Performance Test**
```bash
#!/bin/bash
# performance_test.sh

BASE_URL="http://localhost:9001/api"
TOKEN="your-jwt-token-here"

echo "Starting performance test..."

# Test login performance
echo "Testing login performance..."
for i in {1..100}; do
  curl -s -X POST $BASE_URL/auth/login \
    -H "Content-Type: application/json" \
    -d '{"loginId":"appadmin","password":"admin123"}' \
    -w "%{time_total}\n" -o /dev/null
done

# Test dashboard performance
echo "Testing dashboard performance..."
for i in {1..100}; do
  curl -s -X GET $BASE_URL/drivers/1/dashboard \
    -H "Authorization: Bearer $TOKEN" \
    -w "%{time_total}\n" -o /dev/null
done
```

---

## 🔒 **Security Testing**

### **Authentication Security Tests**

#### **JWT Token Tests**
```java
// src/test/java/com/app/security/JwtSecurityTest.java
@SpringBootTest
@AutoConfigureTestDatabase
class JwtSecurityTest {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Test
    void generateToken_ShouldCreateValidToken() {
        // Given
        Authentication authentication = createMockAuthentication();
        
        // When
        String token = tokenProvider.generateToken(authentication);
        
        // Then
        assertThat(token).isNotNull();
        assertThat(tokenProvider.validateToken(token)).isTrue();
    }
    
    @Test
    void validateToken_ShouldReturnFalse_WhenTokenExpired() {
        // Given
        String expiredToken = createExpiredToken();
        
        // When
        boolean isValid = tokenProvider.validateToken(expiredToken);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    void validateToken_ShouldReturnFalse_WhenTokenInvalid() {
        // Given
        String invalidToken = "invalid.token.here";
        
        // When
        boolean isValid = tokenProvider.validateToken(invalidToken);
        
        // Then
        assertThat(isValid).isFalse();
    }
}
```

#### **API Security Tests**
```java
// src/test/java/com/app/security/ApiSecurityTest.java
@SpringBootTest
@AutoConfigureTestDatabase
class ApiSecurityTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void protectedEndpoints_ShouldRequireAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/drivers/1/dashboard"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void protectedEndpoints_ShouldAcceptValidToken() throws Exception {
        // Given
        String validToken = createValidToken();
        
        // When & Then
        mockMvc.perform(get("/api/drivers/1/dashboard")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }
    
    @Test
    void protectedEndpoints_ShouldRejectInvalidToken() throws Exception {
        // Given
        String invalidToken = "invalid.token.here";
        
        // When & Then
        mockMvc.perform(get("/api/drivers/1/dashboard")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }
}
```

---

## 📊 **Test Reporting**

### **Test Coverage Report**

#### **JaCoCo Configuration**
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### **Generate Coverage Report**
```bash
# Run tests with coverage
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### **Flutter Test Coverage**
```bash
# Run Flutter tests with coverage
flutter test --coverage

# Generate HTML coverage report
genhtml coverage/lcov.info -o coverage/html

# View coverage report
open coverage/html/index.html
```

---

## 🧪 **Test Automation**

### **CI/CD Pipeline Tests**

#### **GitHub Actions Test Workflow**
```yaml
# .github/workflows/test.yml
name: Test Application

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Setup Java
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'
    
    - name: Cache Maven dependencies
      uses: actions/cache@v2
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Run tests
      run: mvn clean test
    
    - name: Generate coverage report
      run: mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v2
      with:
        file: target/site/jacoco/jacoco.xml

  frontend-tests:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Setup Flutter
      uses: subosito/flutter-action@v2
      with:
        flutter-version: '3.0.0'
    
    - name: Install dependencies
      run: flutter pub get
    
    - name: Run tests
      run: flutter test --coverage
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v2
      with:
        file: coverage/lcov.info
```

---

## 📋 **Testing Checklist**

### **Unit Testing Checklist**
- [ ] All service methods tested
- [ ] All repository methods tested
- [ ] All controller endpoints tested
- [ ] Edge cases covered
- [ ] Error scenarios tested
- [ ] Mock objects used appropriately
- [ ] Test data isolated
- [ ] Assertions comprehensive

### **Integration Testing Checklist**
- [ ] API endpoints tested
- [ ] Database operations tested
- [ ] WebSocket connections tested
- [ ] Authentication flow tested
- [ ] File upload/download tested
- [ ] Email functionality tested
- [ ] Real-time notifications tested

### **Performance Testing Checklist**
- [ ] Load testing completed
- [ ] Stress testing completed
- [ ] Response time benchmarks met
- [ ] Memory usage optimized
- [ ] Database performance tested
- [ ] Concurrent user testing
- [ ] API rate limiting tested

### **Security Testing Checklist**
- [ ] Authentication security tested
- [ ] Authorization security tested
- [ ] Input validation tested
- [ ] SQL injection prevention tested
- [ ] XSS prevention tested
- [ ] CSRF protection tested
- [ ] JWT token security tested

---

## 🚨 **Troubleshooting Tests**

### **Common Test Issues**

#### **Backend Test Issues**
```bash
# Database connection issues
# Check test database configuration
# Verify testcontainers setup

# Mock issues
# Check mockito setup
# Verify mock configurations

# Transaction issues
# Check @Transactional annotations
# Verify test isolation
```

#### **Frontend Test Issues**
```bash
# Widget test issues
# Check widget binding
# Verify test environment setup

# Service test issues
# Check mock configurations
# Verify HTTP client mocking

# Integration test issues
# Check device/emulator setup
# Verify test dependencies
```

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Status**: Production Ready  
**Test Coverage**: 80%+  
**Test Types**: Unit, Integration, E2E, Performance, Security  
**Automation**: CI/CD Pipeline  
**Tools**: JUnit, Mockito, TestContainers, Flutter Test, Postman, JMeter
