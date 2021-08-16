package io.antrex.udemy.vertx_stock_brocker.db;

import io.antrex.udemy.vertx_stock_brocker.quotes.GetQuoteHandler;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbResponse {
    public static final Logger LOG = LoggerFactory.getLogger(GetQuoteHandler.class);

    public static Handler<Throwable> errorHandler(RoutingContext context, String message) {
        return err -> {
            LOG.error("Failure: ", err);
            context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                .end(new JsonObject()
                    .put("message", message)
                    .put("path", context.normalizedPath())
                    .toBuffer());
        };
    }

    public static void notFound(RoutingContext context, String message) {
        context.response()
            .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(new JsonObject()
                .put("message", message)
                .put("path", context.normalizedPath())
                .toBuffer());
    }
}
