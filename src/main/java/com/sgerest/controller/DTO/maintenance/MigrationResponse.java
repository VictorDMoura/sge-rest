package com.sgerest.controller.DTO.maintenance;

public record MigrationResponse(
        String message,
        int migrationsExecuted) {
}
