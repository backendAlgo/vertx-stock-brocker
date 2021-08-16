package io.antrex.udemy.vertx_stock_brocker.db.migration;

import io.antrex.udemy.vertx_stock_brocker.MainVerticle;
import io.antrex.udemy.vertx_stock_brocker.config.DbConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlywayMigration {
    public static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    public static Future<Void> migrate(Vertx vertx, DbConfig dbConfig) {
        // Flyway migration is blocking because uses JDBC
        LOG.debug("DB Config: {}", dbConfig);
        return vertx.<Void>executeBlocking(promise -> {
            execute(dbConfig);
            promise.complete();
        }).onFailure(err -> LOG.error("Failed to migrate db schema with error: ", err));
    }

    private static void execute(DbConfig dbConfig) {
        final String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
            dbConfig.getHost(),
            dbConfig.getPort(),
            dbConfig.getDatabase()
        );
        LOG.debug("Migrating DB schema using jdbc url: {}", jdbcUrl);
        var flyway = Flyway.configure()
            .dataSource(jdbcUrl, dbConfig.getUser(), dbConfig.getPassword())
            .schemas("broker")
            .defaultSchema("broker")
            .load();
        var current = Optional.ofNullable(flyway.info().current());
        current.ifPresent(info -> LOG.info("db schema is at version: {}", info.getVersion()));
        var pendingMigrations = flyway.info().pending();
        LOG.debug("Pending migrations are: {}", printMigrations(pendingMigrations));
        flyway.migrate();
    }

    private static String printMigrations(MigrationInfo[] pendingMigrations) {
        if (Objects.isNull(pendingMigrations)) {
            return "[]";
        }
        return Arrays.stream(pendingMigrations)
            .map(each -> each.getVersion() + " - " + each.getDescription())
            .collect(Collectors.joining(", ", "[", "]"));
    }
}
