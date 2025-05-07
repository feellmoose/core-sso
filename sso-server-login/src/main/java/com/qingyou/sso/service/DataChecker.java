package com.qingyou.sso.service;

import java.util.Map;

@FunctionalInterface
public interface DataChecker {
    boolean check(Map<String,String> metadata);
}
