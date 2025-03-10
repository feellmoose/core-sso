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
       return union(1 , num);
    }

    public static String union(int from, int num) {
        if (from < 1 || num < 1) return "";
        if (num == 1) return "($" + from + ")";
        StringBuilder sb = new StringBuilder("($1");
        for (int i = from; i < from + num; i++) {
            sb.append(",$").append(i);
        }
        return sb.append(")").toString();
    }

    public static String union(Collection<?> collection) {
        if (collection.isEmpty()) return "";
        return union(1, collection.size());
    }

    public static String union(int from, Collection<?> collection) {
        if (collection.isEmpty()) return "";
        return union(from, collection.size());
    }

    public static String unionRepeat(int num, int repeat) {
        return unionRepeat(1, num, repeat);
    }

    public static String unionRepeat(int from, int num, int repeat) {
        if (repeat <= 0) return null;
        if (repeat == 1) return union(from, num);
        StringBuilder sb = new StringBuilder(union(from, num));
        for (int i = 1; i < repeat; i++) {
            sb.append(",").append(union(i, repeat));
        }
        return sb.toString();
    }

    public static String unionRepeat(int from, int fields, Collection<?> collection) {
        return unionRepeat(from,fields, collection.size());
    }

    public static String unionRepeat(int fields, Collection<?> collection) {
        return unionRepeat(fields, collection.size());
    }

}
