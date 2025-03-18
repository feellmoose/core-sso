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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Database(String host, int port, String database, String user, String password, Pool pool) {
        public record Pool(int maxSize) {
        }
    }

    public record Security(Jwt jwt, Cookie cookie) {
        public record Jwt(String secret) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Cookie(String name, long expire, long maxAge, long timeout, String path, String domain,
                             boolean secure, boolean httpOnly) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Mail(Message message) {
        public record Message(String from, String subject, String pattern, long expire){}
    }


}
