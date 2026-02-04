package com.sgerest.domain.services;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

@Service
public class DatabaseMigrationService {

    private final Flyway flyway;

    public DatabaseMigrationService(Flyway flyway) {
        this.flyway = flyway;
    }

    public void executeMigrations() {
        flyway.migrate();
    }

}