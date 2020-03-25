package com.android.dialer.util;

import android.content.Context;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

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

    public static int[] getSubId(int id) {
        Class<?> clazz = ReflectUtil.classForName(SubscriptionManager.class.getName());
        Method method = ReflectUtil.getMethodSafely(clazz, "getSubId", int.class);
        return (int[]) ReflectUtil.invokeMethodSafely(method, null, id);
    }

    public static boolean isImsRegistered(TelephonyManager manager, int id) {
        Class<?> clazz = ReflectUtil.classForName(TelephonyManager.class.getName());
        Method method = ReflectUtil.getMethodSafely(clazz, "isImsRegistered", int.class);
        return (boolean) ReflectUtil.invokeMethodSafely(method, manager, id);
    }
}
