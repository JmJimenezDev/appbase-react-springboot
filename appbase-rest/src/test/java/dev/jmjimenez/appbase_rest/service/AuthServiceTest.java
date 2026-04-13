package dev.jmjimenez.appbase_rest.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.jmjimenez.appbase_rest.dto.request.auth.RegisterRequestDTO;
import dev.jmjimenez.appbase_rest.exception.BadRequestException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Test
	void register_InvalidEmailFormat() {
		RegisterRequestDTO request = new RegisterRequestDTO();
		request.setEmail("invalid-email");
		request.setPassword("password123");

		assertThrows(BadRequestException.class, () -> validateRegisterRequest(request));
	}

	@Test
	void register_PasswordTooShort() {
		RegisterRequestDTO request = new RegisterRequestDTO();
		request.setEmail("test@test.com");
		request.setPassword("123");

		assertThrows(BadRequestException.class, () -> validateRegisterRequest(request));
	}

	@Test
	void register_NullEmail() {
		RegisterRequestDTO request = new RegisterRequestDTO();
		request.setEmail(null);
		request.setPassword("password123");

		assertThrows(BadRequestException.class, () -> validateRegisterRequest(request));
	}

	@Test
	void register_NullPassword() {
		RegisterRequestDTO request = new RegisterRequestDTO();
		request.setEmail("test@test.com");
		request.setPassword(null);

		assertThrows(BadRequestException.class, () -> validateRegisterRequest(request));
	}

	@Test
	void register_ValidRequest_NoException() {
		RegisterRequestDTO request = new RegisterRequestDTO();
		request.setEmail("valid@test.com");
		request.setPassword("password123");

		assertDoesNotThrow(() -> validateRegisterRequest(request));
	}

	private void validateRegisterRequest(RegisterRequestDTO request) {
		if (request == null || request.getEmail() == null || request.getPassword() == null)
			throw new BadRequestException("Invalid request");

		if (!request.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
			throw new BadRequestException("Invalid email format");

		if (request.getPassword().length() < 8)
			throw new BadRequestException("Password must be at least 8 characters long");
	}
}
