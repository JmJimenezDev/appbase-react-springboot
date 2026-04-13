package dev.jmjimenez.appbase_rest.service;

import java.io.IOException;
import java.util.List;

import com.lowagie.text.DocumentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import dev.jmjimenez.appbase_rest.dto.UserDTO;
import dev.jmjimenez.appbase_rest.dto.request.user.UpdateUserCredentialsDTO;
import dev.jmjimenez.appbase_rest.dto.request.user.UpdateUserRequestDTO;
import dev.jmjimenez.appbase_rest.entity.User;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

	Page<UserDTO> getAllUsers(Specification<User> spec, Pageable pageable);

	UserDTO getUserById(Long userId);

	UserDTO createUser(UserDTO userDTO);

	UserDTO updateUser(Long userId, UpdateUserRequestDTO request);

	void changePassword(Long userId, UpdateUserCredentialsDTO request);

	void changeEmail(Long userId, UpdateUserCredentialsDTO request);

	void deleteUser(Long userId);

	void exportUsersToPDF(HttpServletResponse response, List<String> selectedFields)
			throws IOException, DocumentException;

	void exportUsersToCsv(HttpServletResponse response, List<String> selectedFields) throws IOException;

}
