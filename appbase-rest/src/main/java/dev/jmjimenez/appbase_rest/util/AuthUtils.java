package dev.jmjimenez.appbase_rest.util;

import java.util.Collection;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import dev.jmjimenez.appbase_rest.entity.User;
import dev.jmjimenez.appbase_rest.entity.enums.RoleName;
import dev.jmjimenez.appbase_rest.exception.AuthenticationException;
import dev.jmjimenez.appbase_rest.exception.BadRequestException;
import dev.jmjimenez.appbase_rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component("authUtils")
@RequiredArgsConstructor
public class AuthUtils {

    private final UserRepository userRepository;

    public boolean isAuthenticated() {
        return getAuthentication()
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    public String getCurrentUserEmail() {
        return getUserDetails()
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new BadRequestException("User not authenticated"));
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getUserDetails()
                .map(UserDetails::getAuthorities)
                .orElseThrow(() -> new BadRequestException("User not authenticated"));
    }

    public boolean hasRole(String role) {
        return getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));
    }

    public boolean isAdmin() {
        return hasRole(RoleName.ROLE_ADMIN.name());
    }

    public boolean isOwnerByUserId(Long userId) {
        String email = getCurrentUserEmail();
        return userRepository.existsByIdAndEmail(userId, email);
    }

    public boolean isOwnerByEmail(String email) {
        String currentEmail = getCurrentUserEmail();
        return currentEmail.equals(email);
    }

    public void validateAuthenticated() {
        if (!isAuthenticated())
            throw new BadRequestException("User not authenticated");
    }

    public void validateAdmin() {
        if (!isAdmin())
            throw new AccessDeniedException("Admin role required");
    }

    public void validateOwnerByUserIdOrAdmin(Long userId) {
        if (!isOwnerByUserId(userId) && !isAdmin())
            throw new AuthenticationException("You are not allowed to access this resource");
    }

    public void validateOwnerByEmailOrAdmin(String email) {
        if (!isOwnerByEmail(email) && !isAdmin())
            throw new AuthenticationException("You are not allowed to access this resource");
    }

    public User getCurrentUserEntity() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }
    
    private Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    private Optional<UserDetails> getUserDetails() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .filter(UserDetails.class::isInstance)
                .map(UserDetails.class::cast);
    }
}
