package io.antrex.udemy.vertx_stock_brocker.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DbConfig {
    String host;
    int port;
    String database;
    String user;
    String password;

    @Override
    public String toString() {
        return "DbConfig{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", database='" + database + '\'' +
            ", user='" + user + '\'' +
            ", password=*****" + '\'' +
            '}';
    }
}
