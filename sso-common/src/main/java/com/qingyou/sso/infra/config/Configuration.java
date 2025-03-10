package com.qingyou.sso.infra.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Configuration(
        Server server,
        Database database,
        Security security,
        Mail mail
) {
    public record Server(String host, int port) {
    }

    public record Database(String host, int port, String database, String user, String password, Connection connection) {
        public record Connection(int poolSize) {
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
