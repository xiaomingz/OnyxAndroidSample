package com.onyx.android.sdk.utils;

import android.os.Build;
import android.system.ErrnoException;
import android.system.Os;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

public class OsUtil {

    public static void setenv(String name, String value, boolean overwrite) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Os.setenv(name, value, overwrite);
            return;
        }

        Class < ? > libcore = ReflectUtil.classForName("libcore.io.Libcore");
        if (libcore == null) {
            Debug.w(OsUtil.class, "Can not find libcore.io.Libcore");
            return;
        }

        final Object os = ReflectUtil.getStaticFieldSafely(libcore, "os");
        if (os == null) {
            Debug.w(OsUtil.class, "Can not find Libcore.os");
            return;
        }

        Method method = ReflectUtil.getMethodSafely(os.getClass(), "setenv", String.class, String.class, boolean.class);
        if (method == null) {
            Debug.w(OsUtil.class, "Can not find Os.setenv()");
            return;
        }

        ReflectUtil.invokeMethodSafely(method, os, name, value, overwrite);
    }

    public static String getenv(String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Os.getenv(name);
        }

        Class < ? > libcore = ReflectUtil.classForName("libcore.io.Libcore");
        if (libcore == null) {
            Debug.w(OsUtil.class, "Can not find libcore.io.Libcore");
            return null;
        }

        final Object os = ReflectUtil.getStaticFieldSafely(libcore, "os");
        if (os == null) {
            Debug.w(OsUtil.class, "Can not find Libcore.os");
            return null;
        }

        Method method = ReflectUtil.getMethodSafely(os.getClass(), "getenv", String.class);
        if (method == null) {
            Debug.w(OsUtil.class, "Can not find Os.getenv()");
            return null;
        }

        AtomicReference<Object> result = new AtomicReference<>();
        if (!ReflectUtil.invokeMethodSafely(result, method, os, name)) {
            return null;
        }
        if (!(result.get() instanceof String)) {
            return null;
        }

        return (String)result.get();
    }
}
