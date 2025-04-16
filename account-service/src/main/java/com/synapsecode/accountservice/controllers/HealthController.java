package com.synapsecode.accountservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts/health")
public class HealthController {
    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        log.info("Health check request received");

        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("service", "Account Service");
        healthStatus.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(healthStatus);
    }
}