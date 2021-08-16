package io.antrex.udemy.vertx_stock_brocker;

import io.antrex.udemy.vertx_stock_brocker.assets.AssetsRestApi;
import io.antrex.udemy.vertx_stock_brocker.config.BrokerConfig;
import io.antrex.udemy.vertx_stock_brocker.config.ConfigLoader;
import io.antrex.udemy.vertx_stock_brocker.db.DbPools;
import io.antrex.udemy.vertx_stock_brocker.quotes.QuotesRestApi;
import io.antrex.udemy.vertx_stock_brocker.watchlist.WatchListRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {
    public static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        ConfigLoader.load(vertx)
            .onFailure(startPromise::fail)
            .onSuccess(config -> {
                LOG.info("Retrieved configuration: {}", config);
                startHttpServer(startPromise, config);
            });
    }

    private void startHttpServer(Promise<Void> startPromise, BrokerConfig config) {
        final Pool db = DbPools.createPgPool(config, vertx);

        Router restApi = Router.router(vertx);
        restApi.route()
            .handler(BodyHandler.create())
            .failureHandler(handlerFailure());

        AssetsRestApi.attach(restApi, db);
        QuotesRestApi.attach(restApi, db);
        WatchListRestApi.attach(restApi, db);
        vertx.createHttpServer()
            .requestHandler(restApi)
            .exceptionHandler(err -> LOG.error("HTTP Server error: ", err))
            .listen(config.getServerPort(), http -> {
                if (http.succeeded()) {
                    LOG.info("HTTP server started on port {}", config.getServerPort());
                    startPromise.complete();
                } else {
                    startPromise.fail(http.cause());
                }
            });
    }

    private Handler<RoutingContext> handlerFailure() {
        return err -> {
            if (err.response().ended()) {
                // Ignore completed response
                return;
            }
            LOG.error("Route Error: ", err.failure());
            err.response()
                .setStatusCode(500)
                .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());

        };
    }
}
