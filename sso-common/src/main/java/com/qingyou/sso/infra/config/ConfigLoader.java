package com.qingyou.sso.infra.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.qingyou.sso.infra.Constants;
import com.qingyou.sso.utils.ResourceUtils;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;


@AllArgsConstructor
public class ConfigLoader {
    private final Vertx vertx;
    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);
    private final TomlMapper mapper = new TomlMapper();

    public Future<Configuration> load() {
        Future<LinkedHashMap<String, Object>> baseConfFuture = loadBaseConf();
        //load sys
        String sys = System.getProperty("profiles.active");
        if (sys != null && !sys.isEmpty()) return loadByEnvName(sys, baseConfFuture);
        //load env
        String env = System.getenv("profiles.active");
        if (env != null && !env.isEmpty()) return loadByEnvName(env, baseConfFuture);
        //load from base conf
        return loadByBaseConf(baseConfFuture);
    }

    private static void mergeMaps(LinkedHashMap<String, Object> baseConf, LinkedHashMap<String, Object> envConf) {
        baseConf.forEach((key, value) -> {
            if (value instanceof LinkedHashMap && envConf.get(key) instanceof LinkedHashMap) {
                mergeMaps((LinkedHashMap<String, Object>) value, (LinkedHashMap<String, Object>) envConf.get(key));
            } else if (!envConf.containsKey(key)) {
                envConf.put(key, value);
            }
        });
    }

    //load base and the env at same time
    private Future<Configuration> loadByEnvName(String env, Future<LinkedHashMap<String, Object>> baseConfFuture) {
        Future<LinkedHashMap<String, Object>> envConfFuture = loadEnvConf(env);
        return Future.all(baseConfFuture, envConfFuture).map(res -> {
            LinkedHashMap<String, Object> baseConf = baseConfFuture.result();
            LinkedHashMap<String, Object> envConf = envConfFuture.result();
            mergeMaps(baseConf, envConf);
            return mapper.convertValue(envConf, Configuration.class);
        });
    }

    //load base first then try to load follow the "active" property
    private Future<Configuration> loadByBaseConf(Future<LinkedHashMap<String, Object>> baseConfFuture) {
        Future<LinkedHashMap<String, Object>> envConfFuture = baseConfFuture.flatMap(configuration -> {
            String baseConfEnv = (String) configuration.get("active");
            if (baseConfEnv == null || baseConfEnv.isBlank()) return Future.succeededFuture(configuration);
            return loadEnvConf(baseConfEnv);
        });
        return envConfFuture.map(configuration -> {
            LinkedHashMap<String, Object> baseConf = baseConfFuture.result();
            LinkedHashMap<String, Object> envConf = envConfFuture.result();
            mergeMaps(baseConf, envConf);
            return mapper.convertValue(envConf, Configuration.class);
        });
    }

    private Future<LinkedHashMap<String, Object>> loadEnvConf(String env) {
        String path = Constants.classPath();
        if (path == null) return loadFromResource(env, "application-" + env + ".toml");
        else return loadFromPath(env,path, "application-" + env + ".toml");
    }

    private Future<LinkedHashMap<String, Object>> loadBaseConf() {
        String path = Constants.classPath();
        if (path == null) return loadFromResource("base", "application.toml");
        else return loadFromPath("base", path, "application.toml");
    }

    @SuppressWarnings("unchecked")
    private Future<LinkedHashMap<String, Object>> loadFromPath(String env, String path, String name) {
        return vertx.fileSystem().readFile(path + name).map(file -> {
            try {
                log.info("Loading Env:{} Conf from {}{}", env, path, name);
                return (LinkedHashMap<String, Object>) mapper.readValue(new String(file.getBytes()), LinkedHashMap.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private Future<LinkedHashMap<String, Object>> loadFromResource(String env, String name) {
        return vertx.executeBlocking(() -> {
            try(InputStream stream = ResourceUtils.load(name)) {
                log.info("Loading Env:{} Conf from resource {}", env, name);
                return (LinkedHashMap<String, Object>) mapper.readValue(new String(stream.readAllBytes()), LinkedHashMap.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
