package com.qingyou.sso.api.constants;

import java.util.Map;

public interface EncodeStringMap {
    Map<String,String> decode(String value);
    String encode(Map<String,String> obj);
}
