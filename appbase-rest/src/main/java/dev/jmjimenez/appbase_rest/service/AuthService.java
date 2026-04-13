package dev.jmjimenez.appbase_rest.service;
import dev.jmjimenez.appbase_rest.dto.LoggedUserDTO;
import dev.jmjimenez.appbase_rest.dto.request.auth.LoginRequestDTO;
import dev.jmjimenez.appbase_rest.dto.request.auth.RegisterRequestDTO;
import dev.jmjimenez.appbase_rest.dto.response.RegisterResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	void login(LoginRequestDTO loginRequestDTO, HttpServletRequest request, HttpServletResponse response);
	
	LoggedUserDTO getProfile();
	
	RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO, HttpServletRequest request, HttpServletResponse response);
	
	void refreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response);
	
	void logout(HttpServletRequest request, HttpServletResponse response);
	
}