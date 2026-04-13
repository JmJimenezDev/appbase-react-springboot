package dev.jmjimenez.appbase_rest.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

	@NotBlank(message = "El email no puede estar vacío")
	@Email(message = "Email inválido")
	private String email;
	
	@NotBlank(message = "La contraseña no puede estar vacía")
	private String password;
	
}
