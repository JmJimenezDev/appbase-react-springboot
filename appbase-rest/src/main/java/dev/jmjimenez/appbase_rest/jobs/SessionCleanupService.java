package dev.jmjimenez.appbase_rest.jobs;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dev.jmjimenez.appbase_rest.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionCleanupService {

    private final UserSessionRepository userSessionRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanOldSessions() {
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);

        int deleted = userSessionRepository.deleteOldInactiveSessions(cutoff);

        log.info("Sesiones antiguas eliminadas: {}", deleted);
    }
}