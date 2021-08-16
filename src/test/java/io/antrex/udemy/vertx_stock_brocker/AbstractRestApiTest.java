package io.antrex.udemy.vertx_stock_brocker;

import io.antrex.udemy.vertx_stock_brocker.config.ConfigLoader;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractRestApiTest {
    public static final Logger LOG = LoggerFactory.getLogger(AbstractRestApiTest.class);
    public static final int TEST_SERVER_PORT = 9000;

    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        System.setProperty(ConfigLoader.SERVER_PORT, String.valueOf(TEST_SERVER_PORT));
        System.setProperty(ConfigLoader.DB_HOST, "localhost");
        System.setProperty(ConfigLoader.DB_PORT, "5432");
        System.setProperty(ConfigLoader.DB_DATABASE, "vertx-stock-broker");
        System.setProperty(ConfigLoader.DB_USER, "postgres");
        System.setProperty(ConfigLoader.DB_PASSWORD, "secret");
        LOG.warn("Tests are using local database !!!");
        vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
    }

    protected WebClient webClient(Vertx vertx) {
        return WebClient.create(vertx, new WebClientOptions()
            .setDefaultPort(TEST_SERVER_PORT));
    }
}
