package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.Enum.RequestStatus;
import com.app.entity.VehicleAssignmentRequest;

public interface VehicleAssignmentRequestRepository extends JpaRepository<VehicleAssignmentRequest, Integer> {

	List<VehicleAssignmentRequest> findBySchool_SchoolIdAndStatus(Integer schoolId, RequestStatus status);
    List<VehicleAssignmentRequest> findByOwner_OwnerId(Integer ownerId);
    boolean existsByVehicle_VehicleIdAndSchool_SchoolIdAndStatus(Integer vehicleId, Integer schoolId, RequestStatus status);
}
