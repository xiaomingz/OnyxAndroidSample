package com.onyx.gallery.utils;

import android.content.Context;
import android.widget.Toast;

import com.onyx.android.sdk.utils.ResManager;
import com.onyx.gallery.R;

/**
 * Created by ming on 2017/1/7.
 */

public class ToastUtils {
    public static final int LONG_DELAY = 3500;
    public static final int SHORT_DELAY = 2000;
    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;
    private static int textSize = ResManager.getDimens(R.dimen.kcb_item_title_text_size);

    public static void showToast(Context appContext, int strId) {
        showToast(appContext, appContext.getString(strId));
    }

    public static void showToast(Context appContext, String s) {
        try {
            if (toast == null || toast.getView() == null || toast.getView().findViewById(android.R.id.message) == null) {
                toast = Toast.makeText(appContext.getApplicationContext(), s, Toast.LENGTH_SHORT);
                toast.show();
                oneTime = System.currentTimeMillis();
            } else {
                twoTime = System.currentTimeMillis();
                if (s.equals(oldMsg)) {
                    if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                        toast.show();
                    }
                } else {
                    oldMsg = s;
                    toast.setText(s);
                    toast.show();
                }
            }
            oneTime = twoTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTextSize(int textSize) {
        ToastUtils.textSize = textSize;
    }

    public static void cancel() {
        if (toast == null) {
            return;
        }
        toast.cancel();
    }
}
