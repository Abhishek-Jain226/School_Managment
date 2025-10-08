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
	
	// Explicit builder method (Lombok fallback)
	public static BulkImportResultDtoBuilder builder() {
		return BulkImportResultDtoBuilder.builder();
	}
	
	// Builder class
	public static class BulkImportResultDtoBuilder {
		private Integer totalRows;
		private Integer successfulImports;
		private Integer failedImports;
		private List<StudentImportResultDto> results;
		private List<String> errors;
		private String message;
		private Boolean success;
		
		public static BulkImportResultDtoBuilder builder() {
			return new BulkImportResultDtoBuilder();
		}
		
		public BulkImportResultDtoBuilder totalRows(Integer totalRows) {
			this.totalRows = totalRows;
			return this;
		}
		
		public BulkImportResultDtoBuilder successfulImports(Integer successfulImports) {
			this.successfulImports = successfulImports;
			return this;
		}
		
		public BulkImportResultDtoBuilder failedImports(Integer failedImports) {
			this.failedImports = failedImports;
			return this;
		}
		
		public BulkImportResultDtoBuilder results(List<StudentImportResultDto> results) {
			this.results = results;
			return this;
		}
		
		public BulkImportResultDtoBuilder errors(List<String> errors) {
			this.errors = errors;
			return this;
		}
		
		public BulkImportResultDtoBuilder message(String message) {
			this.message = message;
			return this;
		}
		
		public BulkImportResultDtoBuilder success(Boolean success) {
			this.success = success;
			return this;
		}
		
		public BulkImportResultDto build() {
			BulkImportResultDto dto = new BulkImportResultDto();
			dto.totalRows = this.totalRows;
			dto.successfulImports = this.successfulImports;
			dto.failedImports = this.failedImports;
			dto.results = this.results;
			dto.errors = this.errors;
			dto.message = this.message;
			dto.success = this.success;
			return dto;
		}
	}
    
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
        
        // Explicit builder method (Lombok fallback)
        public static StudentImportResultDtoBuilder builder() {
            return new StudentImportResultDtoBuilder();
        }
        
        // Builder class
        public static class StudentImportResultDtoBuilder {
            private Integer studentId;
            private String studentName;
            private String parentEmail;
            private String status;
            private String errorMessage;
            private Integer rowNumber;
            
            public StudentImportResultDtoBuilder studentId(Integer studentId) {
                this.studentId = studentId;
                return this;
            }
            
            public StudentImportResultDtoBuilder studentName(String studentName) {
                this.studentName = studentName;
                return this;
            }
            
            public StudentImportResultDtoBuilder parentEmail(String parentEmail) {
                this.parentEmail = parentEmail;
                return this;
            }
            
            public StudentImportResultDtoBuilder status(String status) {
                this.status = status;
                return this;
            }
            
            public StudentImportResultDtoBuilder errorMessage(String errorMessage) {
                this.errorMessage = errorMessage;
                return this;
            }
            
            public StudentImportResultDtoBuilder rowNumber(Integer rowNumber) {
                this.rowNumber = rowNumber;
                return this;
            }
            
            public StudentImportResultDto build() {
                StudentImportResultDto dto = new StudentImportResultDto();
                dto.studentId = this.studentId;
                dto.studentName = this.studentName;
                dto.parentEmail = this.parentEmail;
                dto.status = this.status;
                dto.errorMessage = this.errorMessage;
                dto.rowNumber = this.rowNumber;
                return dto;
            }
        }
    }
}
