package com.dododo.dataox.migrations;

import org.flywaydb.core.Flyway;

public class MigrationsApplication {

    public static void main(String[] args) {
        String dbUrl      = System.getenv("DB_URL");
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");

        Flyway flyway = Flyway.configure()
                .dataSource(dbUrl, dbUsername, dbPassword)
                .load();

        flyway.migrate();
    }
}
