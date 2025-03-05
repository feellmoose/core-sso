package com.qingyou.sso.infra.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.vertx.core.json.JsonObject;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Configuration(
        Server server,
        Application application,
        Database database,
        Security security,
        Mail mail
) {
    public record Server(String host, int port) {
    }

    public record Application(String name, String version) {
    }

    public record Database(String url, String user, String password, Connection connection, Hibernate hibernate) {
        public record Connection(int poolSize) {
        }

        public record Hibernate(boolean showSql, String action) {
        }
    }

    public record Security(Jwt jwt, Cookie cookie) {
        public record Jwt(String secret) {
        }

        public record Cookie(String name, long expire, long maxAge, long timeout, String path, String domain,
                             boolean secure, boolean httpOnly) {
        }
    }

    public record Mail(String host, int port, String username, String password, String from, String subject, String pattern, long expire) {

    }


}
