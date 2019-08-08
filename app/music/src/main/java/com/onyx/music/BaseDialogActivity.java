package com.onyx.music;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

/**
 * @author Kaiguang
 * @Description
 * @Time 2019/8/8
 */
public class BaseDialogActivity extends Activity {
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        adjustLayout();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        adjustLayout();
    }

    private void adjustLayout() {
        int windowWidth = (int) (ScreenUtils.getScreenWidth(getApplicationContext())* ResManager.getFloat(getApplicationContext(), R.dimen.dialog_min_width_major));
        getWindow().setLayout(windowWidth, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
