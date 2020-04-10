package com.onyx.calculator;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Created by ming on 16/9/14.
 */
public class DisableScrollLinearManager extends LinearLayoutManager {

    private boolean canScroll = false;

    public DisableScrollLinearManager(Context context) {
        super(context);
    }

    public DisableScrollLinearManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public DisableScrollLinearManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollEnable(boolean enable) {
        this.canScroll = enable;
    }

    @Override
    public boolean canScrollVertically() {
        return canScroll;
    }

    @Override
    public boolean canScrollHorizontally() {
        return canScroll;
    }
}
