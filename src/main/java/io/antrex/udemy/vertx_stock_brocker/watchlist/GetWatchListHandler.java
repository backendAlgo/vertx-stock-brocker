package io.antrex.udemy.vertx_stock_brocker.watchlist;

import io.antrex.udemy.vertx_stock_brocker.MainVerticle;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class GetWatchListHandler implements Handler<RoutingContext> {
    public static final Logger LOG = LoggerFactory.getLogger(GetWatchListHandler.class);
    Map<UUID, WatchList> watchListPerAccount;

    public GetWatchListHandler(Map<UUID, WatchList> watchListPerAccount) {
        this.watchListPerAccount = watchListPerAccount;
    }

    @Override
    public void handle(RoutingContext context) {
        String accountId = WatchListRestApi.getAccountId(context);
        Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)))
            .ifPresentOrElse((watchList -> context.end(watchList.toJsonObject().toBuffer())), () -> {
                context.response()
                    .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
                    .end(new JsonObject()
                        .put("message", "quote for asset " + accountId + " not available")
                        .put("path", context.normalizedPath())
                        .toBuffer());
            });
    }


}
