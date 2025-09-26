package com.app.controller;

import com.app.payload.request.StudentParentRequestDto;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IParentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

	@Autowired
    private IParentService parentService;

    // ----------- Create Parent -----------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createParent(@RequestBody UserRequestDto request) {
        return ResponseEntity.ok(parentService.createParent(request));
    }

    // ----------- Link Parent To Student -----------
    @PostMapping("/link")
    public ResponseEntity<ApiResponse> linkParentToStudent(@RequestBody StudentParentRequestDto request) {
        return ResponseEntity.ok(parentService.linkParentToStudent(request));
    }

    // ----------- Update Parent -----------
    @PutMapping("/{parentId}")
    public ResponseEntity<ApiResponse> updateParent(
            @PathVariable Integer parentId,
            @RequestBody UserRequestDto request) {
        return ResponseEntity.ok(parentService.updateParent(parentId, request));
    }

    // ----------- Get Parent By Id -----------
    @GetMapping("/{parentId}")
    public ResponseEntity<ApiResponse> getParentById(@PathVariable Integer parentId) {
        return ResponseEntity.ok(parentService.getParentById(parentId));
    }

    // ----------- Get All Parents For A School -----------
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<ApiResponse> getAllParents(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(parentService.getAllParents(schoolId));
    }
    
 // ----------- Get Parent By UserId -----------
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getParentByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(parentService.getParentByUserId(userId));
    }
    
 // ----------- Get Student By Parent UserId -----------
    @GetMapping("/user/{userId}/student")
    public ResponseEntity<ApiResponse> getStudentByParentUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(parentService.getStudentByParentUserId(userId));
    }
}
