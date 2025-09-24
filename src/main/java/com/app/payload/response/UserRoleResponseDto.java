package com.app.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleResponseDto {

	private Long userRoleId;

	private Integer userId;

	private String userName;

	private Integer roleId;

	private String roleName;

	private Boolean isActive;

	private String createdBy;

	private LocalDateTime createdDate;

	private String updatedBy;

	private LocalDateTime updatedDate;

}
