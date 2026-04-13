package dev.jmjimenez.appbase_rest.config;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.Bucket;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

	@Value("${ratelimit.general.limit:70}")
	private int generalLimit;

	@Value("${ratelimit.general.duration:1}")
	private long generalDurationMinutes;

	@Value("${ratelimit.login.limit:5}")
	private int loginLimit;

	@Value("${ratelimit.login.duration:5}")
	private long loginDurationMinutes;

	@Value("${ratelimit.ban.duration:15}")
	private long banDurationMinutes;

	@Value("${ratelimit.maxBlocks:3}")
	private int maxBlocks;

	@Value("${ratelimit.blockCountResetMinutes:10}")
	private long blockCountResetMinutes;

	@Value("${ratelimit.trusted-proxies:}")
	private String trustedProxiesConfig;

	private List<String> trustedProxies;

	private final Cache<String, Bucket> generalBuckets = Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(30))
			.maximumSize(100_000).build();

	private final Cache<String, Bucket> loginBuckets = Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(30))
			.maximumSize(50_000).build();

	private final Cache<String, Instant> bannedIps = Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(2))
			.maximumSize(50_000).build();

	private final Cache<String, AtomicInteger> ipBlockCounts = Caffeine.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(blockCountResetMinutes)).maximumSize(100_000).build();

	private final Cache<String, Instant> lastBlockAttempt = Caffeine.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(blockCountResetMinutes)).maximumSize(100_000).build();
	
	private static final String RETRY_AFTER = "retryAfter";
	private static final String ACTION = "action";
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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String ip = getClientIp(request);
		String path = request.getRequestURI();
		boolean isLogin = path.equals("/api/auth/login") || path.equals("/api/auth/register");

		Instant banExpiry = bannedIps.getIfPresent(ip);
		if (banExpiry != null) {
			if (Instant.now().isBefore(banExpiry)) {
				long secondsRemaining = Duration.between(Instant.now(), banExpiry).getSeconds();
				log.warn("IP {} bloqueada. Restan {} segundos.", ip, secondsRemaining);
				AuditLogger.info("IP bloqueada por rate limit: " + ip, Map.of("ip", ip, "path", path, ACTION, "ban",
						RETRY_AFTER, String.valueOf(secondsRemaining)));
				writeErrorResponse(response, secondsRemaining);
				return;
			} else {
				bannedIps.invalidate(ip);
				log.info("IP {} desbloqueada tras ban extendido.", ip);
				AuditLogger.info("IP desbloqueada tras ban: " + ip, Map.of("ip", ip, "path", path, ACTION, "unban"));
			}
		}

		Bucket bucket = isLogin ? loginBuckets.get(ip, this::createLoginBucket)
				: generalBuckets.get(ip, this::createGeneralBucket);

		if (bucket.tryConsume(1))
			filterChain.doFilter(request, response);
		else
			handleIpBlock(ip, isLogin, request, response);
		
	}

	private void handleIpBlock(String ip, boolean isLogin, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Instant now = Instant.now();
		AtomicInteger count = ipBlockCounts.get(ip, k -> new AtomicInteger(0));
		Instant lastAttempt = lastBlockAttempt.getIfPresent(ip);

		if (lastAttempt == null || Duration.between(lastAttempt, now).toMinutes() >= blockCountResetMinutes)
			count.set(0);
		
		lastBlockAttempt.put(ip, now);

		int newCount = count.incrementAndGet();
		ipBlockCounts.put(ip, count);

		String path = request.getRequestURI();

		if (newCount >= maxBlocks) {
			// Bloqueo extendido
			Instant banUntil = now.plus(Duration.ofMinutes(banDurationMinutes));
			bannedIps.put(ip, banUntil);
			count.set(0);

			long secondsRemaining = Duration.between(now, banUntil).getSeconds();

			if (isLogin) {
				log.warn("IP {} baneada {} minutos por abuso en login", ip, banDurationMinutes);
				AuditLogger.info("IP baneada por abuso en login: " + ip, Map.of("ip", ip, "path", path, ACTION, "ban",
						RETRY_AFTER, String.valueOf(secondsRemaining)));
			} else {
				log.warn("IP {} alcanzó maxBlocks ({}) en endpoint general y será bloqueada {} minutos", ip, maxBlocks,
						banDurationMinutes);
				AuditLogger.info("IP baneada por exceso de solicitudes: " + ip, Map.of("ip", ip, "path", path, ACTION,
						"ban", RETRY_AFTER, String.valueOf(secondsRemaining)));
			}

			writeErrorResponse(response, secondsRemaining);
		} else {
			if (isLogin) {
				AuditLogger.info("Petición login rate limit excedido: " + ip,
						Map.of("ip", ip, "path", path, ACTION, "rateLimit"));
			} else {
				AuditLogger.info("Petición general rate limit excedido: " + ip,
						Map.of("ip", ip, "path", path, ACTION, "rateLimit"));
			}

			writeErrorResponse(response, null);
		}
	}

	private Bucket createGeneralBucket(String key) {
		return Bucket.builder()
				.addLimit(limit -> limit.capacity(generalLimit).refillGreedy(generalLimit, Duration.ofMinutes(generalDurationMinutes)))
				.build();
	}

	private Bucket createLoginBucket(String key) {
		return Bucket.builder()
				.addLimit(limit -> limit.capacity(loginLimit).refillGreedy(loginLimit, Duration.ofMinutes(loginDurationMinutes)))
				.build();
	}

	private void writeErrorResponse(HttpServletResponse response, Long retryAfterSeconds) throws IOException {

		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		if (retryAfterSeconds != null) {
			response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
		}

		String json = """
				{
				  "timestamp": "%s",
				  "status": 429,
				  "error": "Too Many Requests",
				  "message": "Rate limit exceeded",
				  "retryAfterSeconds": %s
				}
				""".formatted(Instant.now(), retryAfterSeconds != null ? retryAfterSeconds : "null");

		response.getWriter().write(json);
	}

	private String getClientIp(HttpServletRequest request) {
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
		if (trustedProxies.isEmpty()) {
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
}
