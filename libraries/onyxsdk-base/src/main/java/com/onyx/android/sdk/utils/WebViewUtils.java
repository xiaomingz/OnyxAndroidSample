package com.onyx.android.sdk.utils;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/5/31 15:45
 *     desc   :
 * </pre>
 */
public class WebViewUtils {

    private static final String TAG = "WebViewUtils";

    public static void hookWebView() {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt == 28) {
            hookWebViewForAndroidP();
        }
    }

    private static void hookWebViewForAndroidP() {
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                Log.i(TAG,"sProviderInstance isn't null");
                return;
            }

            Method getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            getProviderClassMethod.setAccessible(true);
            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
            delegateConstructor.setAccessible(true);

            Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
            chromiumMethodName.setAccessible(true);
            String chromiumMethodNameStr = (String)chromiumMethodName.get(null);
            if (chromiumMethodNameStr == null) {
                chromiumMethodNameStr = "create";
            }
            Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
            if (staticFactory!=null){
                sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
            }

            if (sProviderInstance != null){
                field.set("sProviderInstance", sProviderInstance);
                Log.i(TAG,"Hook success!");
            } else {
                Log.i(TAG,"Hook failed!");
            }
        } catch (Throwable e) {
            Log.w(TAG,e);
        }
    }

}
