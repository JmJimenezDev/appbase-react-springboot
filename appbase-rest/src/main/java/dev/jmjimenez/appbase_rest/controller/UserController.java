package dev.jmjimenez.appbase_rest.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.lowagie.text.DocumentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.jmjimenez.appbase_rest.dto.UserDTO;
import dev.jmjimenez.appbase_rest.dto.request.user.UpdateUserCredentialsDTO;
import dev.jmjimenez.appbase_rest.dto.request.user.UpdateUserRequestDTO;
import dev.jmjimenez.appbase_rest.entity.User;
import dev.jmjimenez.appbase_rest.service.UserService;
import dev.jmjimenez.appbase_rest.specification.UserSpecifications;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public Page<UserDTO> listUsers(@RequestParam(required = false) String search,
			@RequestParam(required = false) String role, @RequestParam(required = false) String emailVerified,
			@RequestParam(required = false) String enabled, @RequestParam(required = false) String name,
			@RequestParam(required = false) String surnames, @RequestParam(required = false) String email,
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
			Pageable pageable) {

		Boolean emailVerifiedFilter = emailVerified != null ? Boolean.valueOf(emailVerified) : null;
		Boolean enabledFilter = enabled != null ? Boolean.valueOf(enabled) : null;

		Specification<User> spec = UserSpecifications.globalSearch(search).and(UserSpecifications.hasRole(role))
				.and(UserSpecifications.isEmailVerified(emailVerifiedFilter))
				.and(UserSpecifications.isEnabled(enabledFilter));

		Specification<User> advancedSpec = UserSpecifications.hasName(name).and(UserSpecifications.hasSurname(surnames))
				.and(UserSpecifications.hasEmail(email)).and(UserSpecifications.hasPhone(phone))
				.and(UserSpecifications.hasStartDate(startDate)).and(UserSpecifications.hasEndDate(endDate));

		Specification<User> finalSpec = spec.and(advancedSpec);

		return userService.getAllUsers(finalSpec, pageable);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {

		return ResponseEntity.ok(userService.getUserById(id));
	}

	@PostMapping
	public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
		return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequestDTO request) {
		return ResponseEntity.ok(userService.updateUser(id, request));
	}

	@PutMapping("/{id}/password")
	public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody UpdateUserCredentialsDTO request) {
		userService.changePassword(id, request);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/email")
	public ResponseEntity<Void> changeEmail(@PathVariable Long id, @RequestBody UpdateUserCredentialsDTO request) {
		userService.changeEmail(id, request);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/export/pdf")
	public ResponseEntity<Void> exportToPDF(@RequestParam(required = false) String fields, HttpServletResponse response)
			throws DocumentException, IOException {

		List<String> selectedFields = (fields == null || fields.isEmpty()) ? null : Arrays.asList(fields.split(","));

		userService.exportUsersToPDF(response, selectedFields);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/export/csv")
	public ResponseEntity<Void> exportUsersToCsv(@RequestParam(required = false) String fields,
			HttpServletResponse response) throws IOException {

		List<String> selectedFields = (fields == null || fields.isEmpty()) ? null : Arrays.asList(fields.split(","));

		userService.exportUsersToCsv(response, selectedFields);
		return ResponseEntity.noContent().build();
	}
}
