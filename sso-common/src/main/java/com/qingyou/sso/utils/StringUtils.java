package com.qingyou.sso.utils;

import java.security.SecureRandom;

public final class StringUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String random(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String randomNum(int length) {
        int num = RANDOM.nextInt((int) Math.pow(10,length-1), (int) Math.pow(10,length)-1);
        return String.valueOf(num);
    }

}
