package com.onyx.android.sdk.utils;

import android.os.Looper;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/5/7 11:27
 *     desc   :
 * </pre>
 */
public class Checks {

    public static void ensureInMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("have to called on main thread");
        }
    }

    public static void ensureInWorkingThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("have to called on working thread");
        }
    }
}
