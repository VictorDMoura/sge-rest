package com.sgerest.controller;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sgerest.controller.DTO.maintenance.MigrationResponse;
import com.sgerest.domain.services.DatabaseMigrationService;
import com.sgerest.exception.ApiErrorResponse;

@RestController
@RequestMapping("v1/maintenance")
@Log4j2
public class MaintenanceController {

    private final DatabaseMigrationService databaseMigrationService;

    public MaintenanceController(DatabaseMigrationService databaseMigrationService) {
        this.databaseMigrationService = databaseMigrationService;
    }

    @PostMapping("/run-migrations")
    public ResponseEntity<Object> runMigrations() {
        try {
            log.warn("Requisição de execução de migrações recebida");
            int count = databaseMigrationService.executeMigrations();
            MigrationResponse response = new MigrationResponse(
                    "Migrações executadas com sucesso",
                    count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao executar migrações: {}", e.getMessage());
            String path = ServletUriComponentsBuilder.fromCurrentRequest()
                    .build()
                    .getPath();
            return ResponseEntity.status(500)
                    .body(new ApiErrorResponse(500, "Migration Error", e.getMessage(), path));
        }

    }

}