package me.liliandev.ensure.setup;

import me.liliandev.ensure.EnsurePlugin;
import me.liliandev.ensure.setup.jdkdummy.Parent;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//Stolen from lombok tysm
public class EnsureSetup {

    private static final String[] EXPECTED_PACKAGES = new String[] {
            "com.sun.tools.javac.api",
            "com.sun.tools.javac.util",
            "com.sun.tools.javac.tree",
            "com.sun.tools.javac.code",
            "com.sun.tools.javac.processing"
    };

    public static void setup() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Module module = EnsurePlugin.class.getModule();
        Module compilerModule = ModuleLayer.boot().findModule("jdk.compiler").orElseThrow(IllegalStateException::new);

        Method m = Module.class.getDeclaredMethod("implAddOpens", String.class, Module.class);
        Unsafe unsafe = getUnsafe();
        long firstFieldOffset = getFirstFieldOffset(unsafe);
        unsafe.putBooleanVolatile(m, firstFieldOffset, true);
        for (String pckage : EXPECTED_PACKAGES) {
            m.invoke(compilerModule, pckage, module);
        }
    }


    private static long getFirstFieldOffset(Unsafe unsafe) {
        try {
            return unsafe.objectFieldOffset(Parent.class.getDeclaredField("first"));
        } catch (NoSuchFieldException | SecurityException e) {
            // can't happen.
            throw new RuntimeException(e);
        }
    }

    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
