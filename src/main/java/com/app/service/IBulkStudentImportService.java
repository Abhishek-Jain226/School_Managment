package com.app.service;

import com.app.payload.request.BulkStudentImportRequestDto;
import com.app.payload.response.BulkImportResultDto;

public interface IBulkStudentImportService {
    
    /**
     * Import multiple students from bulk request
     * @param request Bulk import request containing students list
     * @return Bulk import result with success/failure details
     */
    BulkImportResultDto importStudents(BulkStudentImportRequestDto request);
    
    /**
     * Validate students data before import
     * @param request Bulk import request
     * @return Validation result
     */
    BulkImportResultDto validateStudents(BulkStudentImportRequestDto request);
}
