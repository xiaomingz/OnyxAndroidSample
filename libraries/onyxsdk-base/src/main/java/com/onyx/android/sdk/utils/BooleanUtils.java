package com.onyx.android.sdk.utils;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2019/8/1 15:07
 *     desc   :
 * </pre>
 */
public class BooleanUtils {

    public static String toString(final boolean bool, final String trueString, final String falseString) {
        return bool ? trueString : falseString;
    }

    public static String toStringTrueFalse(final boolean bool) {
        return bool ? "True" : "False";
    }

    public static boolean isChanged(boolean bool1, boolean bool2) {
        return bool1 ^ bool2;
    }
}
