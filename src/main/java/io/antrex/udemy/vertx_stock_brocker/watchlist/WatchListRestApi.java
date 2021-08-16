package io.antrex.udemy.vertx_stock_brocker.watchlist;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WatchListRestApi {
    public static final Logger LOG = LoggerFactory.getLogger(WatchListRestApi.class);

    public static void attach(Router parent, Pool db) {
        final Map<UUID, WatchList> watchListPerAccount = new HashMap<>();
        final String path = "/account/watchlist/:accountId";
        parent.get(path).handler(new GetWatchListHandler(watchListPerAccount));
        parent.put(path).handler(new PutWatchListHandler(watchListPerAccount));
        parent.delete(path).handler(new DeleteWatchListHandler(watchListPerAccount));
        final String pgPath = "/pg/account/watchlist/:accountId";
        parent.get(pgPath).handler(new GetWatchListDatabaseHandler(db));
        parent.put(pgPath).handler(new PutWatchListDatabaseHandler(db));
        parent.delete(pgPath).handler(new DeleteWatchListDatabaseHandler(db));
    }

    static String getAccountId(RoutingContext context) {
        var accountId = context.pathParam("accountId");
        LOG.debug("{} for account {}", context.normalizedPath(), accountId);
        return accountId;
    }


}
