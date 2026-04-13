package dev.jmjimenez.appbase_rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.jmjimenez.appbase_rest.dto.request.auth.RegisterRequestDTO;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void register_Success() throws Exception {
		RegisterRequestDTO request = new RegisterRequestDTO();
		request.setName("Juan");
		request.setSurnames("Pérez");
		request.setEmail("juan@test.com");
		request.setPhone("612345678");
		request.setPassword("password123");

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message").exists());
	}

	@Test
	void login_InvalidCredentials() throws Exception {
		var request = new dev.jmjimenez.appbase_rest.dto.request.auth.LoginRequestDTO();
		request.setEmail("nonexistent@test.com");
		request.setPassword("password123");

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getProfile_Unauthenticated() throws Exception {
		mockMvc.perform(get("/api/auth/profile"))
				.andExpect(status().isUnauthorized());
	}
}
