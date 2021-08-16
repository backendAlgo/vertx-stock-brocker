package io.antrex.udemy.vertx_stock_brocker;

import io.antrex.udemy.vertx_stock_brocker.config.ConfigLoader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionInfoVerticle extends AbstractVerticle {
    public static final Logger LOG = LoggerFactory.getLogger(VersionInfoVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        ConfigLoader.load(vertx)
            .onFailure(startPromise::fail)
            .onSuccess(config -> {
                LOG.info("Current Application Version is: {}", config.getVersion());
                startPromise.complete();
            });
    }
}
