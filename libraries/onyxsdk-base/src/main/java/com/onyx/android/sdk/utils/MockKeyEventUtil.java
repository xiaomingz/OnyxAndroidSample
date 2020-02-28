package com.onyx.android.sdk.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by solskjaer49 on 2018-12-21 19:17.
 */
public class MockKeyEventUtil {
    private static final String SEND_KEY_ACTION = "onyx.android.intent.send.key.event";
    private static final String ARGS_KEY_CODE = "key_code";

    public static void sendMockKeyEvent(Context context, int keycode) {
        Intent intent = new Intent(SEND_KEY_ACTION);
        intent.putExtra(ARGS_KEY_CODE, keycode);
        context.sendBroadcast(intent);
    }
}
