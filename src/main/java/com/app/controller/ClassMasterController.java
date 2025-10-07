package com.app.controller;

import com.app.payload.request.ClassMasterRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IClassMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/class-master")
public class ClassMasterController {

    @Autowired
    private IClassMasterService classMasterService;

    // ----------- Create Class -----------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createClass(@RequestBody ClassMasterRequestDto request) {
        return ResponseEntity.ok(classMasterService.createClass(request));
    }

    // ----------- Update Class -----------
    @PutMapping("/{classId}")
    public ResponseEntity<ApiResponse> updateClass(
            @PathVariable Integer classId,
            @RequestBody ClassMasterRequestDto request) {
        return ResponseEntity.ok(classMasterService.updateClass(classId, request));
    }

    // ----------- Delete Class -----------
    @DeleteMapping("/{classId}")
    public ResponseEntity<ApiResponse> deleteClass(@PathVariable Integer classId) {
        return ResponseEntity.ok(classMasterService.deleteClass(classId));
    }

    // ----------- Get Class By ID -----------
    @GetMapping("/{classId}")
    public ResponseEntity<ApiResponse> getClassById(@PathVariable Integer classId) {
        return ResponseEntity.ok(classMasterService.getClassById(classId));
    }

    // ----------- Get All Classes for School -----------
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllClasses(@RequestParam Integer schoolId) {
        return ResponseEntity.ok(classMasterService.getAllClasses(schoolId));
    }

    // ----------- Get All Active Classes for School -----------
    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getAllActiveClasses(@RequestParam Integer schoolId) {
        return ResponseEntity.ok(classMasterService.getAllActiveClasses(schoolId));
    }

    // ----------- Toggle Class Status -----------
    @PatchMapping("/{classId}/toggle-status")
    public ResponseEntity<ApiResponse> toggleClassStatus(@PathVariable Integer classId) {
        return ResponseEntity.ok(classMasterService.toggleClassStatus(classId));
    }

    // ----------- Check Class Name Exists -----------
    @GetMapping("/check-name")
    public ResponseEntity<ApiResponse> checkClassNameExists(
            @RequestParam String className,
            @RequestParam(required = false) Integer excludeClassId) {
        return ResponseEntity.ok(classMasterService.checkClassNameExists(className, excludeClassId, excludeClassId));
    }
}
