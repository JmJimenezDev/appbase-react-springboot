package dev.jmjimenez.appbase_rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import dev.jmjimenez.appbase_rest.entity.Role;
import dev.jmjimenez.appbase_rest.entity.enums.RoleName;
import dev.jmjimenez.appbase_rest.repository.RoleRepository;
import dev.jmjimenez.appbase_rest.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		
		if (roleRepository.count() == 0) {
			roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build());
			roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build());
		}
	}

	@Test
	void listUsers_Unauthenticated() throws Exception {
		mockMvc.perform(get("/api/users"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "admin@test.com", roles = "ADMIN")
	void listUsers_AsAdmin_Success() throws Exception {
		mockMvc.perform(get("/api/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray());
	}

	@Test
	@WithMockUser(username = "user@test.com", roles = "USER")
	void listUsers_AsUser_Forbidden() throws Exception {
		mockMvc.perform(get("/api/users"))
				.andExpect(status().isForbidden());
	}
}
