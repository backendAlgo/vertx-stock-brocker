package io.antrex.udemy.vertx_stock_brocker.quotes;

import io.antrex.udemy.vertx_stock_brocker.assets.Asset;
import io.antrex.udemy.vertx_stock_brocker.assets.AssetsRestApi;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuotesRestApi {
    public static void attach(Router parent, Pool db) {
        var cachedQuotes = AssetsRestApi.ASSETS.stream()
            .collect(Collectors.toMap(Function.identity(),
                QuotesRestApi::initRandomQuote));

        parent.get("/quotes/:asset").handler(new GetQuoteHandler(cachedQuotes));
        parent.get("/pg/quotes/:asset").handler(new GetQuoteFromDatabaseHandler(db));
    }

    private static Quote initRandomQuote(String asset) {
        return Quote.builder()
            .asset(new Asset(asset))
            .ask(randomValue())
            .lastPrice(randomValue())
            .volume(randomValue())
            .bid(randomValue())
            .build();
    }

    private static BigDecimal randomValue() {
        return BigDecimal.valueOf(ThreadLocalRandom
            .current()
            .nextDouble(0, 100));
    }
}
