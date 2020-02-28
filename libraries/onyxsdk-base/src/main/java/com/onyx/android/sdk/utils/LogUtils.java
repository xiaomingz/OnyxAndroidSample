package com.onyx.android.sdk.utils;

import android.os.Build;

public class LogUtils {

    public static DefaultLogEntry getLogger() {
        if (Build.VERSION.SDK_INT >= 28) {
            return BuildVersionOver28LogEntry.createLog();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return BuildVersionBelow23LogEntry.createLog();
        } else {
            return DefaultLogEntry.createLog();
        }
    }
}
