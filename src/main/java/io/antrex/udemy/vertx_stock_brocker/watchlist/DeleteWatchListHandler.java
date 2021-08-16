package io.antrex.udemy.vertx_stock_brocker.watchlist;

import io.vertx.core.Handler;
import io.vertx.core.impl.HAManager;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {
    private final Map<UUID, WatchList> watchListPerAccount;

    public DeleteWatchListHandler(Map<UUID, WatchList> watchListPerAccount) {
        this.watchListPerAccount = watchListPerAccount;
    }

    @Override
    public void handle(RoutingContext context) {
        String accountId = WatchListRestApi.getAccountId(context);
        final var deleted = watchListPerAccount.remove(UUID.fromString(accountId));
        context.end(deleted.toJsonObject().toBuffer());
    }
}
