package com.onyx.android.sdk.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by solskjaer49 on 2018/9/18 22:02.
 */
public class KeyguardUtils {
    private static final String TAG = KeyguardUtils.class.getSimpleName();
    private static final String FINGERPRINT_SETTING_ACTION = "android.settings.FINGERPRINT_SETTINGS";
    private static Class<?> androidSettingHelperClass = ReflectUtil.classForName("android.onyx.AndroidSettingsHelper");
    private static Method sMethodSaveLockPassword = ReflectUtil.getMethodSafely(androidSettingHelperClass, "saveLockPassword", Context.class, String.class, String.class);
    private static Method sMethodCheckPassword = ReflectUtil.getMethodSafely(androidSettingHelperClass, "checkPassword", Context.class, String.class);
    private static Method sMethodResetPassword =  ReflectUtil.getMethodSafely(androidSettingHelperClass, "resetPassword", Context.class);
    private static Method sMethodResetPasswordWithCurrentCredential = ReflectUtil.getMethodSafely(androidSettingHelperClass, "resetPassword", Context.class, String.class);

    public static void saveLockPassword(Context context, String password, String savedPassword) {
        ReflectUtil.invokeMethodSafely(sMethodSaveLockPassword, null, context, password, savedPassword);
    }

    public static boolean checkPassword(Context context, String password) {
        Boolean result = (Boolean) ReflectUtil.invokeMethodSafely(sMethodCheckPassword, null, context, password);
        return result == null ? false : result;
    }

    public static boolean hasPassword(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && keyguardManager != null) {
            Log.d(TAG, "keyguardManager.isDeviceSecure():" + keyguardManager.isDeviceSecure());
            return keyguardManager.isDeviceSecure();
        }
        return false;
    }

    public static void resetPassword(Context context) {
        ReflectUtil.invokeMethodSafely(sMethodResetPassword, null, context);
    }

    public static void resetPassword(Context context, String savedCredential) {
        ReflectUtil.invokeMethodSafely(sMethodResetPasswordWithCurrentCredential, null, context, savedCredential);
    }

    public static void startFingerprintManagement(Context context) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.O) &&
                DeviceFeatureUtil.hasFingerprint(context)) {
            Intent intent = new Intent(FINGERPRINT_SETTING_ACTION);
            intent.setPackage(BaseConstant.ANDROID_SETTING_PACKAGE_NAME);
            ActivityUtil.startActivitySafely(context, intent);
        } else {
            Log.i(TAG, "Current Device did not support finger print");
        }
    }

    public static boolean isDeviceLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && keyguardManager != null) {
            return keyguardManager.isDeviceLocked();
        }
        return false;
    }

    public static boolean isInKeyguardInputMode(Context context) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }
}
