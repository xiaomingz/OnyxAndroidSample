package com.onyx.gallery.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;

import com.onyx.android.sdk.utils.ResManager;

/**
 * @author Kaiguang
 * @Description
 * @Time 2018/9/13
 */
public class DialogUtils {
    private static final float COMMON_DIALOG_WIDTH_THRESHOLD = 0.67f;
    private static final float DEFAULT_DIALOG_SOFT_INPUT_VERTICAL_MARGIN_PERCENT = 0.1f;

    public static void applyCommonSize(Dialog dialog) {
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        dialog.getWindow().setLayout(getDefaultDialogWidth(dialog.getContext()),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static int getDefaultDialogWidth(Context context) {
        float width = Math.min(ResManager.getWindowDefaultWidth(context), ResManager.getWindowDefaultHeight(context));
        return (int) (width * COMMON_DIALOG_WIDTH_THRESHOLD);
    }


}
