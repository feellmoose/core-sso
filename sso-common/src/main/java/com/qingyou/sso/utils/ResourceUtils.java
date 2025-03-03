package com.qingyou.sso.utils;

import java.io.InputStream;

public class ResourceUtils {
    public static InputStream load(String file){
        return ResourceUtils.class.getClassLoader().getResourceAsStream(file);
    }
}
