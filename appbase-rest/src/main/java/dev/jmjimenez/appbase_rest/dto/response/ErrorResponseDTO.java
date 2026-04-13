package dev.jmjimenez.appbase_rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {

	private String error;
	private String message;
	
}