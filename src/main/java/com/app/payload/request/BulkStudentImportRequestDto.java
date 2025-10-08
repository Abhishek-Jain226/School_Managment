package com.app.payload.request;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkStudentImportRequestDto {
    
    @NotNull(message = "Students list cannot be null")
    @NotEmpty(message = "Students list cannot be empty")
    @Valid
    private List<StudentRequestDto> students;
    
    @NotNull(message = "School ID cannot be null")
    private Integer schoolId;
    
    @NotNull(message = "Created by cannot be null")
    private String createdBy;
    
    private String schoolDomain; // For email generation
    
    private Boolean sendActivationEmails = true; // Default to true
    
    private String emailGenerationStrategy = "USE_PROVIDED"; // USE_PROVIDED (mandatory emails)
    
    // Explicit getters for critical methods (Lombok fallback)
    public List<StudentRequestDto> getStudents() {
        return students;
    }
    
    public Integer getSchoolId() {
        return schoolId;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public String getSchoolDomain() {
        return schoolDomain;
    }
    
    public Boolean getSendActivationEmails() {
        return sendActivationEmails;
    }
}
