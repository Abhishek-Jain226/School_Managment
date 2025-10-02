package com.app.service;

import com.app.payload.request.VehicleAssignmentRequestDto;
import com.app.payload.response.ApiResponse;

public interface IVehicleAssignmentService {

	ApiResponse createRequest(VehicleAssignmentRequestDto dto);   // owner -> create PENDING
    ApiResponse approveRequest(Integer requestId, String updatedBy); // admin -> approve
    ApiResponse rejectRequest(Integer requestId, String updatedBy);  // admin -> reject
    ApiResponse getPendingRequestsBySchool(Integer schoolId); // admin list
    ApiResponse getRequestsByOwner(Integer ownerId); // owner list (optional)

	

}
