package com.onyx.gallery.utils;

import android.content.res.Resources;

/**
 * @author Kaiguang
 * @Description
 * @Time 2018/8/31
 */
public class BarUtils {
    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    public static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}
