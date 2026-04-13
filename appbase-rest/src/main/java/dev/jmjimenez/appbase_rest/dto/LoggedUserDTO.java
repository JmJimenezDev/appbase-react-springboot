package dev.jmjimenez.appbase_rest.dto;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoggedUserDTO {
	
	private Long id;
	private String name;
	private String surnames;
	private String email;
	private String phone;
	private boolean emailVerified;
	private Collection<String> roles;
	
}
