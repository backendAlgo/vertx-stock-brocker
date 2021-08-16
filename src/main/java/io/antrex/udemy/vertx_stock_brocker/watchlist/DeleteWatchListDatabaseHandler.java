package io.antrex.udemy.vertx_stock_brocker.watchlist;

import io.antrex.udemy.vertx_stock_brocker.db.DbResponse;
import io.antrex.udemy.vertx_stock_brocker.quotes.GetQuoteHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DeleteWatchListDatabaseHandler implements Handler<RoutingContext> {
    public static final Logger LOG = LoggerFactory.getLogger(GetQuoteHandler.class);
    private final Pool db;

    public DeleteWatchListDatabaseHandler(Pool db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        final var accountId = WatchListRestApi.getAccountId(context);
        SqlTemplate.forQuery(db, "DELETE FROM broker.watchlist WHERE account_id=#{account_id}")
            .execute(Map.of("account_id", accountId))
            .onFailure(DbResponse.errorHandler(context, "Failed to delete watchlist for accountId: " + accountId))
            .onSuccess(result -> {
                LOG.debug("Deleted {} rows for accountId {}", result.rowCount(), accountId);
                context.response()
                    .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
                    .end();
            });
    }
}
