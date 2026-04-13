package dev.jmjimenez.appbase_rest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Auditable implements UserDetails {

	private static final long serialVersionUID = -8916904309349302490L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre no puede estar vacío")
	@Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
	@Column(name = "name", nullable = false)
	private String name;

	@NotBlank(message = "Los apellidos no pueden estar vacíos")
	@Size(max = 100, message = "Los apellidos no pueden tener más de 100 caracteres")
	@Column(name = "surnames", nullable = false)
	private String surnames;

	@NotBlank(message = "El email no puede estar vacío")
	@Email(message = "Formato de email inválido")
	@Size(max = 100, message = "El email no puede tener más de 100 caracteres")
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@NotBlank(message = "El teléfono no puede estar vacío")
	@Pattern(regexp = "^\\d{9,15}$", message = "El teléfono debe contener solo dígitos y entre 9 y 15 caracteres")
	@Column(name = "phone", nullable = false, unique = false)
	private String phone;

	@NotBlank(message = "La contraseña no puede estar vacía")
	@Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
	@JsonIgnore
	@Column(name = "password", nullable = false)
	private String password;

	private boolean enabled;
	private boolean emailVerified;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@Builder.Default
	private Set<Role> roles = new HashSet<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).toList();
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof User user))
			return false;
		return id != null && id.equals(user.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}