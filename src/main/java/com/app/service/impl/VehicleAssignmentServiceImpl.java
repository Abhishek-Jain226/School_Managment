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

	        requestRepo.save(r);
	        return new ApiResponse(true, "Assignment request created", r);
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

	        // create mapping in school_vehicles
	        SchoolVehicle mapping = SchoolVehicle.builder()
	                .school(req.getSchool())
	                .vehicle(req.getVehicle())
	                .owner(req.getOwner())
	                .isActive(true)
	                .createdBy(updatedBy)
	                .createdDate(LocalDateTime.now())
	                .build();
	        schoolVehicleRepo.save(mapping);

	        return new ApiResponse(true, "Request approved and vehicle assigned to school", mapping);
	    }

	    @Override
	    public ApiResponse rejectRequest(Integer requestId, String updatedBy) {
	        VehicleAssignmentRequest req = requestRepo.findById(requestId)
	                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

	        req.setStatus(RequestStatus.REJECTED);
	        req.setUpdatedBy(updatedBy);
	        req.setUpdatedDate(LocalDateTime.now());
	        requestRepo.save(req);

	        return new ApiResponse(true, "Request rejected", req);
	    }

	    @Override
	    public ApiResponse getPendingRequestsBySchool(Integer schoolId) {
	        List<VehicleAssignmentRequest> list = requestRepo.findBySchool_SchoolIdAndStatus(schoolId, RequestStatus.PENDING);
	        return new ApiResponse(true, "Pending requests fetched", list);
	    }

	    @Override
	    public ApiResponse getRequestsByOwner(Integer ownerId) {
	        List<VehicleAssignmentRequest> list = requestRepo.findByOwner_OwnerId(ownerId);
	        return new ApiResponse(true, "Requests fetched", list);
	    }

}
