package dev.jmjimenez.appbase_rest.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void protectedEndpoints_RequireAuth() throws Exception {
		mockMvc.perform(get("/api/users"))
				.andExpect(status().isUnauthorized());

		mockMvc.perform(get("/api/auth/profile"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void adminEndpoint_RequiresAdminRole() throws Exception {
		mockMvc.perform(get("/api/admin/status"))
				.andExpect(status().isUnauthorized());
	}
}
