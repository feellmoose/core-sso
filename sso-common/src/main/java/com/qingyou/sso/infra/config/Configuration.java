package com.qingyou.sso.infra.config;

import java.util.List;

public record Configuration(
        String active,
        Server server,
        Application application,
        Database database,
        Redis redis,
        Security security
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

    public record Redis(Cache cache, Mode mode, String url, String password, Sentinel sentinel, Cluster cluster) {
        public enum Mode{
            Standalone,
            Sentinel,
            Cluster,
            Replication
        }
        public record Sentinel(String master, List<String> nodes){
        }
        public record Cluster(List<String> nodes){
        }
        public record Cache(long ttl) {
        }
    }

    public record Security(Jwt jwt, Cookie cookie, Password password, ThirdParty thirdParty) {
        public record Jwt(String secret) {
        }

        public record Cookie(String name, long expire, long maxAge, long timeout, String path, String domain,
                             boolean secure, boolean httpOnly) {
        }

        public record Password() {

        }

        public record ThirdParty() {

        }

    }


}
