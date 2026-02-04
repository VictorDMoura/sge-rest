package com.sgerest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sgerest.domain.services.DatabaseMigrationService;

@RestController
@RequestMapping("v1/maintenance")
public class MaintenanceController {

    private final DatabaseMigrationService databaseMigrationService;

    public MaintenanceController(DatabaseMigrationService databaseMigrationService) {
        this.databaseMigrationService = databaseMigrationService;
    }

    @PostMapping("/run-migrations")
    public ResponseEntity<Void> runMigrations() {
        databaseMigrationService.executeMigrations();
        return ResponseEntity.ok().build();
    }

}