package com.qingyou.sso.utils;

import java.security.SecureRandom;
import java.util.Collection;

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

    public static String union(int num) {
        if (num <= 0) return null;
        if (num == 1) return "(?)";
        return "(?" + ",?".repeat(Math.max(0, num - 1)) + ")";
    }

    public static String union(Collection<?> collection) {
        if (collection.isEmpty()) return null;
        if (collection.size() == 1) return "(?)";
        return "(?" + ",?".repeat(Math.max(0, collection.size() - 1)) + ")";
    }

    public static String unionRepeat(int num, int repeat) {
        String each = union(num);
        if (repeat <= 0) return null;
        if (repeat == 1) return each;
        return each + ("," + each).repeat(Math.max(0, num - 1));
    }

    public static String unionRepeat(int fields, Collection<?> collection) {
        String each = union(fields);
        if (collection.isEmpty()) return null;
        if (collection.size() == 1) return each;
        return each + ("," + each).repeat(Math.max(0, collection.size() - 1));
    }

}
