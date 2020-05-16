package com.onyx.android.appcompat.common.utils;

import android.content.Context;
import android.content.Intent;

/**
 * <pre>
 *     author : suicheng
 *     time   : 2020/5/16 12:19
 *     desc   :
 * </pre>
 */
public class BroadcastHelper {

    public static final String ONYX_WAKELOCK_TAG = "onyx_workLowPower";

    public static void sendWakeupIntent(Context context) {
        context.sendBroadcast(new Intent("com.onyx.action.PM_WAKE_UP"));
    }
}
