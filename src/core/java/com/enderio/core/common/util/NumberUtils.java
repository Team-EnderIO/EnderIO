package com.enderio.core.common.util;

public class NumberUtils {

    public static int getInteger(String value) {
        String integerValue = value.replaceAll("[., ]", "");

        try {
            return Integer.parseInt(integerValue);
        } catch(Exception e) {
            return 0;
        }
    }

    public static long getLong(String value) {
        String longValue = value.replaceAll("[., ]", "");
        try {
            return Long.parseLong(longValue);
        } catch(Exception e) {
            return 0;
        }
    }
}
