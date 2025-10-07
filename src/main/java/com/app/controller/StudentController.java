package com.app.controller;

import com.app.payload.request.BulkStudentImportRequestDto;
import com.app.payload.request.StudentRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.BulkImportResultDto;
import com.app.service.IBulkStudentImportService;
import com.app.service.IStudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

	@Autowired
    private IStudentService studentService;
    
    @Autowired
    private IBulkStudentImportService bulkStudentImportService;

    // ----------- Create Student -----------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createStudent(@RequestBody StudentRequestDto request) {
        return ResponseEntity.ok(studentService.createStudent(request));
    }

    // ----------- Update Student -----------
    @PutMapping("/{studentId}")
    public ResponseEntity<ApiResponse> updateStudent(
            @PathVariable Integer studentId,
            @RequestBody StudentRequestDto request) {
        return ResponseEntity.ok(studentService.updateStudent(studentId, request));
    }

    // ----------- Delete Student -----------
    @DeleteMapping("/{studentId}")
    public ResponseEntity<ApiResponse> deleteStudent(@PathVariable Integer studentId) {
        return ResponseEntity.ok(studentService.deleteStudent(studentId));
    }

    // ----------- Get Student By Id -----------
    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse> getStudentById(@PathVariable Integer studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }

    // ----------- Get All Students By School -----------
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<ApiResponse> getAllStudents(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(studentService.getAllStudents(schoolId));
    }

    // ----------- Get Students By Trip -----------
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<ApiResponse> getStudentsByTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(studentService.getStudentsByTrip(tripId));
    }
    

    // ✅ Student count by schoolId
    @GetMapping("/count/{schoolId}")
    public ResponseEntity<ApiResponse> getStudentCount(@PathVariable Integer schoolId) {
        long count = studentService.getStudentCountBySchool(schoolId);
        return ResponseEntity.ok(new ApiResponse(true, "Student count fetched", count));
    }
    
    // ----------- Bulk Import Students -----------
    @PostMapping("/bulk-import")
    public ResponseEntity<BulkImportResultDto> bulkImportStudents(@RequestBody BulkStudentImportRequestDto request) {
        BulkImportResultDto result = bulkStudentImportService.importStudents(request);
        return ResponseEntity.ok(result);
    }
    
    // ----------- Validate Students for Bulk Import -----------
    @PostMapping("/bulk-validate")
    public ResponseEntity<BulkImportResultDto> validateStudentsForBulkImport(@RequestBody BulkStudentImportRequestDto request) {
        BulkImportResultDto result = bulkStudentImportService.validateStudents(request);
        return ResponseEntity.ok(result);
    }
}
