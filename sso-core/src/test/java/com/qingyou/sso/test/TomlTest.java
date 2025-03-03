package com.qingyou.sso.test;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.qingyou.sso.infra.config.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TomlTest {
//    @Test
    public void readConf() throws IOException {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("application.toml")) {
            byte[] bytes = null;
            if (inputStream != null) {
                bytes = inputStream.readAllBytes();
            }
            TomlMapper mapper = new TomlMapper();
            Configuration configuration = mapper.readValue(new String(bytes), Configuration.class);
            Assertions.assertNotNull(configuration);
        }
    }
}
