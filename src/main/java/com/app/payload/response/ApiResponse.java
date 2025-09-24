package com.app.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {

	private boolean success;
	private String message;
	private Object data;
	public ApiResponse(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	
	
	

}
