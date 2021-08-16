package io.antrex.udemy.vertx_stock_brocker.assets;

import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;

import java.util.List;


public class AssetsRestApi {
    public static final List<String> ASSETS =
        List.of("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA");

    public static void attach(Router parent, Pool db) {
        parent.get("/assets").handler(new GetAssetsHandler());
        parent.get("/pg/assets").handler(new GetAssetsFromDatabaseHandler(db));
    }
}
