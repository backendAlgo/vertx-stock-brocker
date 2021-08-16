package io.antrex.udemy.vertx_stock_brocker.config;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Optional;
import java.util.function.Supplier;

@Builder
@Value
@ToString
public class BrokerConfig {
    int serverPort;
    String version;
    DbConfig dbConfig;

    public static BrokerConfig from(JsonObject config) {
        final int serverPort = Optional.ofNullable(config.getInteger(ConfigLoader.SERVER_PORT))
            .orElseThrow(notConfiguredProperty(ConfigLoader.SERVER_PORT));
        final String version = Optional.ofNullable(config.getString("version"))
            .orElseThrow(notConfiguredProperty("version"));

        return BrokerConfig.builder()
            .serverPort(serverPort)
            .version(version)
            .dbConfig(parseDbConfig(config))
            .build();
    }

    private static DbConfig parseDbConfig(JsonObject config) {
        return DbConfig.builder()
            .host(config.getString(ConfigLoader.DB_HOST))
            .port(config.getInteger(ConfigLoader.DB_PORT))
            .database(config.getString(ConfigLoader.DB_DATABASE))
            .user(config.getString(ConfigLoader.DB_USER))
            .password(config.getString(ConfigLoader.DB_PASSWORD))
            .build();


    }

    private static Supplier<RuntimeException> notConfiguredProperty(String propertyName) {
        return () -> new RuntimeException(propertyName + " not configured!");
    }
}
