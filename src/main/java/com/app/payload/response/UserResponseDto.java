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
public class UserResponseDto {

	private Integer uId;

	private String userName;

	private String email;

	private String contactNumber;

	private Boolean isActive;

	private String createdBy;

	private LocalDateTime createdDate;

	private String updatedBy;

	private LocalDateTime updatedDate;

}
