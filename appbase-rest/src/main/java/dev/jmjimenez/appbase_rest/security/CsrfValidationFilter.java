package dev.jmjimenez.appbase_rest.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CsrfValidationFilter extends OncePerRequestFilter {

	private static final List<String> EXCLUDED_PATHS = List.of("/api/auth/login", "/api/auth/register",
			"/api/auth/refresh");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();

		if (EXCLUDED_PATHS.contains(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		if (isUnsafeMethod(request.getMethod())) {

			String csrfHeader = request.getHeader("X-CSRF-TOKEN");
			String csrfCookie = getCookieValue(request, "csrf_token");

			if (csrfHeader == null || csrfCookie == null || !csrfHeader.equals(csrfCookie)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.setContentType("application/json");
				response.getWriter().write("{\"error\":\"CSRF token inválido o ausente\"}");
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private boolean isUnsafeMethod(String method) {
		return HttpMethod.POST.matches(method) || HttpMethod.PUT.matches(method) || HttpMethod.DELETE.matches(method)
				|| HttpMethod.PATCH.matches(method);
	}

	private String getCookieValue(HttpServletRequest request, String name) {
		if (request.getCookies() == null)
			return null;

		for (Cookie cookie : request.getCookies()) {
			if (name.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
