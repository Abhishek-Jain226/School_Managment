# Backend Structure Documentation

## ⚙️ **Spring Boot Application Overview**

**Framework**: Spring Boot 3.x  
**Language**: Java 17  
**Build Tool**: Maven  
**Database**: MySQL 8.0  
**Security**: Spring Security + JWT  
**Real-time**: WebSocket with STOMP  
**Architecture**: Layered Architecture with Clean Code Principles  

---

## 📁 **Project Structure**

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── app/
│   │           ├── KidsVehicleTrackingApplication.java    # Main application class
│   │           ├── config/                                # Configuration classes
│   │           │   ├── DataLoader.java                   # CommandLineRunner for initial data
│   │           │   ├── SecurityConfig.java               # Security configuration
│   │           │   ├── WebConfig.java                    # Web configuration
│   │           │   └── WebSocketConfig.java              # WebSocket configuration
│   │           ├── controller/                           # REST controllers
│   │           │   ├── AuthController.java               # Authentication endpoints
│   │           │   ├── AppAdminController.java           # AppAdmin operations
│   │           │   ├── SchoolAdminController.java        # SchoolAdmin operations
│   │           │   ├── VehicleOwnerController.java       # VehicleOwner operations
│   │           │   ├── DriverController.java             # Driver operations
│   │           │   ├── StudentController.java            # Student operations
│   │           │   ├── SchoolController.java             # School operations
│   │           │   ├── VehicleController.java            # Vehicle operations
│   │           │   ├── TripController.java               # Trip operations
│   │           │   ├── NotificationController.java       # Notification endpoints
│   │           │   └── PendingUserController.java        # Pending user operations
│   │           ├── entity/                               # JPA entities
│   │           │   ├── User.java                         # User entity
│   │           │   ├── Role.java                         # Role entity
│   │           │   ├── UserRole.java                     # UserRole entity
│   │           │   ├── School.java                       # School entity
│   │           │   ├── SchoolUser.java                   # SchoolUser entity
│   │           │   ├── Vehicle.java                      # Vehicle entity
│   │           │   ├── VehicleOwner.java                 # VehicleOwner entity
│   │           │   ├── Driver.java                       # Driver entity
│   │           │   ├── VehicleDriver.java                # VehicleDriver entity
│   │           │   ├── Student.java                      # Student entity
│   │           │   ├── StudentParent.java                # StudentParent entity
│   │           │   ├── Trip.java                         # Trip entity
│   │           │   ├── TripStudent.java                  # TripStudent entity
│   │           │   ├── TripStatus.java                   # TripStatus entity
│   │           │   ├── DispatchLog.java                  # DispatchLog entity
│   │           │   └── Notification.java                 # Notification entity
│   │           ├── Enum/                                 # Enum classes
│   │           │   ├── GenderType.java                   # Gender enum
│   │           │   ├── VehicleType.java                  # Vehicle type enum
│   │           │   ├── TripType.java                     # Trip type enum
│   │           │   ├── TripStatusType.java               # Trip status enum
│   │           │   ├── EventType.java                    # Event type enum
│   │           │   ├── NotificationType.java             # Notification type enum
│   │           │   ├── UserStatus.java                   # User status enum
│   │           │   └── VehicleStatus.java                # Vehicle status enum
│   │           ├── exception/                            # Exception handling
│   │           │   ├── ResourceNotFoundException.java    # Custom exception
│   │           │   └── handler/                          # Exception handlers
│   │           │       └── GlobalExceptionHandler.java   # Global exception handler
│   │           ├── payload/                              # DTOs and payloads
│   │           │   ├── request/                          # Request DTOs
│   │           │   │   ├── LoginRequestDto.java          # Login request
│   │           │   │   ├── ForgotPasswordRequestDto.java # Forgot password request
│   │           │   │   ├── ResetPasswordRequestDto.java  # Reset password request
│   │           │   │   ├── SchoolRequestDto.java         # School request
│   │           │   │   ├── DriverRequestDto.java         # Driver request
│   │           │   │   ├── StudentRequestDto.java        # Student request
│   │           │   │   ├── TripRequestDto.java           # Trip request
│   │           │   │   ├── NotificationRequestDto.java   # Notification request
│   │           │   │   └── StudentAttendanceRequestDto.java # Attendance request
│   │           │   └── response/                         # Response DTOs
│   │           │       ├── ApiResponse.java              # Generic API response
│   │           │       ├── LoginResponseDto.java         # Login response
│   │           │       ├── SchoolResponseDto.java        # School response
│   │           │       ├── DriverResponseDto.java        # Driver response
│   │           │       ├── StudentResponseDto.java       # Student response
│   │           │       ├── TripResponseDto.java          # Trip response
│   │           │       ├── DriverDashboardResponseDto.java # Driver dashboard response
│   │           │       ├── DriverProfileResponseDto.java # Driver profile response
│   │           │       ├── DriverReportsResponseDto.java # Driver reports response
│   │           │       ├── TimeBasedTripsResponseDto.java # Time-based trips response
│   │           │       ├── TripStatusResponseDto.java    # Trip status response
│   │           │       └── WebSocketNotificationDto.java # WebSocket notification
│   │           ├── repository/                           # JPA repositories
│   │           │   ├── UserRepository.java               # User repository
│   │           │   ├── RoleRepository.java               # Role repository
│   │           │   ├── UserRoleRepository.java           # UserRole repository
│   │           │   ├── SchoolRepository.java             # School repository
│   │           │   ├── SchoolUserRepository.java         # SchoolUser repository
│   │           │   ├── VehicleRepository.java            # Vehicle repository
│   │           │   ├── VehicleOwnerRepository.java       # VehicleOwner repository
│   │           │   ├── DriverRepository.java             # Driver repository
│   │           │   ├── VehicleDriverRepository.java      # VehicleDriver repository
│   │           │   ├── StudentRepository.java            # Student repository
│   │           │   ├── StudentParentRepository.java      # StudentParent repository
│   │           │   ├── TripRepository.java               # Trip repository
│   │           │   ├── TripStudentRepository.java        # TripStudent repository
│   │           │   ├── TripStatusRepository.java         # TripStatus repository
│   │           │   ├── DispatchLogRepository.java        # DispatchLog repository
│   │           │   └── NotificationRepository.java       # Notification repository
│   │           ├── security/                             # Security components
│   │           │   ├── JwtAuthenticationFilter.java      # JWT filter
│   │           │   └── JwtTokenProvider.java             # JWT token provider
│   │           └── service/                              # Service layer
│   │               ├── IAuthService.java                 # Auth service interface
│   │               ├── IAppAdminService.java             # AppAdmin service interface
│   │               ├── ISchoolService.java               # School service interface
│   │               ├── IDriverService.java               # Driver service interface
│   │               ├── IStudentService.java              # Student service interface
│   │               ├── ITripService.java                 # Trip service interface
│   │               ├── IVehicleOwnerService.java         # VehicleOwner service interface
│   │               ├── IPendingUserService.java          # PendingUser service interface
│   │               ├── ISchoolAdminService.java          # SchoolAdmin service interface
│   │               ├── WebSocketNotificationService.java # WebSocket service
│   │               └── impl/                             # Service implementations
│   │                   ├── AuthServiceImpl.java          # Auth service implementation
│   │                   ├── AppAdminServiceImpl.java      # AppAdmin service implementation
│   │                   ├── SchoolServiceImpl.java        # School service implementation
│   │                   ├── DriverServiceImpl.java        # Driver service implementation
│   │                   ├── StudentServiceImpl.java       # Student service implementation
│   │                   ├── TripServiceImpl.java          # Trip service implementation
│   │                   ├── VehicleOwnerServiceImpl.java  # VehicleOwner service implementation
│   │                   ├── PendingUserServiceImpl.java   # PendingUser service implementation
│   │                   └── SchoolAdminServiceImpl.java   # SchoolAdmin service implementation
│   └── resources/
│       ├── application.properties                        # Main properties
│       ├── application-dev.properties                    # Development properties
│       ├── application-prod.properties                   # Production properties
│       └── db/
│           └── migration/                                # Database migration scripts
└── test/
    └── java/
        └── com/
            └── app/
                └── KidsVehicleTrackingApplicationTests.java # Test class
```

---

## 🏗️ **Architecture Layers**

### **1. Controller Layer**
```java
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {
    
    private final IDriverService driverService;
    
    @GetMapping("/{driverId}/dashboard")
    public ResponseEntity<ApiResponse> getDriverDashboard(@PathVariable Integer driverId) {
        ApiResponse response = driverService.getDriverDashboard(driverId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{driverId}/attendance")
    public ResponseEntity<ApiResponse> markStudentAttendance(
            @PathVariable Integer driverId,
            @RequestBody @Valid StudentAttendanceRequestDto request) {
        ApiResponse response = driverService.markStudentAttendance(driverId, request);
        return ResponseEntity.ok(response);
    }
}
```

### **2. Service Layer**
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements IDriverService {
    
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;
    private final DispatchLogRepository dispatchLogRepository;
    private final WebSocketNotificationService webSocketService;
    
    @Override
    @Transactional(readOnly = true)
    public ApiResponse getDriverDashboard(Integer driverId) {
        try {
            Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
            
            DriverDashboardResponseDto dashboard = buildDriverDashboard(driver);
            
            return ApiResponse.builder()
                .success(true)
                .message("Driver dashboard retrieved successfully")
                .data(dashboard)
                .build();
                
        } catch (Exception e) {
            log.error("Error retrieving driver dashboard for driver ID: {}", driverId, e);
            return ApiResponse.builder()
                .success(false)
                .message("Failed to retrieve driver dashboard")
                .build();
        }
    }
}
```

### **3. Repository Layer**
```java
@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {
    
    Optional<Driver> findByUserId(Integer userId);
    
    List<Driver> findByIsActiveTrue();
    
    @Query("SELECT d FROM Driver d WHERE d.userId = :userId AND d.isActive = true")
    Optional<Driver> findActiveDriverByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT d FROM Driver d JOIN d.vehicleDrivers vd WHERE vd.schoolId = :schoolId AND vd.isActive = true")
    List<Driver> findDriversBySchoolId(@Param("schoolId") Integer schoolId);
}
```

### **4. Entity Layer**
```java
@Entity
@Table(name = "drivers", indexes = {
    @Index(name = "idx_drivers_user_id", columnList = "u_id"),
    @Index(name = "idx_drivers_contact", columnList = "driver_contact_number"),
    @Index(name = "idx_drivers_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private Integer driverId;
    
    @Column(name = "u_id")
    private Integer userId;
    
    @Column(name = "driver_name", nullable = false, length = 150)
    private String driverName;
    
    @Column(name = "driver_contact_number", nullable = false, length = 20, unique = true)
    private String driverContactNumber;
    
    @Column(name = "driver_address", length = 300)
    private String driverAddress;
    
    @Lob
    @Column(name = "driver_photo")
    private String driverPhoto;
    
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_by", length = 50)
    private String createdBy;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
```

---

## 🔧 **Configuration Classes**

### **1. Security Configuration**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**", "/api/pending-users/**", "/api/public/**", "/activation", "/ws/**", "/app/**")
                .permitAll()
                .anyRequest()
                .authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### **2. WebSocket Configuration**
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

### **3. Data Loader (Initial Data)**
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        createRoles();
        createAppAdmin();
    }
    
    private void createRoles() {
        if (roleRepository.count() == 0) {
            List<Role> roles = Arrays.asList(
                Role.builder().roleName("APP_ADMIN").roleDescription("Application Administrator").build(),
                Role.builder().roleName("SCHOOL_ADMIN").roleDescription("School Administrator").build(),
                Role.builder().roleName("VEHICLE_OWNER").roleDescription("Vehicle Owner").build(),
                Role.builder().roleName("DRIVER").roleDescription("Driver").build(),
                Role.builder().roleName("PARENT").roleDescription("Parent").build()
            );
            roleRepository.saveAll(roles);
            log.info("Roles created successfully");
        }
    }
    
    private void createAppAdmin() {
        if (!userRepository.existsByUserName("appadmin")) {
            User appAdmin = User.builder()
                .userName("appadmin")
                .email("appadmin@example.com")
                .contactNumber("9876543210")
                .password(passwordEncoder.encode("admin123"))
                .isActive(true)
                .createdBy("SYSTEM")
                .build();
            
            userRepository.save(appAdmin);
            
            Role appAdminRole = roleRepository.findByRoleName("APP_ADMIN")
                .orElseThrow(() -> new RuntimeException("APP_ADMIN role not found"));
            
            UserRole userRole = UserRole.builder()
                .user(appAdmin)
                .role(appAdminRole)
                .isActive(true)
                .createdBy("SYSTEM")
                .build();
            
            userRoleRepository.save(userRole);
            log.info("AppAdmin user created successfully");
        }
    }
}
```

---

## 🔒 **Security Implementation**

### **JWT Token Provider**
```java
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;
    
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);
        
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }
}
```

### **JWT Authentication Filter**
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                
                User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                
                UserPrincipal userPrincipal = UserPrincipal.create(user);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

---

## 🌐 **WebSocket Implementation**

### **WebSocket Notification Service**
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public void sendNotificationToUser(String username, WebSocketNotificationDto notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                notification
            );
            log.debug("Notification sent to user: {}", username);
        } catch (Exception e) {
            log.error("Failed to send notification to user: {}", username, e);
        }
    }
    
    public void sendNotificationToSchool(Integer schoolId, WebSocketNotificationDto notification) {
        try {
            messagingTemplate.convertAndSend(
                "/topic/school/" + schoolId,
                notification
            );
            log.debug("Notification sent to school: {}", schoolId);
        } catch (Exception e) {
            log.error("Failed to send notification to school: {}", schoolId, e);
        }
    }
    
    public void sendNotificationToRole(String role, WebSocketNotificationDto notification) {
        try {
            messagingTemplate.convertAndSend(
                "/topic/role/" + role,
                notification
            );
            log.debug("Notification sent to role: {}", role);
        } catch (Exception e) {
            log.error("Failed to send notification to role: {}", role, e);
        }
    }
}
```

### **WebSocket Controller**
```java
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    
    private final WebSocketNotificationService notificationService;
    
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public WebSocketNotificationDto sendMessage(@Payload WebSocketNotificationDto chatMessage,
                                               SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderName());
        return chatMessage;
    }
    
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public WebSocketNotificationDto addUser(@Payload WebSocketNotificationDto chatMessage,
                                           SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderName());
        return chatMessage;
    }
}
```

---

## 📊 **Database Configuration**

### **Application Properties**
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/Kids_Vehicle_tracking_Db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# HikariCP Connection Pool Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.leak-detection-threshold=60000

# JWT Configuration
app.jwt.secret=mySecretKey
app.jwt.expiration=86400000

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Server Configuration
server.port=9001
server.servlet.context-path=/
```

---

## 🚀 **Performance Optimization**

### **Database Indexing**
```java
@Entity
@Table(name = "dispatch_logs", indexes = {
    @Index(name = "idx_dispatch_logs_created_date", columnList = "created_date"),
    @Index(name = "idx_dispatch_logs_event_type", columnList = "event_type"),
    @Index(name = "idx_dispatch_logs_driver_id", columnList = "driver_id"),
    @Index(name = "idx_dispatch_logs_trip_student", columnList = "trip_id, student_id")
})
public class DispatchLog {
    // Entity fields
}
```

### **Query Optimization**
```java
@Repository
public interface DispatchLogRepository extends JpaRepository<DispatchLog, Integer> {
    
    @Query("SELECT dl FROM DispatchLog dl WHERE dl.trip = :trip AND dl.student = :student ORDER BY dl.createdDate DESC")
    List<DispatchLog> findByTripAndStudent(@Param("trip") Trip trip, @Param("student") Student student);
    
    @Query("SELECT dl FROM DispatchLog dl WHERE dl.driver = :driver ORDER BY dl.createdDate DESC")
    List<DispatchLog> findByDriverOrderByCreatedDateDesc(@Param("driver") Driver driver);
    
    @Query("SELECT COUNT(dl) FROM DispatchLog dl WHERE dl.driver = :driver AND dl.eventType = :eventType AND DATE(dl.createdDate) = CURRENT_DATE")
    Long countByDriverAndEventTypeToday(@Param("driver") Driver driver, @Param("eventType") EventType eventType);
}
```

### **Transaction Management**
```java
@Service
@Transactional
public class DriverServiceImpl implements IDriverService {
    
    @Override
    @Transactional(readOnly = true)
    public ApiResponse getDriverDashboard(Integer driverId) {
        // Read-only transaction for queries
    }
    
    @Override
    @Transactional
    public ApiResponse markStudentAttendance(Integer driverId, StudentAttendanceRequestDto request) {
        // Read-write transaction for updates
    }
}
```

---

## 🔧 **Exception Handling**

### **Global Exception Handler**
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build());
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> handleValidationException(ValidationException ex) {
        log.error("Validation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.builder()
                .success(false)
                .message("An unexpected error occurred")
                .build());
    }
}
```

---

## 📝 **Logging Configuration**

### **Logback Configuration**
```xml
<!-- src/main/resources/logback-spring.xml -->
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

---

## 🧪 **Testing Structure**

### **Unit Test Example**
```java
@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {
    
    @Mock
    private DriverRepository driverRepository;
    
    @Mock
    private TripRepository tripRepository;
    
    @InjectMocks
    private DriverServiceImpl driverService;
    
    @Test
    void getDriverDashboard_ShouldReturnDashboard_WhenDriverExists() {
        // Given
        Integer driverId = 1;
        Driver driver = Driver.builder()
            .driverId(driverId)
            .driverName("John Doe")
            .build();
        
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        
        // When
        ApiResponse response = driverService.getDriverDashboard(driverId);
        
        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Driver dashboard retrieved successfully");
    }
}
```

---

## 🚀 **Build Configuration**

### **Maven Configuration**
```xml
<!-- pom.xml -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
    <relativePath/>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
    
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.9.1</version>
    </dependency>
    
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Status**: Production Ready
