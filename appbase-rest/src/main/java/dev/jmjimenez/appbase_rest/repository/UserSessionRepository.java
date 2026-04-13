package dev.jmjimenez.appbase_rest.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import dev.jmjimenez.appbase_rest.entity.User;
import dev.jmjimenez.appbase_rest.entity.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

	Optional<UserSession> findByUserAndDeviceId(User user, String deviceId);
	
	@Modifying
	@Query("""
	    DELETE FROM UserSession s
	    WHERE s.active = false
	    AND s.createdAt < :cutoff
	""")
	int deleteOldInactiveSessions(Instant cutoff);

}
