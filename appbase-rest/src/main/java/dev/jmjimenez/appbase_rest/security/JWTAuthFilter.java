package dev.jmjimenez.appbase_rest.security;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.jmjimenez.appbase_rest.security.util.JWTUtils;
import dev.jmjimenez.appbase_rest.service.JpaUserDetailsService;
import dev.jmjimenez.appbase_rest.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {

	private final JWTUtils jwtUtils;
	private final JpaUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String jwtToken = jwtUtils.getCookieValue(request, "access_token");
		String authHeader = request.getHeader("Authorization");

		if (jwtToken == null && authHeader != null && authHeader.startsWith("Bearer ")) {
			jwtToken = authHeader.substring(7);
		}

		if (jwtToken == null) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String userEmail = jwtUtils.extractUsername(jwtToken);

			if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

				if (jwtUtils.isValidToken(jwtToken, userDetails, request)) {

					SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					securityContext.setAuthentication(authentication);
					SecurityContextHolder.setContext(securityContext);

					MDC.put(Constants.USER_ID, userEmail);
					MDC.put("ip", jwtUtils.extractIp(request));

				} else {
					log.warn("Token inválido o expirado para el usuario: {}", userEmail);
					respondUnauthorized(response, "Token inválido o expirado");
					return;
				}
			}

		} catch (Exception e) {
			log.error("Error al procesar el token JWT", e);
			respondUnauthorized(response, "Error al procesar el token");
			return;
		}

		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	private void respondUnauthorized(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write("{\"error\": \"" + message + "\"}");
	}
}
