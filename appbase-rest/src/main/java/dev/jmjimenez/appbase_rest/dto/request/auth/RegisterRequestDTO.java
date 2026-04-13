package dev.jmjimenez.appbase_rest.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

	@NotBlank(message = "El nombre no puede estar vacío")
	@Size(max = 50)
	private String name;

	@NotBlank(message = "Los apellidos no pueden estar vacíos")
	@Size(max = 100)
	private String surnames;

	@NotBlank(message = "El email no puede estar vacío")
	@Email(message = "Email inválido")
	private String email;

	@NotBlank(message = "El teléfono no puede estar vacío")
	@Pattern(regexp = "^\\d{9,15}$")
	private String phone;

	@NotBlank(message = "La contraseña no puede estar vacía")
	@Size(min = 8, message = "Mínimo 8 caracteres")
	private String password;
}
