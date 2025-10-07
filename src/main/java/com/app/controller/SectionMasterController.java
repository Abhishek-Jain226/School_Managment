package com.app.controller;

import com.app.entity.SectionMaster;
import com.app.payload.request.SectionMasterRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.ISectionMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/section-master")
public class SectionMasterController {

    @Autowired
    private ISectionMasterService sectionMasterService;

    // ----------- Create Section -----------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createSection(@RequestBody SectionMasterRequestDto request) {
        return ResponseEntity.ok(sectionMasterService.createSection(request));
    }

    // ----------- Update Section -----------
    @PutMapping("/{sectionId}")
    public ResponseEntity<ApiResponse> updateSection(
            @PathVariable Integer sectionId,
            @RequestBody SectionMasterRequestDto request) {
        return ResponseEntity.ok(sectionMasterService.updateSection(sectionId, request));
    }

    // ----------- Delete Section -----------
    @DeleteMapping("/{sectionId}")
    public ResponseEntity<ApiResponse> deleteSection(@PathVariable Integer sectionId) {
        return ResponseEntity.ok(sectionMasterService.deleteSection(sectionId));
    }

    // ----------- Get Section By ID -----------
    @GetMapping("/{sectionId}")
    public ResponseEntity<ApiResponse> getSectionById(@PathVariable Integer sectionId) {
        return ResponseEntity.ok(sectionMasterService.getSectionById(sectionId));
    }

    // ----------- Get All Sections for School -----------
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllSections(@RequestParam Integer schoolId) {
        return ResponseEntity.ok(sectionMasterService.getAllSections(schoolId));
    }

    // ----------- Get All Active Sections for School -----------
    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getAllActiveSections(@RequestParam Integer schoolId) {
        return ResponseEntity.ok(sectionMasterService.getAllActiveSections(schoolId));
    }


    // ----------- Toggle Section Status -----------
    @PatchMapping("/{sectionId}/toggle-status")
    public ResponseEntity<ApiResponse> toggleSectionStatus(@PathVariable Integer sectionId) {
        return ResponseEntity.ok(sectionMasterService.toggleSectionStatus(sectionId));
    }

    // ----------- Check Section Name Exists -----------
    @GetMapping("/check-name")
    public ResponseEntity<ApiResponse> checkSectionNameExists(
            @RequestParam String sectionName,
            @RequestParam(required = false) Integer excludeSectionId) {
        return ResponseEntity.ok(sectionMasterService.checkSectionNameExists(sectionName, excludeSectionId, excludeSectionId));
    }
}
