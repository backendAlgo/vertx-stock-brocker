package io.antrex.udemy.vertx_stock_brocker.quotes;

import io.antrex.udemy.vertx_stock_brocker.MainVerticle;
import io.antrex.udemy.vertx_stock_brocker.db.DbResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class GetQuoteHandler implements Handler<RoutingContext> {
    public static final Logger LOG = LoggerFactory.getLogger(GetQuoteHandler.class);
    final Map<String, Quote> cachedQuotes;

    public GetQuoteHandler(Map<String, Quote> cachedQuotes) {
        this.cachedQuotes = cachedQuotes;
    }

    @Override
    public void handle(RoutingContext context) {
        LOG.info("Quotes context: {}", context.normalizedPath());
        String asset = context.pathParam("asset");
        Optional.ofNullable(cachedQuotes.get(asset))
            .ifPresentOrElse(quote -> {
                    var response = quote.toJsonObject();
                    LOG.info("Path {} responds with {}",
                        context.normalizedPath(),
                        response.encode());
                    context.end(response.toBuffer());
                }, () -> DbResponse.notFound(context, "quote for asset " + asset + " not available!")
            );
    }
}
