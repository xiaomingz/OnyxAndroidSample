package com.onyx.android.sdk.utils;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2019/4/18 16:26
 *     desc   :
 * </pre>
 */
public class NumberUtils {

    public static final int INVALID_ID = 0;
    public static final float FLOAT_ONE = 1.0f;

    public static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return INVALID_ID;
        }
    }

    public static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return INVALID_ID;
        }
    }

    public static float parseFloat(String s) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return INVALID_ID;
        }
    }

    public static double parseDobule(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return INVALID_ID;
        }
    }

}
