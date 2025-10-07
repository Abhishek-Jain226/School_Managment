package com.app.service.impl;

import com.app.entity.ClassMaster;
import com.app.entity.School;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.ClassMasterRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.ClassMasterResponseDto;
import com.app.repository.ClassMasterRepository;
import com.app.repository.SchoolRepository;
import com.app.service.IClassMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassMasterServiceImpl implements IClassMasterService {

    @Autowired
    private ClassMasterRepository classMasterRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;

    @Override
    public ApiResponse createClass(ClassMasterRequestDto request) {
        try {
            // Validate school exists
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));

            // Check if class name already exists for this school
            if (classMasterRepository.existsByClassNameAndSchool(request.getClassName(), school)) {
                return new ApiResponse(false, "Class with this name already exists for this school", null);
            }

            // Check if class order already exists for this school
            if (classMasterRepository.findByClassOrderAndSchool(request.getClassOrder(), school).isPresent()) {
                return new ApiResponse(false, "Class with this order already exists for this school", null);
            }

            ClassMaster classMaster = ClassMaster.builder()
                    .className(request.getClassName())
                    .classOrder(request.getClassOrder())
                    .description(request.getDescription())
                    .school(school)
                    .isActive(request.getIsActive())
                    .createdBy(request.getCreatedBy())
                    .createdDate(LocalDateTime.now())
                    .build();

            ClassMaster savedClass = classMasterRepository.save(classMaster);
            ClassMasterResponseDto response = convertToResponseDto(savedClass);

            return new ApiResponse(true, "Class created successfully", response);
        } catch (Exception e) {
            return new ApiResponse(false, "Error creating class: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse updateClass(Integer classId, ClassMasterRequestDto request) {
        try {
            ClassMaster classMaster = classMasterRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + classId));

            // Validate school exists
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));

            // Check if class name already exists for this school (excluding current class)
            if (classMasterRepository.existsByClassNameAndSchoolAndClassIdNot(request.getClassName(), school, classId)) {
                return new ApiResponse(false, "Class with this name already exists for this school", null);
            }

            // Check if class order already exists for this school (excluding current class)
            if (classMasterRepository.findByClassOrderAndSchool(request.getClassOrder(), school).isPresent() &&
                    !classMasterRepository.findByClassOrderAndSchool(request.getClassOrder(), school).get().getClassId().equals(classId)) {
                return new ApiResponse(false, "Class with this order already exists for this school", null);
            }

            classMaster.setClassName(request.getClassName());
            classMaster.setClassOrder(request.getClassOrder());
            classMaster.setDescription(request.getDescription());
            classMaster.setSchool(school);
            classMaster.setIsActive(request.getIsActive());
            classMaster.setUpdatedBy(request.getUpdatedBy());
            classMaster.setUpdatedDate(LocalDateTime.now());

            ClassMaster updatedClass = classMasterRepository.save(classMaster);
            ClassMasterResponseDto response = convertToResponseDto(updatedClass);

            return new ApiResponse(true, "Class updated successfully", response);
        } catch (Exception e) {
            return new ApiResponse(false, "Error updating class: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse deleteClass(Integer classId) {
        try {
            ClassMaster classMaster = classMasterRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + classId));

            classMasterRepository.delete(classMaster);
            return new ApiResponse(true, "Class deleted successfully", null);
        } catch (Exception e) {
            return new ApiResponse(false, "Error deleting class: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getClassById(Integer classId) {
        try {
            ClassMaster classMaster = classMasterRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + classId));

            ClassMasterResponseDto response = convertToResponseDto(classMaster);
            return new ApiResponse(true, "Class retrieved successfully", response);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving class: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getAllClasses(Integer schoolId) {
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));
            
            List<ClassMaster> classes = classMasterRepository.findAllBySchoolOrderByClassOrder(school);
            List<ClassMasterResponseDto> responses = classes.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());

            return new ApiResponse(true, "Classes retrieved successfully", responses);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving classes: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getAllActiveClasses(Integer schoolId) {
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));
            
            List<ClassMaster> classes = classMasterRepository.findAllActiveBySchoolOrderByClassOrder(school);
            List<ClassMasterResponseDto> responses = classes.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());

            return new ApiResponse(true, "Active classes retrieved successfully", responses);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving active classes: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse toggleClassStatus(Integer classId) {
        try {
            ClassMaster classMaster = classMasterRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + classId));

            classMaster.setIsActive(!classMaster.getIsActive());
            classMaster.setUpdatedDate(LocalDateTime.now());

            ClassMaster updatedClass = classMasterRepository.save(classMaster);
            ClassMasterResponseDto response = convertToResponseDto(updatedClass);

            return new ApiResponse(true, "Class status updated successfully", response);
        } catch (Exception e) {
            return new ApiResponse(false, "Error updating class status: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse checkClassNameExists(String className, Integer schoolId, Integer excludeClassId) {
        try {
            // Validate school exists
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

            boolean exists;
            if (excludeClassId != null) {
                exists = classMasterRepository.existsByClassNameAndSchoolAndClassIdNot(className, school, excludeClassId);
            } else {
                exists = classMasterRepository.existsByClassNameAndSchool(className, school);
            }

            return new ApiResponse(true, "Class name check completed", exists);
        } catch (Exception e) {
            return new ApiResponse(false, "Error checking class name: " + e.getMessage(), null);
        }
    }

    private ClassMasterResponseDto convertToResponseDto(ClassMaster classMaster) {
        return ClassMasterResponseDto.builder()
                .classId(classMaster.getClassId())
                .className(classMaster.getClassName())
                .classOrder(classMaster.getClassOrder())
                .description(classMaster.getDescription())
                .schoolId(classMaster.getSchool() != null ? classMaster.getSchool().getSchoolId() : null)
                .schoolName(classMaster.getSchool() != null ? classMaster.getSchool().getSchoolName() : null)
                .isActive(classMaster.getIsActive())
                .createdBy(classMaster.getCreatedBy())
                .createdDate(classMaster.getCreatedDate())
                .updatedBy(classMaster.getUpdatedBy())
                .updatedDate(classMaster.getUpdatedDate())
                .build();
    }
}
