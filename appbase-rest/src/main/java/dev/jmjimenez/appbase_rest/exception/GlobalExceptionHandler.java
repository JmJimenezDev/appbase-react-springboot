package dev.jmjimenez.appbase_rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.jmjimenez.appbase_rest.dto.response.ErrorResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {

		ErrorResponseDTO errorResponse = new ErrorResponseDTO("Internal Server Error", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {

		ErrorResponseDTO errorResponse = new ErrorResponseDTO("Invalid Input", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<ErrorResponseDTO> handleMissingHeader(MissingRequestHeaderException ex) {

		ErrorResponseDTO errorResponse = new ErrorResponseDTO("Missing Header",
				"Required header is missing: " + ex.getHeaderName());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex) {

		ErrorResponseDTO errorResponse = new ErrorResponseDTO("Unauthorized", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {

		ErrorResponseDTO errorResponse = new ErrorResponseDTO("Access Denied", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN); // 403 Forbidden
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {

		ErrorResponseDTO errorResponse = new ErrorResponseDTO("Not Found", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponseDTO> handleBadRequestException(BadRequestException ex) {

		ErrorResponseDTO errorResponse = new ErrorResponseDTO("Bad Request", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // HTTP 400 Bad Request
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ErrorResponseDTO> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {

		ErrorResponseDTO errorResponse = new ErrorResponseDTO("Conflict", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}
}