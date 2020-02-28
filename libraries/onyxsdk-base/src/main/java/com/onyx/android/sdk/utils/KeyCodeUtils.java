package com.onyx.android.sdk.utils;

import android.view.KeyEvent;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/8/27 18:18
 *     desc   :
 * </pre>
 */
public class KeyCodeUtils {

    public static boolean isDigitalKey(int keyCode) {
        return keyCode >= KeyEvent.KEYCODE_0
                && keyCode <= KeyEvent.KEYCODE_9;
    }

}
