package com.onyx.android.sdk.api.device.screensaver;

import android.text.TextUtils;

import java.io.File;

class FileUtils {

    public static boolean fileExist(final String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    public static String getParent(final String path) {
        File file = new File(path);
        return file.getParent();
    }
}
