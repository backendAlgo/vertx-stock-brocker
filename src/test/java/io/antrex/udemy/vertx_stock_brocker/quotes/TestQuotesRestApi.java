package io.antrex.udemy.vertx_stock_brocker.quotes;

import io.antrex.udemy.vertx_stock_brocker.AbstractRestApiTest;
import io.antrex.udemy.vertx_stock_brocker.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi extends AbstractRestApiTest {
    public static final Logger LOG = LoggerFactory.getLogger(TestQuotesRestApi.class);



    @Test
    void return_quotes(Vertx vertx, VertxTestContext testContext) throws Throwable {
        var client = webClient(vertx);
        client.get("/quotes/AAPL")
            .send()
            .onComplete(testContext.succeeding(response -> {
                var asJsonObject = response.bodyAsJsonObject();
                LOG.info("Response: {}", asJsonObject);
                var asset = asJsonObject.getJsonObject("asset").encode();
                assertEquals("{\"name\":\"AAPL\"}", asset);
                assertEquals(200, response.statusCode());
                testContext.completeNow();
            }));
    }



    @Test
    void return_not_found(Vertx vertx, VertxTestContext testContext) throws Throwable {
        var client = webClient(vertx);
        client.get("/quotes/aa")
            .send()
            .onComplete(testContext.succeeding(response -> {
                LOG.info("Response {}", response.bodyAsString());
                assertEquals(404, response.statusCode());
                testContext.completeNow();
            }));
    }
}
