package io.antrex.udemy.vertx_stock_brocker.db;

import io.antrex.udemy.vertx_stock_brocker.config.BrokerConfig;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public class DbPools {
    public static PgPool createPgPool(BrokerConfig config, final Vertx vertx) {
        final var connectOptions = new PgConnectOptions()
            .setHost(config.getDbConfig().getHost())
            .setPort(config.getDbConfig().getPort())
            .setDatabase(config.getDbConfig().getDatabase())
            .setUser(config.getDbConfig().getUser())
            .setPassword(config.getDbConfig().getPassword());
        final var poolOptions = new PoolOptions()
            .setMaxSize(4);
        return PgPool.pool(vertx, connectOptions, poolOptions);
    }

    //


}
