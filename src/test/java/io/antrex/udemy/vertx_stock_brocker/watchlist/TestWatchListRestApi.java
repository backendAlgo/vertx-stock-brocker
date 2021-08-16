package io.antrex.udemy.vertx_stock_brocker.watchlist;

import io.antrex.udemy.vertx_stock_brocker.AbstractRestApiTest;
import io.antrex.udemy.vertx_stock_brocker.assets.Asset;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestWatchListRestApi extends AbstractRestApiTest {
    public static final Logger LOG = LoggerFactory.getLogger(TestWatchListRestApi.class);

    @Test
    void adds_and_returns_watchlist_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
        var client = webClient(vertx);
        var accountId = UUID.randomUUID();
        client.put("/account/watchlist/" + accountId)
            .sendJsonObject(body())
            .onComplete(testContext.succeeding(response -> {
                var asJsonObject = response.bodyAsJsonObject();
                LOG.info("Response: {}", asJsonObject);
                assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", asJsonObject.encode());
                assertEquals(200, response.statusCode());
            }))
            .compose(next -> {
                client.get("/account/watchlist/" + accountId)
                    .send()
                    .onComplete(testContext.succeeding(response -> {
                        var asJsonObject = response.bodyAsJsonObject();
                        LOG.info("Response: {}", asJsonObject);
                        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", asJsonObject.encode());
                        assertEquals(200, response.statusCode());
                        testContext.completeNow();
                    }));
                return Future.succeededFuture();
            });
    }

    @Test
    void adds_and_deletes_watchlist_for_account(Vertx vertx, VertxTestContext context) {
        var client = webClient(vertx);
        var accountId = UUID.randomUUID();
        client.put("/account/watchlist/" + accountId)
            .sendJsonObject(body())
            .onComplete(context.succeeding(response -> {
                var asJsonObject = response.bodyAsJsonObject();
                LOG.info("Response: {}", asJsonObject);
                assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", asJsonObject.encode());
                assertEquals(200, response.statusCode());
            }))
            .compose(next -> {
                client.delete("/account/watchlist/" + accountId)
                    .send()
                    .onComplete(context.succeeding(response -> {
                        var asJsonObject = response.bodyAsJsonObject();
                        LOG.info("Response: {}", asJsonObject);
                        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", asJsonObject.encode());
                        assertEquals(200, response.statusCode());
                        context.completeNow();
                    }));
                return Future.succeededFuture();
            });
    }

    private JsonObject body() {
        return new WatchList(List.of(
            new Asset("AMZN"),
            new Asset("TSLA"))
        ).toJsonObject();
    }

}
