package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.Role;
import com.app.entity.School;
import com.app.entity.SchoolUser;
import com.app.entity.User;
import com.app.entity.VehicleOwner;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.PendingUserRequestDTO;
import com.app.payload.request.VehicleOwnerRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.VehicleOwnerResponseDto;
import com.app.repository.RoleRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.SchoolUserRepository;
import com.app.repository.UserRepository;
import com.app.repository.VehicleOwnerRepository;
import com.app.service.IPendingUserService;
import com.app.service.IVehicleOwnerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleOwnerServiceImpl implements IVehicleOwnerService {

	@Autowired
    private VehicleOwnerRepository vehicleOwnerRepository;
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private IPendingUserService pendingUserService;
	
	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private SchoolUserRepository schoolUserRepository;
	
	
//	@Override
//	public ApiResponse registerVehicleOwner(VehicleOwnerRequestDto request) {
//	    // 1. Load Owner User
//	    User ownerUser = userRepository.findById(request.getUserId())
//	            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));
//
//	    // 2. Check if already registered
//	    if (vehicleOwnerRepository.existsByUser(ownerUser)) {
//	        return new ApiResponse(false, "This user is already registered as Vehicle Owner", null);
//	    }
//
//	    // 3. Create VehicleOwner entity
//	    VehicleOwner owner = VehicleOwner.builder()
//	            .user(ownerUser)
//	            .name(request.getName())
//	            .contactNumber(request.getContactNumber())
//	            .email(request.getEmail())
//	            .address(request.getAddress())
//	            .createdBy(request.getCreatedBy())
//	            .createdDate(LocalDateTime.now())
//	            .build();
//
//	    VehicleOwner savedOwner = vehicleOwnerRepository.save(owner);
//
//	    // 4. Load VEHICLE_OWNER role
//	    Role role = roleRepository.findByRoleName("VEHICLE_OWNER")
//	            .orElseThrow(() -> new ResourceNotFoundException("Role VEHICLE_OWNER not found"));
//
//	    // 5. Get school from Admin who created this Owner
//	    User creator = userRepository.findByUserName(request.getCreatedBy())
//	            .orElseThrow(() -> new ResourceNotFoundException("Creator not found"));
//
//	    SchoolUser adminSchoolUser = schoolUserRepository.findByUser(creator)
//	            .orElseThrow(() -> new ResourceNotFoundException("Admin is not mapped to any school"));
//
//	    School school = adminSchoolUser.getSchool();
//
//	    // 6. Prevent duplicate mapping
//	    if (!schoolUserRepository.existsBySchoolAndUserAndRole(school, ownerUser, role)) {
//	        SchoolUser schoolUser = SchoolUser.builder()
//	                .user(ownerUser)   // ✅ VehicleOwner ka user
//	                .school(school)    // ✅ Admin ke school ke sath map karo
//	                .role(role)
//	                .isActive(true)
//	                .createdBy(request.getCreatedBy())
//	                .createdDate(LocalDateTime.now())
//	                .build();
//
//	        schoolUserRepository.save(schoolUser);
//	    }
//
//	    // 7. Create PendingUser entry
//	    PendingUserRequestDTO pendingReq = PendingUserRequestDTO.builder()
//	            .entityType("VEHICLE_OWNER")
//	            .entityId(savedOwner.getOwnerId().longValue())
//	            .email(request.getEmail())
//	            .contactNumber(request.getContactNumber())
//	            .roleId(role.getRoleId())
//	            .createdBy(request.getCreatedBy())
//	            .build();
//
//	    pendingUserService.createPendingUser(pendingReq);
//
//	    // 8. Return success response
//	    return new ApiResponse(true,
//	            "Vehicle Owner registered successfully. Activation link sent to email.",
//	            savedOwner.getOwnerId());
//	}
	
	@Override
	public ApiResponse registerVehicleOwner(VehicleOwnerRequestDto request) {
	    // ✅ Pehle VehicleOwner save karo bina User ke
	    VehicleOwner owner = VehicleOwner.builder()
	            .name(request.getName())
	            .contactNumber(request.getContactNumber())
	            .email(request.getEmail())
	            .address(request.getAddress())
	            .createdBy(request.getCreatedBy())
	            .createdDate(LocalDateTime.now())
	            .build();

	    VehicleOwner savedOwner = vehicleOwnerRepository.save(owner);

	    // ✅ VEHICLE_OWNER role load karo
	    Role role = roleRepository.findByRoleName("VEHICLE_OWNER")
	            .orElseThrow(() -> new ResourceNotFoundException("Role VEHICLE_OWNER not found"));

	    // ✅ CreatedBy (SchoolAdmin) ke school detect karo
	    User creator = userRepository.findByUserName(request.getCreatedBy())
	            .orElseThrow(() -> new ResourceNotFoundException("Creator not found"));

	    SchoolUser adminSchoolUser = schoolUserRepository.findByUser(creator)
	            .orElseThrow(() -> new ResourceNotFoundException("Admin is not mapped to any school"));

	    School school = adminSchoolUser.getSchool();

	    // ✅ PendingUser entry banao (User activation link ke liye)
	    PendingUserRequestDTO pendingReq = PendingUserRequestDTO.builder()
	            .entityType("VEHICLE_OWNER")
	            .entityId(savedOwner.getOwnerId().longValue())  // 👈 VehicleOwner ka id
	            .email(request.getEmail())
	            .contactNumber(request.getContactNumber())
	            .roleId(role.getRoleId())
	            .createdBy(request.getCreatedBy())
	            .build();

	    pendingUserService.createPendingUser(pendingReq);

	    return new ApiResponse(true,
	            "Vehicle Owner registered successfully. Activation link sent to email.",
	            savedOwner.getOwnerId());
	}





//    @Override
//    public ApiResponse activateOwner(Integer ownerId, String activationCode) {
//        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
//                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));
//
//       
//        User user = owner.getUser();
//        user.setIsActive(true);
//        user.setUpdatedDate(LocalDateTime.now());
//        userRepository.save(user);
//
//        return new ApiResponse(true, "Vehicle owner activated successfully", mapToResponse(owner));
//    }
    
    @Override
    public ApiResponse activateOwner(Integer ownerId, String activationCode) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));

       
        User user = User.builder()
                .userName(owner.getEmail().split("@")[0]) 
                .email(owner.getEmail())
                .contactNumber(owner.getContactNumber())
                .isActive(true)
                .createdBy("SYSTEM")
                .createdDate(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

     
        owner.setUser(savedUser);
        owner.setUpdatedBy("SYSTEM");
        owner.setUpdatedDate(LocalDateTime.now());
        vehicleOwnerRepository.save(owner);

        return new ApiResponse(true, "Vehicle owner activated successfully", mapToResponse(owner));
    }

    @Override
    public ApiResponse updateVehicleOwner(Integer ownerId, VehicleOwnerRequestDto request) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));

//        if (request.getUserId() != null) {
//            User user = userRepository.findById(request.getUserId())
//                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));
//            owner.setUser(user);
//        }

        owner.setName(request.getName());
        owner.setContactNumber(request.getContactNumber());
        owner.setAddress(request.getAddress());
        owner.setEmail(request.getEmail());
        owner.setUpdatedBy(request.getUpdatedBy());
        owner.setUpdatedDate(LocalDateTime.now());

        VehicleOwner updated = vehicleOwnerRepository.save(owner);

        return new ApiResponse(true, "Vehicle owner updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse deleteVehicleOwner(Integer ownerId) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));

        vehicleOwnerRepository.delete(owner);

        return new ApiResponse(true, "Vehicle owner deleted successfully", null);
    }

    @Override
    public ApiResponse getVehicleOwnerById(Integer ownerId) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));

        return new ApiResponse(true, "Vehicle owner fetched successfully", mapToResponse(owner));
    }

    @Override
    public ApiResponse getAllVehicleOwners(Integer schoolId) {
        // मान लेते हैं कि filter schoolId से user जुड़े होंगे
        List<VehicleOwnerResponseDto> owners = vehicleOwnerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Vehicle owners fetched successfully", owners);
    }
    @Override
    public ApiResponse getVehicleOwnerByUserId(Integer userId) {
        return vehicleOwnerRepository.findByUser_uId(userId)
            .map(owner -> new ApiResponse(true, "Owner fetched successfully", mapToResponse(owner)))
            .orElse(new ApiResponse(false, "No owner found for this userId", null));
    }
    // ------------------ Private Mapper ------------------
    private VehicleOwnerResponseDto mapToResponse(VehicleOwner owner) {
        return VehicleOwnerResponseDto.builder()
                .ownerId(owner.getOwnerId())
                .userId(owner.getUser().getUId())
                .name(owner.getName())
                .contactNumber(owner.getContactNumber())
                .address(owner.getAddress())
                .email(owner.getEmail())
                .createdBy(owner.getCreatedBy())
                .createdDate(owner.getCreatedDate())
                .updatedBy(owner.getUpdatedBy())
                .updatedDate(owner.getUpdatedDate())
                .build();
    }
}
