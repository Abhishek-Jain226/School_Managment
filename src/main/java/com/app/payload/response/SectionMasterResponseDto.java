package com.app.payload.response;

import java.time.LocalDateTime;

import com.app.entity.SectionMaster;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionMasterResponseDto {

    private Integer sectionId;
    private String sectionName;
    private String description;
    private Integer schoolId;
    private String schoolName;
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
}
