package com.enderio.base.common.util;

public class ThrowableUtil {

    public static <T extends Exception> T addStackTrace(T throwable) {
        try {
            throw throwable;
        } catch (Exception throwable1) {
            return (T) throwable1;
        }
    }
}
