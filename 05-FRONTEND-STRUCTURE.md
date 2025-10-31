# Frontend Structure Documentation

## 📱 **Flutter Application Overview**

**Framework**: Flutter 3.x  
**Language**: Dart  
**State Management**: StatefulWidget + Custom StateManager  
**Architecture**: Clean Architecture with Repository Pattern  
**Platform Support**: Android, iOS, Web  

---

## 📁 **Project Structure**

```
lib/
├── main.dart                           # Application entry point
├── app.dart                           # App configuration and routing
├── app_routes.dart                    # Route definitions and navigation
├── config/
│   ├── app_config.dart                # Application configuration
│   └── environment.dart               # Environment variables
├── core/
│   ├── constants/                     # Application constants
│   │   ├── app_constants.dart         # General constants
│   │   ├── api_constants.dart         # API endpoints
│   │   └── ui_constants.dart          # UI constants
│   ├── themes/                        # App themes and styling
│   │   ├── app_theme.dart             # Main theme configuration
│   │   ├── color_scheme.dart          # Color definitions
│   │   └── text_styles.dart           # Typography styles
│   └── utils/                         # Utility functions
│       ├── validators.dart            # Input validation
│       ├── formatters.dart            # Data formatters
│       └── helpers.dart               # Helper functions
├── data/
│   ├── models/                        # Data models (DTOs)
│   │   ├── user.dart                  # User model
│   │   ├── school.dart                # School model
│   │   ├── driver.dart                # Driver model
│   │   ├── vehicle.dart               # Vehicle model
│   │   ├── student.dart               # Student model
│   │   ├── trip.dart                  # Trip model
│   │   ├── notification.dart          # Notification model
│   │   ├── driver_profile.dart        # Driver profile model
│   │   ├── driver_reports.dart        # Driver reports model
│   │   ├── notification_request.dart  # Notification request model
│   │   └── websocket_notification.dart # WebSocket notification model
│   ├── repositories/                  # Data repositories
│   │   ├── auth_repository.dart       # Authentication repository
│   │   ├── school_repository.dart     # School data repository
│   │   ├── driver_repository.dart     # Driver data repository
│   │   └── parent_repository.dart     # Parent data repository
│   └── services/                      # API services
│       ├── auth_service.dart          # Authentication service
│       ├── school_service.dart        # School operations
│       ├── driver_service.dart        # Driver operations
│       ├── parent_service.dart        # Parent operations
│       ├── base_http_service.dart     # Base HTTP service
│       └── websocket_notification_service.dart # WebSocket service
├── domain/
│   ├── entities/                      # Business entities
│   │   ├── user_entity.dart           # User business entity
│   │   ├── school_entity.dart         # School business entity
│   │   └── trip_entity.dart           # Trip business entity
│   ├── repositories/                  # Repository interfaces
│   │   ├── auth_repository_interface.dart
│   │   ├── school_repository_interface.dart
│   │   └── driver_repository_interface.dart
│   └── usecases/                      # Business logic
│       ├── login_usecase.dart         # Login business logic
│       ├── school_management_usecase.dart
│       └── driver_operations_usecase.dart
├── presentation/
│   ├── pages/                         # UI pages/screens
│   │   ├── auth/                      # Authentication pages
│   │   │   ├── login_screen.dart      # Login page
│   │   │   ├── forgot_password_screen.dart
│   │   │   └── reset_password_screen.dart
│   │   ├── app_admin/                 # AppAdmin pages
│   │   │   ├── app_admin_dashboard.dart
│   │   │   ├── school_management_page.dart
│   │   │   └── app_admin_profile_page.dart
│   │   ├── school_admin/              # SchoolAdmin pages
│   │   │   ├── school_admin_dashboard.dart
│   │   │   ├── staff_management_page.dart
│   │   │   ├── student_management_page.dart
│   │   │   └── vehicle_management_page.dart
│   │   ├── vehicle_owner/             # VehicleOwner pages
│   │   │   ├── vehicle_owner_dashboard.dart
│   │   │   ├── driver_management_page.dart
│   │   │   └── vehicle_owner_profile_page.dart
│   │   ├── driver/                    # Driver pages
│   │   │   ├── simplified_driver_dashboard.dart
│   │   │   ├── driver_profile_page.dart
│   │   │   ├── driver_reports_page.dart
│   │   │   └── simplified_student_management_page.dart
│   │   ├── parent/                    # Parent pages
│   │   │   ├── parent_dashboard_page.dart
│   │   │   ├── vehicle_tracking_page.dart
│   │   │   └── enhanced_vehicle_tracking_page.dart
│   │   └── common/                    # Common pages
│   │       ├── profile_page.dart      # Generic profile page
│   │       └── settings_page.dart     # Settings page
│   ├── widgets/                       # Reusable widgets
│   │   ├── common/                    # Common widgets
│   │   │   ├── custom_app_bar.dart    # Custom app bar
│   │   │   ├── custom_button.dart     # Custom button
│   │   │   ├── custom_text_field.dart # Custom text field
│   │   │   ├── loading_widget.dart    # Loading indicator
│   │   │   └── error_widget.dart      # Error display
│   │   ├── forms/                     # Form widgets
│   │   │   ├── school_registration_form.dart
│   │   │   ├── driver_registration_form.dart
│   │   │   └── student_registration_form.dart
│   │   └── cards/                     # Card widgets
│   │       ├── school_card.dart       # School display card
│   │       ├── driver_card.dart       # Driver display card
│   │       └── student_card.dart      # Student display card
│   └── providers/                     # State providers
│       ├── auth_provider.dart         # Authentication state
│       ├── school_provider.dart       # School state
│       └── driver_provider.dart       # Driver state
├── services/                          # External services
│   ├── auth_service.dart              # Authentication service
│   ├── driver_service.dart            # Driver operations
│   ├── school_service.dart            # School operations
│   ├── parent_service.dart            # Parent operations
│   ├── base_http_service.dart         # Base HTTP service
│   └── websocket_notification_service.dart # WebSocket service
└── utils/                             # Utility classes
    ├── state_manager.dart             # State management utilities
    ├── error_handler.dart             # Error handling utilities
    └── loading_widgets.dart           # Loading UI components
```

---

## 🎨 **UI Architecture**

### **Widget Hierarchy**
```
MaterialApp
├── AppRoutes (Navigation)
├── Theme (App Theme)
├── Scaffold
│   ├── AppBar (Custom App Bar)
│   ├── Body (Page Content)
│   │   ├── LoadingWidget (Loading State)
│   │   ├── ErrorWidget (Error State)
│   │   └── ContentWidget (Success State)
│   └── BottomNavigationBar (Navigation)
└── WebSocketService (Real-time Updates)
```

### **State Management Pattern**
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  State Manager  │    │   Data Layer    │
│                 │    │                 │    │                 │
│ ┌─────────────┐ │    │ ┌─────────────┐ │    │ ┌─────────────┐ │
│ │   Widgets   │ │◄──►│ │   State     │ │◄──►│ │  Services   │ │
│ │             │ │    │ │  Manager    │ │    │ │             │ │
│ └─────────────┘ │    │ └─────────────┘ │    │ └─────────────┘ │
│                 │    │                 │    │                 │
│ ┌─────────────┐ │    │ ┌─────────────┐ │    │ ┌─────────────┐ │
│ │   Pages     │ │◄──►│ │   Events    │ │◄──►│ │  Repos      │ │
│ │             │ │    │ │             │ │    │ │             │ │
│ └─────────────┘ │    │ └─────────────┘ │    │ └─────────────┘ │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

---

## 🔧 **Key Components**

### **1. Base HTTP Service**
```dart
class BaseHttpService {
  final AuthService _auth = AuthService();

  Map<String, String> _getHeaders({String? contentType}) {
    final token = _auth.getTokenSync();
    return {
      "Content-Type": contentType ?? "application/json",
      if (token != null) "Authorization": "Bearer $token",
    };
  }

  Future<http.Response> get(String url, {Map<String, String>? headers}) async {
    final uri = Uri.parse(url);
    final response = await http.get(uri, headers: headers ?? _getHeaders());
    return response;
  }

  Future<http.Response> post(String url, {Map<String, dynamic>? body, Map<String, String>? headers}) async {
    final uri = Uri.parse(url);
    final response = await http.post(uri, headers: headers ?? _getHeaders(), body: jsonEncode(body));
    return response;
  }

  Map<String, dynamic> handleResponse(http.Response response, {String operation = 'Operation'}) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      return jsonDecode(response.body) as Map<String, dynamic>;
    } else {
      throw ErrorHandler.handleHttpError(response);
    }
  }
}
```

### **2. State Manager**
```dart
class StateManager<T> extends ChangeNotifier {
  T? _state;
  String? _error;
  bool _isLoading = false;

  T? get state => _state;
  String? get error => _error;
  bool get isLoading => _isLoading;

  void setState(T newState) {
    _state = newState;
    _error = null;
    notifyListeners();
  }

  void setError(String errorMessage) {
    _error = errorMessage;
    notifyListeners();
  }

  void setLoading(bool loading) {
    _isLoading = loading;
    notifyListeners();
  }

  Future<void> execute(Future<T> Function() operation) async {
    setLoading(true);
    setError(null);
    try {
      final result = await operation();
      setState(result);
    } catch (e) {
      setError(e.toString());
    } finally {
      setLoading(false);
    }
  }
}
```

### **3. Error Handler**
```dart
class ErrorHandler {
  static AppException handleHttpError(http.Response response) {
    final statusCode = response.statusCode;
    String message;
    String? code;

    try {
      final responseBody = jsonDecode(response.body);
      message = responseBody['message'] ?? 'An unexpected error occurred.';
      code = responseBody['code']?.toString();
    } catch (e) {
      message = 'Failed to parse server response.';
      code = 'PARSE_ERROR';
    }

    switch (statusCode) {
      case 400:
        return ValidationException(message, code: code);
      case 401:
      case 403:
        return AuthenticationException(message, code: code);
      case 404:
        return NetworkException(message, code: code);
      case 500:
        return ServerException(message, code: code);
      default:
        return AppException(message, code: code);
    }
  }

  static void showErrorSnackBar(BuildContext context, AppException error) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(error.message),
        backgroundColor: Colors.red,
      ),
    );
  }
}
```

### **4. Loading Widgets**
```dart
class LoadingWidgets {
  static Widget standardLoading({String? message}) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const CircularProgressIndicator(),
          if (message != null) ...[
            const SizedBox(height: 16),
            Text(
              message,
              style: const TextStyle(fontSize: 16),
              textAlign: TextAlign.center,
            ),
          ],
        ],
      ),
    );
  }

  static Widget buttonLoading({double size = 16, Color? color}) {
    return SizedBox(
      width: size,
      height: size,
      child: CircularProgressIndicator(
        strokeWidth: 2,
        valueColor: AlwaysStoppedAnimation<Color>(color ?? Colors.white),
      ),
    );
  }
}
```

---

## 🎯 **Page-Specific Architecture**

### **1. Driver Dashboard**
```dart
class SimplifiedDriverDashboard extends StatefulWidget {
  @override
  _SimplifiedDriverDashboardState createState() => _SimplifiedDriverDashboardState();
}

class _SimplifiedDriverDashboardState extends State<SimplifiedDriverDashboard> 
    with LoadingStateMixin, ErrorHandlingMixin {
  
  // State variables
  DriverDashboard? _driverDashboard;
  DriverReports? _driverReports;
  List<Trip> _morningTrips = [];
  List<Trip> _afternoonTrips = [];
  Trip? _selectedTrip;
  String _selectedTripType = 'MORNING';
  bool _isTripActive = false;
  
  // Services
  final DriverService _driverService = DriverService();
  final WebSocketNotificationService _webSocketService = WebSocketNotificationService();
  
  @override
  void initState() {
    super.initState();
    _loadDriverData();
    _initializeWebSocket();
  }
  
  Future<void> _loadDriverData() async {
    await executeWithLoading(() async {
      final dashboard = await _driverService.getDriverDashboard();
      final reports = await _driverService.getDriverReports();
      final trips = await _driverService.getAssignedTrips();
      
      setState(() {
        _driverDashboard = dashboard;
        _driverReports = reports;
        _morningTrips = trips.where((t) => t.tripType == 'MORNING_PICKUP').toList();
        _afternoonTrips = trips.where((t) => t.tripType == 'AFTERNOON_DROP').toList();
      });
    });
  }
}
```

### **2. School Management**
```dart
class SchoolManagementPage extends StatefulWidget {
  @override
  _SchoolManagementPageState createState() => _SchoolManagementPageState();
}

class _SchoolManagementPageState extends State<SchoolManagementPage> 
    with LoadingStateMixin, ErrorHandlingMixin {
  
  List<School> _schools = [];
  final SchoolService _schoolService = SchoolService();
  
  @override
  void initState() {
    super.initState();
    _loadSchools();
  }
  
  Future<void> _loadSchools() async {
    await executeWithLoading(() async {
      final schools = await _schoolService.getAllSchools();
      setState(() {
        _schools = schools;
      });
    });
  }
}
```

---

## 🔄 **Navigation Architecture**

### **App Routes**
```dart
class AppRoutes {
  // Authentication routes
  static const String login = '/login';
  static const String forgotPassword = '/forgot-password';
  static const String resetPassword = '/reset-password';
  
  // Dashboard routes
  static const String appAdminDashboard = '/app-admin-dashboard';
  static const String schoolAdminDashboard = '/school-admin-dashboard';
  static const String vehicleOwnerDashboard = '/vehicle-owner-dashboard';
  static const String simplifiedDriverDashboard = '/simplified-driver-dashboard';
  static const String parentDashboard = '/parent-dashboard';
  
  // Management routes
  static const String schoolManagement = '/school-management';
  static const String staffManagement = '/staff-management';
  static const String studentManagement = '/student-management';
  static const String driverManagement = '/driver-management';
  
  // Profile routes
  static const String appAdminProfile = '/app-admin-profile';
  static const String driverProfile = '/driver-profile';
  static const String vehicleOwnerProfile = '/vehicle-owner-profile';
  
  // Tracking routes
  static const String vehicleTracking = '/vehicle-tracking';
  static const String enhancedVehicleTracking = '/enhanced-vehicle-tracking';
  
  // Student management routes
  static const String simplifiedStudentManagement = '/simplified-student-management';
  
  // Reports routes
  static const String driverReports = '/driver-reports';
}
```

### **Navigation Helper**
```dart
class NavigationHelper {
  static void navigateTo(BuildContext context, String routeName, {Object? arguments}) {
    Navigator.pushNamed(context, routeName, arguments: arguments);
  }
  
  static void navigateAndReplace(BuildContext context, String routeName, {Object? arguments}) {
    Navigator.pushReplacementNamed(context, routeName, arguments: arguments);
  }
  
  static void navigateAndClearStack(BuildContext context, String routeName, {Object? arguments}) {
    Navigator.pushNamedAndRemoveUntil(
      context, 
      routeName, 
      (route) => false,
      arguments: arguments,
    );
  }
}
```

---

## 🎨 **Theme Architecture**

### **App Theme**
```dart
class AppTheme {
  static ThemeData get lightTheme {
    return ThemeData(
      primarySwatch: Colors.blue,
      primaryColor: AppColors.primary,
      scaffoldBackgroundColor: AppColors.background,
      appBarTheme: AppBarTheme(
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8),
          ),
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: BorderSide(color: AppColors.primary),
        ),
      ),
    );
  }
}
```

### **Color Scheme**
```dart
class AppColors {
  static const Color primary = Color(0xFF2196F3);
  static const Color secondary = Color(0xFF03DAC6);
  static const Color background = Color(0xFFF5F5F5);
  static const Color surface = Color(0xFFFFFFFF);
  static const Color error = Color(0xFFB00020);
  static const Color success = Color(0xFF4CAF50);
  static const Color warning = Color(0xFFFF9800);
  static const Color info = Color(0xFF2196F3);
}
```

---

## 📱 **Platform-Specific Features**

### **Android Configuration**
```xml
<!-- android/app/src/main/AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### **iOS Configuration**
```xml
<!-- ios/Runner/Info.plist -->
<key>NSLocationWhenInUseUsageDescription</key>
<string>This app needs location access to track vehicle location.</string>
<key>NSCameraUsageDescription</key>
<string>This app needs camera access to take photos.</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>This app needs photo library access to select images.</string>
```

---

## 🔧 **Dependencies**

### **pubspec.yaml**
```yaml
dependencies:
  flutter:
    sdk: flutter
  
  # HTTP and Networking
  http: ^1.1.0
  dio: ^5.3.2
  
  # State Management
  provider: ^6.0.5
  
  # Local Storage
  shared_preferences: ^2.2.2
  
  # Image Handling
  image_picker: ^1.0.4
  
  # Location Services
  geolocator: ^10.1.0
  permission_handler: ^11.0.1
  
  # Maps
  google_maps_flutter: ^2.5.0
  geocoding: ^2.1.1
  
  # Real-time Communication
  stomp_dart_client: ^1.0.0
  
  # UI Components
  shimmer: ^3.0.0
  flutter_spinkit: ^5.2.0
  
  # Utilities
  intl: ^0.18.1
  uuid: ^4.1.0
  
  # Deep Linking
  app_links: ^3.4.5

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^3.0.0
```

---

## 🚀 **Build Configuration**

### **Android Build**
```gradle
// android/app/build.gradle
android {
    compileSdkVersion 34
    defaultConfig {
        applicationId "com.app.kids_vehicle_tracking"
        minSdkVersion 21
        targetSdkVersion 34
        versionCode 1
        versionName "1.0.0"
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### **iOS Build**
```xml
<!-- ios/Runner/Info.plist -->
<key>CFBundleShortVersionString</key>
<string>1.0.0</string>
<key>CFBundleVersion</key>
<string>1</string>
<key>CFBundleIdentifier</key>
<string>com.app.kidsVehicleTracking</string>
```

---

## 📊 **Performance Optimization**

### **Image Optimization**
```dart
class ImageUtils {
  static Future<String> compressAndEncodeImage(File imageFile) async {
    final bytes = await imageFile.readAsBytes();
    final compressedBytes = await FlutterImageCompress.compressWithList(
      bytes,
      minWidth: 300,
      minHeight: 300,
      quality: 80,
    );
    return base64Encode(compressedBytes!);
  }
}
```

### **Memory Management**
```dart
class MemoryManager {
  static void clearImageCache() {
    PaintingBinding.instance.imageCache.clear();
  }
  
  static void disposeControllers(List<TextEditingController> controllers) {
    for (var controller in controllers) {
      controller.dispose();
    }
  }
}
```

---

## 🔒 **Security Implementation**

### **Token Management**
```dart
class TokenManager {
  static const String _tokenKey = 'auth_token';
  
  static Future<void> saveToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_tokenKey, token);
  }
  
  static Future<String?> getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_tokenKey);
  }
  
  static Future<void> clearToken() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_tokenKey);
  }
}
```

### **Input Validation**
```dart
class Validators {
  static String? validateEmail(String? value) {
    if (value == null || value.isEmpty) {
      return 'Email is required';
    }
    if (!RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(value)) {
      return 'Please enter a valid email';
    }
    return null;
  }
  
  static String? validatePhone(String? value) {
    if (value == null || value.isEmpty) {
      return 'Phone number is required';
    }
    if (!RegExp(r'^[0-9]{10}$').hasMatch(value)) {
      return 'Please enter a valid 10-digit phone number';
    }
    return null;
  }
}
```

---

**Last Updated**: December 2024  
**Version**: 1.0.0  
**Status**: Production Ready
