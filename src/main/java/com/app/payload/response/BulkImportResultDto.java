package com.app.payload.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResultDto {
    
    private Integer totalRows;
    private Integer successfulImports;
    private Integer failedImports;
    private List<StudentImportResultDto> results;
    private List<String> errors;
    private String message;
    private Boolean success;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentImportResultDto {
        private Integer studentId;
        private String studentName;
        private String parentEmail;
        private String status; // SUCCESS, ERROR
        private String errorMessage;
        private Integer rowNumber;
    }
}
