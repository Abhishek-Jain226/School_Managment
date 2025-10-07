package com.app.payload.request;

import com.app.entity.SectionMaster;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionMasterRequestDto {

    private Integer sectionId;

    @NotBlank(message = "Section name is required")
    @Size(max = 50, message = "Section name cannot exceed 50 characters")
    private String sectionName;


    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "School ID is required")
    private Integer schoolId;

    private Boolean isActive = true;

    @NotBlank(message = "Created by is required")
    @Size(max = 50, message = "Created by cannot exceed 50 characters")
    private String createdBy;

    @Size(max = 50, message = "Updated by cannot exceed 50 characters")
    private String updatedBy;
}
