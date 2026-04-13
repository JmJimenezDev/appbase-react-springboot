package dev.jmjimenez.appbase_rest.entity;

import java.io.Serializable;

import dev.jmjimenez.appbase_rest.entity.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends Auditable implements Serializable {

	private static final long serialVersionUID = 8345326497604730860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	@Enumerated(EnumType.STRING)
	private RoleName name;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Role role))
			return false;
		return id != null && id.equals(role.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
