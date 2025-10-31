package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.Enum.RequestStatus;
import com.app.entity.School;
import com.app.entity.SchoolVehicle;
import com.app.entity.Vehicle;
import com.app.entity.VehicleAssignmentRequest;
import com.app.entity.VehicleOwner;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.VehicleAssignmentRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.repository.SchoolRepository;
import com.app.repository.SchoolVehicleRepository;
import com.app.repository.VehicleAssignmentRequestRepository;
import com.app.repository.VehicleOwnerRepository;
import com.app.repository.VehicleRepository;
import com.app.service.IVehicleAssignmentService;
import com.app.service.IWebSocketNotificationService;
import com.app.payload.response.WebSocketNotificationDto;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class VehicleAssignmentServiceImpl implements IVehicleAssignmentService{
	
	@Autowired
	private VehicleAssignmentRequestRepository requestRepo;
	@Autowired
	private VehicleRepository vehicleRepo;
	@Autowired
	private SchoolRepository schoolRepo;
	@Autowired
	private VehicleOwnerRepository ownerRepo;
	@Autowired
	private SchoolVehicleRepository schoolVehicleRepo;
	@Autowired
	private IWebSocketNotificationService webSocketNotificationService;

	 @Override
	    public ApiResponse createRequest(VehicleAssignmentRequestDto dto) {
	        // validate existence
	        Vehicle vehicle = vehicleRepo.findById(dto.getVehicleId())
	                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
	        VehicleOwner owner = ownerRepo.findById(dto.getOwnerId())
	                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
	        School school = schoolRepo.findById(dto.getSchoolId())
	                .orElseThrow(() -> new ResourceNotFoundException("School not found"));

	        // prevent duplicate PENDING
	        if (requestRepo.existsByVehicle_VehicleIdAndSchool_SchoolIdAndStatus(vehicle.getVehicleId(),
	                school.getSchoolId(), RequestStatus.PENDING)) {
	            return new ApiResponse(false, "A pending request already exists for this vehicle & school", null);
	        }

	        VehicleAssignmentRequest r = VehicleAssignmentRequest.builder()
	                .vehicle(vehicle)
	                .owner(owner)
	                .school(school)
	                .createdBy(dto.getCreatedBy())
	                .status(RequestStatus.PENDING)
	                .build();

	        VehicleAssignmentRequest savedRequest = requestRepo.save(r);
	        
        // Send WebSocket notification to School Admin
        WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
                .type("VEHICLE_ASSIGNMENT_REQUEST")
                .title("New Vehicle Assignment Request")
                .message("New vehicle assignment request from " + owner.getName() + " for vehicle " + vehicle.getVehicleNumber())
                .priority("MEDIUM") // Changed from HIGH to MEDIUM to reduce spam
                .schoolId(school.getSchoolId())
                .vehicleId(vehicle.getVehicleId())
                .action("CREATE")
                .entityType("VEHICLE_ASSIGNMENT_REQUEST")
                .targetRole("SCHOOL_ADMIN")
                .build();
        
        webSocketNotificationService.sendNotificationToSchool(school.getSchoolId(), notification);
	        
	        return new ApiResponse(true, "Assignment request created", savedRequest);
	    }

	    @Override
	    public ApiResponse approveRequest(Integer requestId, String updatedBy) {
	        VehicleAssignmentRequest req = requestRepo.findById(requestId)
	                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

	        if (req.getStatus() == RequestStatus.APPROVED) {
	            return new ApiResponse(false, "Request already approved", null);
	        }

	        // mark approved
	        req.setStatus(RequestStatus.APPROVED);
	        req.setUpdatedBy(updatedBy);
	        req.setUpdatedDate(LocalDateTime.now());
	        requestRepo.save(req);

        // create mapping in school_vehicles (check for duplicates first)
        boolean mappingExists = schoolVehicleRepo.existsBySchool_SchoolIdAndVehicle_VehicleId(
                req.getSchool().getSchoolId(), 
                req.getVehicle().getVehicleId()
        );
        
        if (!mappingExists) {
            SchoolVehicle mapping = SchoolVehicle.builder()
                    .school(req.getSchool())
                    .vehicle(req.getVehicle())
                    .owner(req.getOwner())
                    .isActive(true)
                    .createdBy(updatedBy)
                    .createdDate(LocalDateTime.now())
                    .build();
            schoolVehicleRepo.save(mapping);
        } else {
            // Update existing mapping to active if it was inactive
            SchoolVehicle existingMapping = schoolVehicleRepo.findBySchool_SchoolIdAndVehicle_VehicleId(
                    req.getSchool().getSchoolId(), 
                    req.getVehicle().getVehicleId()
            ).orElse(null);
            
            if (existingMapping != null && !existingMapping.getIsActive()) {
                existingMapping.setIsActive(true);
                existingMapping.setUpdatedBy(updatedBy);
                existingMapping.setUpdatedDate(LocalDateTime.now());
                schoolVehicleRepo.save(existingMapping);
            }
        }

	        // Send WebSocket notification to Vehicle Owner
	        WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
	                .type("VEHICLE_ASSIGNMENT_APPROVED")
	                .title("Vehicle Assignment Approved")
	                .message("Your vehicle ${req.getVehicle().getVehicleNumber()} has been approved for school ${req.getSchool().getSchoolName()}")
	                .priority("MEDIUM")
	                .vehicleId(req.getVehicle().getVehicleId())
	                .schoolId(req.getSchool().getSchoolId())
	                .action("APPROVE")
	                .entityType("VEHICLE_ASSIGNMENT_REQUEST")
	                .targetRole("VEHICLE_OWNER")
	                .build();
	        
	        webSocketNotificationService.sendNotificationToRole("VEHICLE_OWNER", notification);

	        String message = mappingExists ? 
            "Request approved and existing vehicle assignment reactivated" : 
            "Request approved and vehicle assigned to school";
        return new ApiResponse(true, message, null);
	    }

	    @Override
	    public ApiResponse rejectRequest(Integer requestId, String updatedBy) {
	        VehicleAssignmentRequest req = requestRepo.findById(requestId)
	                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

	        req.setStatus(RequestStatus.REJECTED);
	        req.setUpdatedBy(updatedBy);
	        req.setUpdatedDate(LocalDateTime.now());
	        requestRepo.save(req);

	        // Send WebSocket notification to Vehicle Owner
	        WebSocketNotificationDto notification = WebSocketNotificationDto.builder()
	                .type("VEHICLE_ASSIGNMENT_REJECTED")
	                .title("Vehicle Assignment Rejected")
	                .message("Your vehicle ${req.getVehicle().getVehicleNumber()} assignment request for school ${req.getSchool().getSchoolName()} has been rejected")
	                .priority("MEDIUM")
	                .vehicleId(req.getVehicle().getVehicleId())
	                .schoolId(req.getSchool().getSchoolId())
	                .action("REJECT")
	                .entityType("VEHICLE_ASSIGNMENT_REQUEST")
	                .targetRole("VEHICLE_OWNER")
	                .build();
	        
	        webSocketNotificationService.sendNotificationToRole("VEHICLE_OWNER", notification);

	        return new ApiResponse(true, "Request rejected", req);
	    }

	    @Override
	    public ApiResponse getPendingRequestsBySchool(Integer schoolId) {
	        List<VehicleAssignmentRequest> list = requestRepo.findBySchool_SchoolIdAndStatus(schoolId, RequestStatus.PENDING);
	        return new ApiResponse(true, "Pending requests fetched", list);
	    }
	    
	    @Override
	    public ApiResponse getAllRequestsBySchool(Integer schoolId) {
	        List<VehicleAssignmentRequest> list = requestRepo.findBySchool_SchoolId(schoolId);
	        return new ApiResponse(true, "All requests fetched", list);
	    }

	    @Override
	    public ApiResponse getRequestsByOwner(Integer ownerId) {
	        List<VehicleAssignmentRequest> list = requestRepo.findByOwner_OwnerId(ownerId);
	        return new ApiResponse(true, "Requests fetched", list);
	    }

}
