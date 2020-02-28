package com.onyx.android.sdk.utils;

import android.view.MotionEvent;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/11/15 15:46
 *     desc   :
 * </pre>
 */
public class TouchUtils {

    public static boolean isPenTouchType(MotionEvent event) {
        int toolType = event.getToolType(event.getActionIndex());
        return toolType == MotionEvent.TOOL_TYPE_STYLUS
                || toolType == MotionEvent.TOOL_TYPE_ERASER;
    }

}
