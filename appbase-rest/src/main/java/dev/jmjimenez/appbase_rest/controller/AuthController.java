package dev.jmjimenez.appbase_rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.jmjimenez.appbase_rest.dto.LoggedUserDTO;
import dev.jmjimenez.appbase_rest.dto.request.auth.LoginRequestDTO;
import dev.jmjimenez.appbase_rest.dto.request.auth.RegisterRequestDTO;
import dev.jmjimenez.appbase_rest.dto.response.RegisterResponseDTO;
import dev.jmjimenez.appbase_rest.exception.AuthenticationException;
import dev.jmjimenez.appbase_rest.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para registro, login, refresh token y perfil")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Usuario registrado correctamente", content = @Content(schema = @Schema(implementation = RegisterResponseDTO.class))),
			@ApiResponse(responseCode = "400", description = "Datos de registro inválidos", content = @Content) })
	public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO registerRequest,
			HttpServletRequest request, HttpServletResponse response) {
		return authService.register(registerRequest, request, response);
	}

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Login de usuario", description = "Autentica al usuario y genera tokens JWT")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Login exitoso"),
			@ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content) })
	public void login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest request,
			HttpServletResponse response) {
		authService.login(loginRequest, request, response);
	}

	@PostMapping("/refresh")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Refrescar token", description = "Genera un nuevo access token usando el refresh token de las cookies")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Token refrescado correctamente"),
			@ApiResponse(responseCode = "401", description = "Refresh token faltante o inválido", content = @Content) })
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = getCookieValue(request, "refresh_token");

		if (refreshToken == null)
			throw new AuthenticationException("Refresh token is missing");

		authService.refreshToken(refreshToken, request, response);
	}

	@GetMapping("/profile")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Perfil del usuario", description = "Obtiene los datos del usuario autenticado")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Perfil obtenido correctamente", content = @Content(schema = @Schema(implementation = LoggedUserDTO.class))),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado", content = @Content) })
	public LoggedUserDTO getProfile() {

		return authService.getProfile();
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Logout", description = "Elimina los tokens y cierra sesión del usuario")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Logout exitoso") })
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		authService.logout(request, response);
	}

	private String getCookieValue(HttpServletRequest request, String cookieName) {
		if (request.getCookies() != null)
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(cookieName))
					return cookie.getValue();
			}

		return null;
	}
}