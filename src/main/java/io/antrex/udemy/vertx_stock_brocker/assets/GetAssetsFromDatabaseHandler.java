package io.antrex.udemy.vertx_stock_brocker.assets;

import io.antrex.udemy.vertx_stock_brocker.db.DbResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAssetsFromDatabaseHandler implements Handler<RoutingContext> {
    public static final Logger LOG = LoggerFactory.getLogger(GetAssetsHandler.class);
    private final Pool db;

    public GetAssetsFromDatabaseHandler(Pool db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        db.query("SELECT a.value FROM broker.assets as a")
            .execute()
            .onFailure(DbResponse.errorHandler(context, "Failed to get assets from db!"))
            .onSuccess(result -> {
                var response = new JsonArray();
                result.forEach(row -> response.add(row.getValue("value")));
                LOG.info("Request path: {}, response with {}",
                    context.normalizedPath(),
                    response.encode());
                context.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .end(response.toBuffer());
            });
    }
}
