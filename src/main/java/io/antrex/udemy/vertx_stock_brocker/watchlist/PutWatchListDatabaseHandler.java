package io.antrex.udemy.vertx_stock_brocker.watchlist;

import io.antrex.udemy.vertx_stock_brocker.config.DbConfig;
import io.antrex.udemy.vertx_stock_brocker.db.DbResponse;
import io.antrex.udemy.vertx_stock_brocker.quotes.GetQuoteHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.spi.observability.HttpResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PutWatchListDatabaseHandler implements Handler<RoutingContext> {
    public static final Logger LOG = LoggerFactory.getLogger(GetQuoteHandler.class);
    private final Pool db;

    public PutWatchListDatabaseHandler(Pool db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        final var accountId = WatchListRestApi.getAccountId(context);
        var json = context.getBodyAsJson();
        var watchlist = json.mapTo(WatchList.class);
        // change to collectors list
        var parameterBatch = watchlist.getAssets().stream()
            .<Map<String, Object>>map(asset -> Map.of(
                "account_id", accountId,
                "asset", asset.getName())
            ).collect((Supplier<ArrayList<Map<String, Object>>>) ArrayList::new,
                ArrayList::add, ArrayList::addAll);
        // Transaction
        db.withTransaction(client -> {
            // 1 - Delete all for account_id
            return SqlTemplate.forUpdate(client, "DELETE FROM broker.watchlist w WHERE w.account_id = #{account_id}")
                .execute(Map.of("account_id", accountId))
                .onFailure(DbResponse.errorHandler(context, "Failed to clear watchlist for accountId: " + accountId))
                .compose(next -> {
                    // 2 - Add all for account_id
                    return addAllForAccountId(client, context, parameterBatch);
                })
                .onFailure(DbResponse.errorHandler(context, "Failed to update watchlist of accountId: " + accountId))
                .onSuccess(result -> {
                    // 3 -Both succeeded
                    context.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code())
                        .end();
                });
        });


    }

    private Future<SqlResult<Void>> addAllForAccountId(SqlConnection client, RoutingContext context, ArrayList<Map<String, Object>> parameterBatch) {
        return SqlTemplate.forUpdate(client,
                "INSERT INTO broker.watchlist VALUES (#{account_id},#{asset}) " +
                    "ON CONFLICT (account_id, asset) DO NOTHING")
            .executeBatch(parameterBatch)
            .onFailure(DbResponse.errorHandler(context, "Failed to insert into watchlist"));
    }
}
