package com.android.dialer.util;

import com.onyx.android.sdk.utils.ReflectUtil;

import java.lang.reflect.Method;

/**
 * Created by TonyXie on 2020-03-18
 */
public class ClazzUtils {
    public static void enterDiagMode(boolean enterDiagMode) {
        Class<?> eInkHelperClass = ReflectUtil.classForName("android.onyx.UsbModeChooserHelper");
        Method method = ReflectUtil.getMethodSafely(eInkHelperClass, "enterDiagMode", boolean.class);
        ReflectUtil.invokeMethodSafely(method, null, enterDiagMode);
    }
}
