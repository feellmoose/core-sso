package com.qingyou.sso.api.constants;

import java.util.Map;

public enum DataType implements EncodeStringMap{
    Json(){
        @Override
        public Map<String, String> decode(String value) {
            return io.vertx.core.json.Json.decodeValue(value, Map.class);
        }

        @Override
        public String encode(Map<String, String> obj) {
            return Json.encode(obj);
        }
    }

}
