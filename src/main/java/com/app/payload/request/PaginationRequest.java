package com.app.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequest {

	@NotNull(message = "Page number is required")
	@Min(value = 0, message = "Page number cannot be negative")
	private Integer pageNo;

	@NotNull(message = "Page size is required")
	@Min(value = 1, message = "Page size must be at least 1")
	@Max(value = 100, message = "page size cannot exceed 100")
	private Integer pageSize;

}
