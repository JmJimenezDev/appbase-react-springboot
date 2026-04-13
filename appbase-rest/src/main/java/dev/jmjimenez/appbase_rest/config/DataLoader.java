package dev.jmjimenez.appbase_rest.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import dev.jmjimenez.appbase_rest.entity.Role;
import dev.jmjimenez.appbase_rest.entity.enums.RoleName;
import dev.jmjimenez.appbase_rest.repository.RoleRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

	private final RoleRepository roleRepository;
	
	@Override
    public void run(String... args) {
        seedRole(RoleName.ROLE_ADMIN);
        seedRole(RoleName.ROLE_USER);
    }

    private void seedRole(RoleName roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
	
}
