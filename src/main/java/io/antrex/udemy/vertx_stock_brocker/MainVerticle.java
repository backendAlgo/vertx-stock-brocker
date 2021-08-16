package io.antrex.udemy.vertx_stock_brocker;

import io.antrex.udemy.vertx_stock_brocker.config.ConfigLoader;
import io.antrex.udemy.vertx_stock_brocker.db.migration.FlywayMigration;
import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainVerticle extends AbstractVerticle {
    public static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.exceptionHandler(err ->
            LOG.error("Unhandled: ", err));
        vertx.deployVerticle(new MainVerticle())
            .onFailure(err -> LOG.error("Failed to deploy", err))
            .onSuccess(id ->
                LOG.info("Deployed {} with id {}", MainVerticle.class.getSimpleName(), id)
            );
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.deployVerticle(VersionInfoVerticle.class.getName())
            .onFailure(startPromise::fail)
            .onSuccess(id -> LOG.info("Deployed {} with id {}", VersionInfoVerticle.class.getSimpleName(), id))
            .compose(next -> migrateDatabase())
            .onFailure(startPromise::fail)
            .onSuccess(id -> LOG.info("Migrated db schema to latest version!"))
            .compose(next -> deployRestApi(startPromise));

    }

    private Future<Void> migrateDatabase() {
        return ConfigLoader.load(vertx)
            .compose(config -> FlywayMigration.migrate(vertx, config.getDbConfig()));
    }

    private Future<String> deployRestApi(Promise<Void> startPromise) {
        return vertx.deployVerticle(RestApiVerticle.class.getName(),
                new DeploymentOptions()
                    .setInstances(processors())
            )
            .onFailure(startPromise::fail)
            .onSuccess(id -> {
                LOG.info("Deployed {} with id {}", RestApiVerticle.class.getSimpleName(), id);
                startPromise.complete();
            });
    }

    private int processors() {
        return Math.max(1, Runtime.getRuntime().availableProcessors());
    }

}
