package dev.jmjimenez.appbase_rest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

@RestController
public class StatusController {

    private final DataSource dataSource;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private String serverPort;

    public StatusController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/api/admin/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("application", appName);
        status.put("serverPort", serverPort);
        status.put("uptime", System.currentTimeMillis());

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            status.put("dbProductName", metaData.getDatabaseProductName());
            status.put("dbProductVersion", metaData.getDatabaseProductVersion());
            status.put("dbURL", metaData.getURL());
            status.put("dbUser", metaData.getUserName());
            status.put("dbStatus", "CONNECTED");
        } catch (Exception e) {
            status.put("dbStatus", "ERROR: " + e.getMessage());
        }

        return ResponseEntity.ok(status);
    }
}
