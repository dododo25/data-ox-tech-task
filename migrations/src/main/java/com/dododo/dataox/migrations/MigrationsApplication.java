package com.dododo.dataox.migrations;

import org.flywaydb.core.Flyway;

public class MigrationsApplication {

    public static void main(String[] args) {
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:postgresql://localhost:5432/dataox", "postgres", "postgres")
                .load();

        flyway.migrate();
    }
}
