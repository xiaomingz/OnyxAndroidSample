package com.onyx.gallery.common;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.utils.ResManager;
import com.onyx.gallery.R;
import com.onyx.gallery.utils.DialogUtils;

/**
 * Created by lxm on 2018/1/30.
 */

public class BaseNoteDialog extends Dialog {

    public BaseNoteDialog(@NonNull Context context) {
        super(context);
    }

    public BaseNoteDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void show(int x, int y) {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawableResource(R.color.transparent_color);
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = x;
        lp.y = y;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        show();
    }

    @Override
    public void show() {
        super.show();
        DialogUtils.applyCommonSize(this);
    }

    public void fullScreen(Context context) {
        if (getWindow() == null) {
            return;
        }
        getWindow().setLayout(ResManager.getWindowDefaultWidth(context),
                ResManager.getWindowDefaultHeight(context));
    }

}
