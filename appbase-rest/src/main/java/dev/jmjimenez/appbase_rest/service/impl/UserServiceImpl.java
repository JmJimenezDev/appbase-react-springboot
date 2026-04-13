package dev.jmjimenez.appbase_rest.service.impl;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lowagie.text.DocumentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.jmjimenez.appbase_rest.dto.UserDTO;
import dev.jmjimenez.appbase_rest.dto.request.user.UpdateUserCredentialsDTO;
import dev.jmjimenez.appbase_rest.dto.request.user.UpdateUserRequestDTO;
import dev.jmjimenez.appbase_rest.entity.User;
import dev.jmjimenez.appbase_rest.exception.BadRequestException;
import dev.jmjimenez.appbase_rest.exception.ResourceAlreadyExistsException;
import dev.jmjimenez.appbase_rest.exception.ResourceNotFoundException;
import dev.jmjimenez.appbase_rest.mapper.UserMapper;
import dev.jmjimenez.appbase_rest.repository.UserRepository;
import dev.jmjimenez.appbase_rest.service.UserService;
import dev.jmjimenez.appbase_rest.util.AuthUtils;
import dev.jmjimenez.appbase_rest.util.Constants;
import dev.jmjimenez.appbase_rest.util.ExportUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private static final String CREATED_AT = "createdAt";
	private static final String PHONE = "phone";
	private static final String SURNAMES = "surnames";
	private static final String ROLE = "role";
	private static final String EMAIL_VERIFIED = "emailVerified";
	private static final String ENABLED = "enabled";
	private static final String EMAIL = "email";
	private static final String NAME = "name";
	private static final String ID = "id";

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	private final AuthUtils authUtils;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Page<UserDTO> getAllUsers(Specification<User> spec, Pageable pageable) {
		authUtils.validateAdmin();
		return userRepository.findAll(spec, pageable).map(userMapper::toUserDTO);
	}

	@Override
	public UserDTO getUserById(Long userId) {
		authUtils.validateAdmin();
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		return userMapper.toUserDTO(user);
	}

	@Override
	public UserDTO createUser(UserDTO userDTO) {
		authUtils.validateAdmin();
		User user = userMapper.toUserEntity(userDTO);
		userRepository.save(user);
		return userMapper.toUserDTO(user);
	}

	@Override
	public UserDTO updateUser(Long userId, UpdateUserRequestDTO request) {
		// Validación de ownership en runtime
		authUtils.validateOwnerByUserIdOrAdmin(userId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));

		if (request.getName() != null)
			user.setName(request.getName());
		if (request.getSurnames() != null)
			user.setSurnames(request.getSurnames());
		if (request.getPhone() != null)
			user.setPhone(request.getPhone());

		userRepository.save(user);
		return userMapper.toUserDTO(user);
	}

	@Override
	public void changePassword(Long userId, UpdateUserCredentialsDTO request) {
		authUtils.validateOwnerByUserIdOrAdmin(userId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));

		// Verificar oldPassword
		if (request.getOldPassword() == null
				|| !passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
			throw new AccessDeniedException("Contraseña actual incorrecta");
		}

		if (request.getNewPassword() == null || request.getNewPassword().length() < 8) {
			throw new BadRequestException("La nueva contraseña debe tener al menos 8 caracteres");
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
	}

	@Override
	public void changeEmail(Long userId, UpdateUserCredentialsDTO request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Constants.ROLE_ADMIN));
		if (!user.getEmail().equals(auth.getName()) && !isAdmin)
			throw new AccessDeniedException("No tienes permisos para cambiar el email de este usuario");

		if (request.getEmail() == null || request.getEmail().isBlank()
				|| !request.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
			throw new BadRequestException("Email inválido");

		if (userRepository.existsByEmail(request.getEmail()))
			throw new ResourceAlreadyExistsException("El email ya está en uso");

		user.setEmail(request.getEmail().trim().toLowerCase());
		userRepository.save(user);
	}

	@Override
	public void deleteUser(Long userId) {
		authUtils.validateAdmin();

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Constants.USER_NOT_FOUND));
		userRepository.delete(user);
	}

	@Override
	public void exportUsersToPDF(HttpServletResponse response, List<String> selectedFields)
			throws IOException, DocumentException {
		exportUsers(response, selectedFields, ExportType.PDF);
	}

	@Override
	public void exportUsersToCsv(HttpServletResponse response, List<String> selectedFields) throws IOException {
		exportUsers(response, selectedFields, ExportType.CSV);
	}

	private enum ExportType {
		PDF, CSV
	}

	private void exportUsers(HttpServletResponse response, List<String> selectedFields, ExportType type)
			throws IOException, DocumentException {

		authUtils.validateAdmin();

		List<User> users = userRepository.findAll();
		List<String> fieldsToExport = prepareUserFields(selectedFields, type);

		String[] headers = fieldsToExport.stream().map(this::mapFieldToHeader).toArray(String[]::new);

		if (type.equals(ExportType.PDF))
			ExportUtils.exportToPdf(response, users, headers,
					user -> fieldsToExport.stream().map(field -> mapFieldToValue(user, field)).toArray(String[]::new),
					"users");
		else
			ExportUtils
					.exportToCsv(
							response, users, headers, user -> fieldsToExport.stream()
									.map(field -> mapFieldToValue(user, field)).collect(Collectors.joining(";")),
							"users");
	}

	private List<String> prepareUserFields(List<String> selectedFields, ExportType type) {
		List<String> defaultFields = new ArrayList<>(List.of(ID, NAME, SURNAMES, EMAIL, ENABLED, EMAIL_VERIFIED, ROLE));
		if (type == ExportType.CSV)
			defaultFields.add(CREATED_AT);

		if (selectedFields == null || selectedFields.isEmpty())
			return defaultFields;
		else {
			List<String> fields = new ArrayList<>(selectedFields);
			if (!fields.contains(ID))
				fields.add(0, ID);
			return fields;
		}
	}

	private String mapFieldToHeader(String field) {
		return switch (field) {
		case ID -> "ID";
		case NAME -> "Nombre";
		case SURNAMES -> "Apellidos";
		case EMAIL -> "Email";
		case PHONE -> "Telefono";
		case ROLE -> "Rol";
		case EMAIL_VERIFIED -> "EmailVerificado";
		case ENABLED -> "Habilitado";
		case CREATED_AT -> "FechaCreacion";
		default -> field;
		};
	}

	private String mapFieldToValue(User user, String field) {
		return switch (field) {
		case ID -> String.valueOf(user.getId());
		case NAME -> user.getName();
		case SURNAMES -> user.getSurnames();
		case EMAIL -> user.getEmail();
		case PHONE -> user.getPhone();
		case ROLE -> user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.joining(";"));
		case EMAIL_VERIFIED -> String.valueOf(user.isEmailVerified());
		case ENABLED -> String.valueOf(user.isEnabled());
		case CREATED_AT -> user.getCreatedAt().format(DATE_FORMATTER);
		default -> "";
		};
	}
}
