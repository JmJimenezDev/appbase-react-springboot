package dev.jmjimenez.appbase_rest.config;
import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
	    var auth = SecurityContextHolder.getContext().getAuthentication();
	    return (auth != null && auth.isAuthenticated()) ? Optional.of(auth.getName()) : Optional.of("SYSTEM");
	}

}
