package com.sgerest.domain.services;

import lombok.extern.log4j.Log4j2;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class DatabaseMigrationService {

    private final Flyway flyway;

    public DatabaseMigrationService(Flyway flyway) {
        this.flyway = flyway;
    }

    public int executeMigrations() {
        try {
            log.info("Iniciando execução de migrações Flyway...");
            int count = flyway.migrate().migrationsExecuted;
            log.info("Migrações executadas com sucesso. Total: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Erro ao executar migrações: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao executar migrações do banco de dados", e);
        }
    }

}