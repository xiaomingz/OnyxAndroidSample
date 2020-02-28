package com.onyx.android.sdk.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.WindowManager;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by suicheng on 2018/4/20.
 */
public class ResManager {
    private static Context appContext;
    private static WeakReference<Context> uiContextReference;

    public static void init(Context context) {
        ResManager.appContext = context;
    }

    public static void installUiContext(Context uiContext) {
        ResManager.uiContextReference = new WeakReference<>(uiContext);
    }

    public static void uninstallUiContext() {
        ResManager.uiContextReference = null;
    }

    private static Context getContext() {
        if (uiContextReference != null && uiContextReference.get() != null) {
            return uiContextReference.get();
        }
        return appContext;
    }

    public static String getString(int resId) {
        return getContext().getResources().getString(resId);
    }

    public static String getString(String resName) {
        return getString(getIdentifier(resName, "string"));
    }

    @Nullable
    public static String getStringSafely(String resName) {
        int id = getIdentifier(resName, "string");
        if (id <= 0) {
            return null;
        }
        return getString(id);
    }

    public static String getString(int resId, Object... formatArgs) {
        return getContext().getResources().getString(resId, formatArgs);
    }

    public static Integer getInteger(int resId) {
        return getContext().getResources().getInteger(resId);
    }

    public static float getFloat(int resId) {
        TypedValue outValue = new TypedValue();
        getContext().getResources().getValue(resId, outValue, true);
        return outValue.getFloat();
    }

    public static String[] getStringArray(int resId) {
        return getContext().getResources().getStringArray(resId);
    }

    public static TypedArray getTypedArray(int resId) {
        return getContext().getResources().obtainTypedArray(resId);
    }

    public static int getDimens(int resId) {
        return getContext().getResources().getDimensionPixelSize(resId);
    }

    public static int getColor(int res) {
        return getContext().getResources().getColor(res);
    }

    public static int[] getIntArray(int resId) {
        return getContext().getResources().getIntArray(resId);
    }

    public static Integer [] getIntegerArray(int resId) {
        int [] array = getIntArray(resId);
        Integer[] objectArray = new Integer[array.length];
        for(int ctr = 0; ctr < array.length; ctr++) {
            objectArray[ctr] = Integer.valueOf(array[ctr]); // returns Integer value
        }
        return objectArray;
    }

    public static Float[] getFloatArray(int resId) {
        String []  array = getContext().getResources().getStringArray(resId);
        Float[] objectArray = new Float[array.length];
        for(int ctr = 0; ctr < array.length; ctr++) {
            objectArray[ctr] = MathUtils.parseFloat(array[ctr]);
        }
        return objectArray;
    }

    static public String getUriOfRawName(String rawName) {
        return "file:///android_res/raw/" + rawName;
    }

    static public String getUriOfAssets(String htmlName) {
        return "file:///android_asset/" + htmlName;
    }

    public static float getDimension(int resId) {
        return getContext().getResources().getDimension(resId);
    }

    public static int getDimensionPixelSize(int resId) {
        return getContext().getResources().getDimensionPixelSize(resId);
    }

    public static float getFloatValue(int floatDimen) {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(floatDimen, typedValue, true);
        return typedValue.getFloat();
    }

    public static Drawable getDrawable(String name) {
        int resId = getDrawableResId(name);
        if (resId <= 0) {
            return null;
        }
        return getDrawable(resId);
    }

    public static Drawable getDrawable(int resId) {
        return getContext().getResources().getDrawable(resId);
    }

    public static int getDrawableResId(String resName) {
        return getIdentifier(resName, "drawable");
    }

    public static float getScreenWidth() {
        return appContext.getResources().getDisplayMetrics().widthPixels;
    }

    public static float getScreenHeight() {
        return appContext.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     *  this will get the real screen height that not affected by navigation bar
     * @return real screen height
     */
    @TargetApi(17)
    public static int getRealScreenHeight() {
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        Point outSize = new Point(0, 0);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getRealSize(outSize);
        }
        return outSize.y;
    }

    public static int getWindowDefaultWidth(Context context) {
        return getWindowDefaultSize(context).x;
    }

    public static int getWindowDefaultHeight(Context context) {
        return getRealScreenHeight();
    }

    public static Point getWindowDefaultSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point outSize = new Point(0, 0);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getSize(outSize);
        }
        return outSize;
    }

    public static float getAspectRatio() {
        return getScreenWidth()/getScreenHeight();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static int getDPI() {
        return getContext().getResources().getDisplayMetrics().densityDpi;
    }

    public static int getIdentifier(String name, String type) {
        return getContext().getResources().getIdentifier(name, type, getContext().getPackageName());
    }

    public static InputStream openRawResource(String name) {
        return getContext().getResources().openRawResource(getIdentifier(name, "raw"));
    }

    public static InputStream openRawResource(int id) {
        return getContext().getResources().openRawResource(id);
    }

    public static String getQuantityString(int resId, int quantity) {
        return getContext().getResources().getQuantityString(resId, quantity);
    }

    public static String getQuantityString(int resId, int quantity, Object... formatArgs) {
        return getContext().getResources().getQuantityString(resId, quantity, formatArgs);
    }

    public static boolean getBoolean(int id) {
        return getContext().getResources().getBoolean(id);
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    @Nullable
    public static String getResourceEntryName(int id) {
        try {
            if (id <= 0) {
                return null;
            }
            return getResources().getResourceEntryName(id);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static int getDensityDpi() {
        return getAppContext().getResources().getDisplayMetrics().densityDpi;
    }

    @Nullable
    public static Drawable getDrawableForDensity(String packageName, int resourceId, int density) {
        if (resourceId <= 0) {
            return null;
        }
        try {
            PackageInfo packageInfo = ApplicationUtil.getPackageInfoFromPackageName(getContext(), packageName);
            Resources resources = getContext().getPackageManager().getResourcesForApplication(packageInfo.applicationInfo);
            return resources.getDrawableForDensity(resourceId, density);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Try to guess if the color on top of the given {@code colorOnBottomInt}
     * should be light or dark. Returns true if top color should be light
     */
    public static boolean shouldColorOnTopBeLight(@ColorInt final int colorOnBottomInt) {
        return 186 > (((0.299 * Color.red(colorOnBottomInt))
                + ((0.587 * Color.green(colorOnBottomInt))
                + (0.114 * Color.blue(colorOnBottomInt)))));
    }
}
