package dev.jmjimenez.appbase_rest.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import dev.jmjimenez.appbase_rest.config.RateLimitingFilter;
import dev.jmjimenez.appbase_rest.entity.enums.RoleName;
import dev.jmjimenez.appbase_rest.service.JpaUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final JpaUserDetailsService customUserDetailsService;
	private final RateLimitingFilter rateLimitingFilter;
	private final JWTAuthFilter jwtAuthFilter;
	private final CsrfValidationFilter csrfValidationFilter;

	@Value("${app.cors.allowed-origins}")
	private String allowedOrigins;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				// Desactivar CSRF automático de Spring Security
				.csrf(csrf -> csrf.disable())

				.headers(headers -> headers
						.httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
						.frameOptions(frame -> frame.sameOrigin()).contentTypeOptions(content -> {
						})
						.contentSecurityPolicy(csp -> csp.policyDirectives(
								"default-src 'self'; script-src 'self'; object-src 'none'; frame-ancestors 'self';")))

				.cors(cors -> cors.configurationSource(corsConfigurationSource()))

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.exceptionHandling(ex -> ex.authenticationEntryPoint(new Http401UnauthorizedEntryPoint())
						.accessDeniedHandler(accessDeniedHandler()))

				.authorizeHttpRequests(auth -> auth
						// Rutas públicas
						.requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh", "/public/**",
								"/api/email/reset-password", "/api-docs/**", "/api-docs.yaml", "/swagger-ui/**",
								"/swagger-ui.html")
						.permitAll()
						// Rutas admin
						.requestMatchers("/api/admin/**").hasAuthority(RoleName.ROLE_ADMIN.name())
						// Rutas protegidas de usuario o admin
						.requestMatchers("/api/**")
						.hasAnyAuthority(RoleName.ROLE_USER.name(), RoleName.ROLE_ADMIN.name()).anyRequest()
						.authenticated())

				// AuthenticationProvider
				.authenticationProvider(authenticationProvider(customUserDetailsService, passwordEncoder()))

				.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterAfter(csrfValidationFilter, JWTAuthFilter.class);

		return http.build();
	}

	@Bean
	AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService); // FIX
		authProvider.setPasswordEncoder(passwordEncoder);
		return authProvider;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		List<String> origins = Arrays.asList(allowedOrigins.split(","));
		config.setAllowedOrigins(origins);

		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-CSRF-TOKEN"));
		config.setAllowCredentials(true);
		config.setExposedHeaders(List.of("Authorization", "X-CSRF-TOKEN"));
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	AccessDeniedHandler accessDeniedHandler() {
		AccessDeniedHandlerImpl handler = new AccessDeniedHandlerImpl();
		handler.setErrorPage(null); // no redirect
		return (request, response, ex) -> {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Access Denied\"}");
		};
	}
}
