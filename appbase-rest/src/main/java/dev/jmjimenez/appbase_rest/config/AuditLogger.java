package dev.jmjimenez.appbase_rest.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Logger exclusivo para auditoría.
 * Soporta niveles INFO, WARN, ERROR y añade automáticamente MDC.
 */
public class AuditLogger {
	
    private AuditLogger() {
        /* This utility class should not be instantiated */
    }

    private static final Logger logger = LoggerFactory.getLogger("AUDIT_LOGGER");

    // INFO
    public static void info(String action) {
        info(action, null);
    }

    public static void info(String action, Map<String, String> context) {
        withMDC(context, () -> logger.info(action));
    }

    // WARN
    public static void warn(String action) {
        warn(action, null);
    }

    public static void warn(String action, Map<String, String> context) {
        withMDC(context, () -> logger.warn(action));
    }

    // ERROR
    public static void error(String action) {
        error(action, null);
    }

    public static void error(String action, Map<String, String> context) {
        withMDC(context, () -> logger.error(action));
    }

    // ----- Helper -----
    private static void withMDC(Map<String, String> context, Runnable loggingAction) {
        try {
            if (context != null) {
                context.forEach(MDC::put); // agregar todos los pares clave/valor al MDC
            }
            loggingAction.run();
        } finally {
            if (context != null) {
                context.keySet().forEach(MDC::remove); // limpiar MDC para evitar fugas
            }
        }
    }
}
