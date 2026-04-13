package dev.jmjimenez.appbase_rest.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.jmjimenez.appbase_rest.config.AuditLogger;
import dev.jmjimenez.appbase_rest.dto.LoggedUserDTO;
import dev.jmjimenez.appbase_rest.dto.request.auth.LoginRequestDTO;
import dev.jmjimenez.appbase_rest.dto.request.auth.RegisterRequestDTO;
import dev.jmjimenez.appbase_rest.dto.response.RegisterResponseDTO;
import dev.jmjimenez.appbase_rest.entity.Role;
import dev.jmjimenez.appbase_rest.entity.User;
import dev.jmjimenez.appbase_rest.entity.UserSession;
import dev.jmjimenez.appbase_rest.entity.enums.RoleName;
import dev.jmjimenez.appbase_rest.exception.BadRequestException;
import dev.jmjimenez.appbase_rest.exception.ResourceAlreadyExistsException;
import dev.jmjimenez.appbase_rest.exception.ResourceNotFoundException;
import dev.jmjimenez.appbase_rest.mapper.UserMapper;
import dev.jmjimenez.appbase_rest.repository.RoleRepository;
import dev.jmjimenez.appbase_rest.repository.UserRepository;
import dev.jmjimenez.appbase_rest.repository.UserSessionRepository;
import dev.jmjimenez.appbase_rest.security.util.CsrfTokenUtil;
import dev.jmjimenez.appbase_rest.security.util.JWTUtils;
import dev.jmjimenez.appbase_rest.service.AuthService;
import dev.jmjimenez.appbase_rest.service.JpaUserDetailsService;
import dev.jmjimenez.appbase_rest.util.Constants;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

	@Value("${jwt.access.expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh.expiration}")
	private long refreshTokenExpiration;

	private Duration accessTokenDuration;
	private Duration refreshTokenDuration;

	@PostConstruct
	public void init() {
		accessTokenDuration = Duration.ofMillis(accessTokenExpiration);
		refreshTokenDuration = Duration.ofMillis(refreshTokenExpiration);
	}

	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	private final JWTUtils jwtUtils;
	private final JpaUserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	@Override
	public RegisterResponseDTO register(RegisterRequestDTO request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {

		try {
			validateRegisterRequest(request);

			if (userRepository.existsByEmail(request.getEmail()))
				throw new ResourceAlreadyExistsException("Email already in use");

			Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
					.orElseThrow(() -> new ResourceNotFoundException("Default role USER not found"));

			User user = User.builder().name(request.getName().trim()).surnames(request.getSurnames().trim())
					.email(request.getEmail().toLowerCase().trim()).phone(request.getPhone().trim())
					.password(passwordEncoder.encode(request.getPassword())).enabled(true).emailVerified(false)
					.roles(Set.of(userRole)).build();

			userRepository.save(user);

			log.info("New user registered successfully: {}", user.getEmail());
			AuditLogger.info("USER_REGISTERED: New user registered");

			String deviceId = UUID.randomUUID().toString();

			UserSession session = UserSession.builder().user(user).deviceId(deviceId).tokenVersion(1)
					.createdAt(Instant.now()).expiresAt(Instant.now().plusMillis(refreshTokenExpiration)).active(true)
					.build();

			userSessionRepository.save(session);

			UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

			generateAndAttachTokens(userDetails, httpRequest, httpResponse, deviceId, session.getTokenVersion());

			return new RegisterResponseDTO("User registered successfully");
		} catch (Exception e) {
			AuditLogger.warn("USER_REGISTER_FAILED: Registration failed",
					Map.of("email", request.getEmail(), "ip", httpRequest.getRemoteAddr(), "reason", e.getMessage()));
			throw e;
		}
	}

	@Override
	public void login(LoginRequestDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

			if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
				log.warn("Failed login attempt for email: {}", request.getEmail());
				throw new BadRequestException("Invalid credentials");
			}

			User user = userRepository.findByEmail(userDetails.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));

			String deviceId = UUID.randomUUID().toString();

			UserSession session = UserSession.builder().user(user).deviceId(deviceId).tokenVersion(1)
					.createdAt(Instant.now()).expiresAt(Instant.now().plusMillis(refreshTokenExpiration)).active(true)
					.build();

			userSessionRepository.save(session);

			generateAndAttachTokens(userDetails, httpRequest, httpResponse, deviceId, session.getTokenVersion());

			log.info("User logged in successfully: {}", user.getEmail());
			AuditLogger.info("LOGIN_SUCCESS: User logged in",
					Map.of(Constants.USER_ID, user.getEmail(), "ip", httpRequest.getRemoteAddr()));

			httpResponse.setStatus(HttpServletResponse.SC_OK);
		} catch (UsernameNotFoundException e) {
			MDC.put(Constants.USER_ID, request.getEmail());
			AuditLogger.info("LOGIN_ERROR: User not found");
			MDC.remove(Constants.USER_ID);
			throw new BadRequestException("Invalid credentials");
		} catch (BadCredentialsException e) {
			MDC.put(Constants.USER_ID, request.getEmail());
			AuditLogger.info("LOGIN_ERROR: Invalid credentials");
			MDC.remove(Constants.USER_ID);
			throw e;
		} catch (Exception e) {
			MDC.put(Constants.USER_ID, request.getEmail() != null ? request.getEmail() : "anonymous");
			AuditLogger.info("LOGIN_ERROR: Unexpected error during login");
			MDC.remove(Constants.USER_ID);
			throw e;
		}
	}

	@Override
	public void refreshToken(String refreshToken, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

		try {
			if (refreshToken == null || refreshToken.isBlank())
				throw new BadRequestException("Refresh token missing");

			String username = jwtUtils.extractUsername(refreshToken);
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (!jwtUtils.isValidToken(refreshToken, userDetails, httpRequest)) {
				log.warn("Invalid refresh token attempt for user: {}", username);
				throw new BadRequestException("Invalid or expired refresh token");
			}

			log.info("Refresh token successful for user: {}", username);
			AuditLogger.info("REFRESH_SUCCESS: Refresh token used",
					Map.of(Constants.USER_ID, username, "ip", httpRequest.getRemoteAddr()));

			String deviceId = jwtUtils.extractDeviceIdFromRequest(httpRequest);

			if (deviceId == null)
				throw new BadRequestException("Device ID missing");

			User user = userRepository.findByEmail(userDetails.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
			UserSession session = userSessionRepository.findByUserAndDeviceId(user, deviceId)
					.orElseThrow(() -> new ResourceNotFoundException("Session not found"));

			if (!session.isActive() || session.getExpiresAt().isBefore(Instant.now()))
				throw new BadRequestException("Session expired or inactive");

			session.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiration));
			userSessionRepository.save(session);

			generateAndAttachTokens(userDetails, httpRequest, httpResponse, deviceId, session.getTokenVersion());

			httpResponse.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			AuditLogger.error("REFRESH_ERROR: Refresh token error",
					Map.of("ip", httpRequest.getRemoteAddr(), "reason", e.getMessage()));
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public LoggedUserDTO getProfile() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails))
			throw new BadRequestException("User not authenticated");

		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));

		return userMapper.toLoggedUserDto(user);
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {

			User user = userRepository.findByEmail(userDetails.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));

			String deviceId = jwtUtils.extractDeviceIdFromRequest(request);
			UserSession session = userSessionRepository.findByUserAndDeviceId(user, deviceId)
					.orElseThrow(() -> new ResourceNotFoundException("Session not found"));
			session.setActive(false);
			userSessionRepository.save(session);

			log.info("User logged out: {}", user.getEmail());
			AuditLogger.info("USER_LOGOUT: User logged out", Map.of(Constants.USER_ID, user.getEmail()));
		}

		clearCookies(response);
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private void generateAndAttachTokens(UserDetails userDetails, HttpServletRequest request,
			HttpServletResponse response, String deviceId, int sessionTokenVersion) {

		String accessToken = jwtUtils.generateToken(userDetails, request, deviceId, sessionTokenVersion);
		String refreshToken = jwtUtils.generateRefreshToken(userDetails, request, deviceId);
		String csrfToken = CsrfTokenUtil.generateCsrfToken();

		ResponseCookie accessCookie = buildCookie("access_token", accessToken, accessTokenDuration, true);
		ResponseCookie refreshCookie = buildCookie("refresh_token", refreshToken, refreshTokenDuration, true);
		ResponseCookie csrfCookie = buildCookie("csrf_token", csrfToken, accessTokenDuration, false);

		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, csrfCookie.toString());
	}

	private ResponseCookie buildCookie(String name, String value, Duration duration, boolean httpOnly) {
		return ResponseCookie.from(name, value).httpOnly(httpOnly).secure(true) // obligatorio en producción HTTPS
				.sameSite("Strict").path("/").maxAge(duration).build();
	}

	private void clearCookies(HttpServletResponse response) {
		response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("access_token", "", Duration.ZERO, true).toString());
		response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("refresh_token", "", Duration.ZERO, true).toString());
		response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("csrf_token", "", Duration.ZERO, false).toString());
	}

	private void validateRegisterRequest(RegisterRequestDTO request) {
		if (request.getEmail() == null || request.getPassword() == null)
			throw new BadRequestException("Invalid request");

		if (!request.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
			throw new BadRequestException("Invalid email format");

		if (request.getPassword().length() < 8)
			throw new BadRequestException("Password must be at least 8 characters long");
	}
}
