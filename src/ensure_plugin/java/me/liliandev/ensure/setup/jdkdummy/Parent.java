package me.liliandev.ensure.setup.jdkdummy;

import java.io.OutputStream;

@SuppressWarnings("all")
public class Parent {
    boolean first;
    static final Object STATIC_OBJ = OutputStream.class;
    volatile Object second;
    private static volatile boolean staticSecond;
    private static volatile boolean staticThird;
}
