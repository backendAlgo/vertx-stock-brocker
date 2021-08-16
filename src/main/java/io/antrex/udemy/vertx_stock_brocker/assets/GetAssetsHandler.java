package io.antrex.udemy.vertx_stock_brocker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GetAssetsHandler implements Handler<RoutingContext> {
    public static final Logger LOG = LoggerFactory.getLogger(GetAssetsHandler.class);
    public static final List<String> ASSETS =
        List.of("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA");

    @Override
    public void handle(RoutingContext context) {

        var response = ASSETS.stream()
            .map(Asset::new)
            .reduce(new JsonArray(), JsonArray::add, JsonArray::addAll);
        LOG.info("Request path: {}", context.normalizedPath());
//        artificialSleep(context);
        context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .putHeader("my-header", "my-value")
            .end(response.toBuffer());
    }

    /**
     * only use for testing purpose to scaling & load testing
     *
     * @param context
     */
    private void artificialSleep(RoutingContext context) {
        try {
            final int random = ThreadLocalRandom.current().nextInt(100, 300);
            if (random % 2 == 0) {
                Thread.sleep(200);
                context.response()
                    .setStatusCode(500)
                    .end("Sleeping");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
