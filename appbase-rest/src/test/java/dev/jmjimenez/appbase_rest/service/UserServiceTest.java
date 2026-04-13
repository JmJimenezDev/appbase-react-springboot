package dev.jmjimenez.appbase_rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.jmjimenez.appbase_rest.dto.UserDTO;
import dev.jmjimenez.appbase_rest.entity.User;
import dev.jmjimenez.appbase_rest.exception.ResourceNotFoundException;
import dev.jmjimenez.appbase_rest.mapper.UserMapper;
import dev.jmjimenez.appbase_rest.repository.UserRepository;
import dev.jmjimenez.appbase_rest.service.impl.UserServiceImpl;
import dev.jmjimenez.appbase_rest.util.AuthUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthUtils authUtils;

	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(userRepository, userMapper, authUtils, passwordEncoder);
	}

	@Test
	void getUserById_Success() {
		User testUser = User.builder().id(1L).name("Juan").build();
		UserDTO userDTO = new UserDTO();
		userDTO.setId(1L);
		userDTO.setName("Juan");
		
		doNothing().when(authUtils).validateAdmin();
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(userMapper.toUserDTO(testUser)).thenReturn(userDTO);

		UserDTO result = userService.getUserById(1L);

		assertNotNull(result);
		assertEquals("Juan", result.getName());
		verify(authUtils).validateAdmin();
	}

	@Test
	void getUserById_NotFound() {
		doNothing().when(authUtils).validateAdmin();
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
	}

	@Test
	void changePassword_WrongOldPassword() {
		User testUser = User.builder().id(1L).password("encodedPassword").build();
		doNothing().when(authUtils).validateOwnerByUserIdOrAdmin(anyLong());
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		var request = new dev.jmjimenez.appbase_rest.dto.request.user.UpdateUserCredentialsDTO();
		request.setOldPassword("wrongPassword");
		request.setNewPassword("newpassword123");

		assertThrows(AccessDeniedException.class, () -> userService.changePassword(1L, request));
	}

	@Test
	void changePassword_NewPasswordTooShort() {
		User testUser = User.builder().id(1L).password("encodedPassword").build();
		doNothing().when(authUtils).validateOwnerByUserIdOrAdmin(anyLong());
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		var request = new dev.jmjimenez.appbase_rest.dto.request.user.UpdateUserCredentialsDTO();
		request.setOldPassword("password123");
		request.setNewPassword("123");

		assertThrows(Exception.class, () -> userService.changePassword(1L, request));
	}

	@Test
	void deleteUser_Success() {
		User testUser = User.builder().id(1L).build();
		doNothing().when(authUtils).validateAdmin();
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		doNothing().when(userRepository).delete(any(User.class));

		userService.deleteUser(1L);

		verify(userRepository).delete(testUser);
	}

	@Test
	void deleteUser_NotFound() {
		doNothing().when(authUtils).validateAdmin();
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
	}
}
