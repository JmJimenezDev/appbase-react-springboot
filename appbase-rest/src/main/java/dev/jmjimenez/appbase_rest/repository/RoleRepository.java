package dev.jmjimenez.appbase_rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.jmjimenez.appbase_rest.entity.Role;
import dev.jmjimenez.appbase_rest.entity.enums.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
    Optional<Role> findByName(RoleName name);

	boolean existsByName(RoleName roleName);
    
}
