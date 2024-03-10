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

    public static String formatWithPrefix(int number) {
        if (number < 100_000) {
            return Long.toString(number);
        } else if (number < 1_000_000) {
            return String.format("%.1fk", number / 1_000.0);
        } else if (number < 1_000_000_000) {
            return String.format("%.3fM", number / 1_000_000.0);
        } else {
            return String.format("%.3fB", number / 1_000_000_000.0);
        }
    }
}
