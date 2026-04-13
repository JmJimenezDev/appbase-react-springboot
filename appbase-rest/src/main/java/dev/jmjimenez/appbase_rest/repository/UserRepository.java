package dev.jmjimenez.appbase_rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import dev.jmjimenez.appbase_rest.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	Optional<User> findByEmail(String username);
	
	boolean existsByEmail(String email);
	
	boolean existsByPhone(String phone);

	boolean existsByIdAndEmail(Long userId, String email);

}