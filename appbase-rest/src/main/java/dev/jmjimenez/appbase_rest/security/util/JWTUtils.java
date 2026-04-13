package dev.jmjimenez.appbase_rest.security.util;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import dev.jmjimenez.appbase_rest.entity.User;
import dev.jmjimenez.appbase_rest.entity.UserSession;
import dev.jmjimenez.appbase_rest.repository.UserRepository;
import dev.jmjimenez.appbase_rest.repository.UserSessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTUtils {

	private static final String DEVICE_ID = "deviceId";

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.access.expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh.expiration}")
	private long refreshTokenExpiration;

	@Value("${ratelimit.trusted-proxies:}")
	private String trustedProxiesConfig;

	private SecretKey key;
	private List<String> trustedProxies;

	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;

	static final String USER_NOT_FOUND = "Usuario no encontrado";
	static final String TOKEN_VERSION = "tokenVersion";
	static final String AGENT = "agent";
	static final String TOKEN_TYPE = "token_type";
	static final String REFRESH = "refresh";
	private static final String LOCALHOST_IPV4 = "127.0.0.1";
	private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
	private static final String UNKNOWN_IP = "unknown";

	@PostConstruct
	public void init() {
		if (trustedProxiesConfig != null && !trustedProxiesConfig.isBlank()) {
			trustedProxies = Arrays.stream(trustedProxiesConfig.split(","))
					.map(String::trim)
					.filter(s -> !s.isBlank())
					.toList();
		} else {
			trustedProxies = List.of();
		}
	}

	private SecretKey getKey() {
		if (key == null) {
			byte[] decodedKey = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
			key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
		}
		return key;
	}

	public String generateToken(UserDetails userDetails, HttpServletRequest request) {

		final String userIp = extractIp(request);
		final String userAgent = extractUserAgent(request);

		HashMap<String, Object> claims = new HashMap<>();
		claims.put("roles", userDetails.getAuthorities().stream().map(auth -> auth.getAuthority()).toList());
		claims.put("ip", userIp);
		claims.put(AGENT, userAgent);

		return Jwts.builder().setId(UUID.randomUUID().toString()).setClaims(claims)
				.setSubject(userDetails.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration)).signWith(getKey())
				.compact();
	}

	public String generateToken(UserDetails userDetails, HttpServletRequest request, String deviceId,
			int sessionTokenVersion) {

		HashMap<String, Object> claims = new HashMap<>();
		claims.put("roles", userDetails.getAuthorities().stream().map(a -> a.getAuthority()).toList());
		claims.put(DEVICE_ID, deviceId);
		claims.put(TOKEN_VERSION, sessionTokenVersion);
		claims.put("ip", extractIp(request));
		claims.put(AGENT, extractUserAgent(request));

		return Jwts.builder().setId(UUID.randomUUID().toString()).setClaims(claims)
				.setSubject(userDetails.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration)).signWith(getKey())
				.compact();
	}

	public String generateRefreshToken(UserDetails userDetails, HttpServletRequest request, String deviceId) {

		final String userIp = extractIp(request);
		final String userAgent = extractUserAgent(request);

		HashMap<String, Object> claims = new HashMap<>();
		claims.put(TOKEN_TYPE, REFRESH);
		claims.put(DEVICE_ID, deviceId);
		claims.put("ip", userIp);
		claims.put(AGENT, userAgent);

		return Jwts.builder().setId(UUID.randomUUID().toString()).setClaims(claims)
				.setSubject(userDetails.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration)).signWith(getKey())
				.compact();
	}

	public String generateResetPasswordToken(String email) {
		HashMap<String, Object> claims = new HashMap<>();
		claims.put(TOKEN_TYPE, "reset_password");

		return Jwts.builder().setId(UUID.randomUUID().toString()).setClaims(claims).setSubject(email)
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
				.signWith(getKey()).compact();
	}

	public String extractUsername(String token) {
		return extractClaims(token, Claims::getSubject);
	}

	public boolean isValidToken(String token, UserDetails userDetails, HttpServletRequest request) {
		final String username = extractUsername(token);
		final User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

		final String userIp = extractIp(request);
		final String userAgent = extractUserAgent(request);

		boolean baseValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token)
				&& validateIpAndAgent(token, userIp, userAgent);

		if (!baseValid)
			return false;

		String deviceId = extractClaims(token, claims -> claims.get(DEVICE_ID, String.class));

		UserSession session = userSessionRepository.findByUserAndDeviceId(user, deviceId).orElse(null);

		return session != null && session.isActive() && session.getExpiresAt().isAfter(Instant.now())
				&& !isTokenExpired(token) && validateIpAndAgent(token, userIp, userAgent);
	}

	public String validateResetPasswordToken(String token) {
		try {
			if (isTokenExpired(token))
				return null;

			final String type = extractClaims(token, claims -> claims.get(TOKEN_TYPE, String.class));
			if (!"reset_password".equals(type))
				return null;

			return extractClaims(token, Claims::getSubject);
		} catch (Exception e) {
			return null;
		}
	}

	public String extractJwtId(String token) {
		try {
			return extractClaims(token, Claims::getId);
		} catch (Exception e) {
			log.warn("No se pudo extraer el JWT ID", e);
			return null;
		}
	}

	public String getCookieValue(HttpServletRequest request, String cookieName) {
		if (request.getCookies() == null)
			return null;

		for (Cookie cookie : request.getCookies()) {
			if (cookieName.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}

	// ------------------- Helpers -------------------

	private <T> T extractClaims(String token, Function<Claims, T> resolver) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
		return resolver.apply(claims);
	}

	private boolean isTokenExpired(String token) {
		return extractClaims(token, Claims::getExpiration).before(new Date());
	}

	private boolean validateIpAndAgent(String token, String userIp, String userAgent) {
		final String tokenIp = extractClaims(token, claims -> claims.get("ip", String.class));
		final String tokenAgent = extractClaims(token, claims -> claims.get(AGENT, String.class));
		return userIp.equals(tokenIp) && userAgent.equals(tokenAgent);
	}

	public String extractIp(HttpServletRequest request) {
		String remoteAddr = request.getRemoteAddr();

		if (isTrustedProxy(remoteAddr)) {
			String xfHeader = request.getHeader("X-Forwarded-For");
			if (xfHeader != null && !xfHeader.isBlank()) {
				String[] ips = xfHeader.split(",");
				for (String ip : ips) {
					String trimmedIp = ip.trim();
					if (!trimmedIp.isBlank() && !isPrivateOrLocalIp(trimmedIp)) {
						return trimmedIp;
					}
				}
			}

			String xRealIp = request.getHeader("X-Real-IP");
			if (xRealIp != null && !xRealIp.isBlank() && !isPrivateOrLocalIp(xRealIp.trim())) {
				return xRealIp.trim();
			}
		}

		return remoteAddr;
	}

	private boolean isTrustedProxy(String ip) {
		if (trustedProxies == null || trustedProxies.isEmpty()) {
			return false;
		}
		return trustedProxies.contains(ip);
	}

	private boolean isPrivateOrLocalIp(String ip) {
		if (ip == null || ip.isBlank()) {
			return true;
		}
		if (ip.equals(LOCALHOST_IPV4) || ip.equals(LOCALHOST_IPV6) || ip.equals(UNKNOWN_IP)) {
			return true;
		}
		if (ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("172.")) {
			if (ip.startsWith("172.")) {
				try {
					int secondOctet = Integer.parseInt(ip.split("\\.")[1]);
					return secondOctet >= 16 && secondOctet <= 31;
				} catch (NumberFormatException e) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public String extractUserAgent(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");
		return (agent == null) ? "" : agent;
	}

	public String extractDeviceIdFromRequest(HttpServletRequest request) {
		String token = getCookieValue(request, "access_token");
		if (token == null || token.isBlank()) {
			return null;
		}

		try {
			return extractClaims(token, claims -> claims.get(DEVICE_ID, String.class));
		} catch (Exception e) {
			log.warn("No se pudo extraer deviceId del JWT", e);
			return null;
		}
	}
}
