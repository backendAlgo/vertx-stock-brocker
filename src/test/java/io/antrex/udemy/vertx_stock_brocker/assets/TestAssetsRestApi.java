package io.antrex.udemy.vertx_stock_brocker.assets;

import io.antrex.udemy.vertx_stock_brocker.AbstractRestApiTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class TestAssetsRestApi extends AbstractRestApiTest {
    public static final Logger LOG = LoggerFactory.getLogger(TestAssetsRestApi.class);


    @Test
    void return_all_assets(Vertx vertx, VertxTestContext testContext) throws Throwable {
        var client = webClient(vertx);
        client.get("/assets")
            .send()
            .onComplete(testContext.succeeding(response -> {
                var jsonArray = response.bodyAsJsonArray();
                LOG.info("Response: {}", jsonArray);
                assertEquals("[{\"name\":\"AAPL\"},{\"name\":\"AMZN\"},{\"name\":\"FB\"},{\"name\":\"GOOG\"},{\"name\":\"MSFT\"},{\"name\":\"NFLX\"},{\"name\":\"TSLA\"}]",
                    jsonArray.encode());
                assertEquals(200, response.statusCode());
                testContext.completeNow();
            }));
    }
}
