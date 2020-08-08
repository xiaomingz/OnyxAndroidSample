package com.onyx.android.sdk.kui.dialog;

import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;


import com.onyx.android.sdk.kui.utils.ViewUtils;
import com.onyx.android.sdk.utils.ObjectHolder;
import com.onyx.android.sdk.utils.ResManager;

import java.lang.ref.WeakReference;


/**
 * Create at 2018/5/18 by anypwx
 **/
public class BasePopWindow extends PopupWindow {
    private int[] location;
    private Point p;
    private static com.onyx.android.sdk.utils.ObjectHolder<BasePopWindow> dialogHolder = new ObjectHolder<>();
    private final WeakReference<BasePopWindow> basePopWindowWeakReference;
    private boolean showAsLeft = false;
    private boolean showAsUp = false;

    public BasePopWindow(Context context) {
        super(context);
        basePopWindowWeakReference = new WeakReference<>(this);
        dialogHolder.add(basePopWindowWeakReference);
    }

    public void dismissPopView() {
        dismiss();
    }

    public void showDynamicPositionPopView(final View parent, Point p) {
        this.p = p;
        showDynamicPositionPopView(parent);
    }

    public void showDynamicPositionPopView(final View parent) {
        getLocation(parent);
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Point point = getShowLocation(parent, getContentView().getMeasuredWidth(), getContentView().getMeasuredHeight());
        if (point == null) {
            return;
        }
        showAtLocation(parent, Gravity.NO_GRAVITY, point.x, point.y);
    }

    private Point getShowLocation(View anchor, int popWidth, int popHeight) {
        if (anchor == null) {
            return null;
        }
        int[] location = getLocation(anchor);
        int touchX = location[0];
        int touchY = location[1];
        Context context = anchor.getContext();
        int screenHeight = ResManager.getWindowDefaultHeight(context);
        int screenWidth = ResManager.getWindowDefaultWidth(context);
        //init offsetX offsetY position
        int offsetX = touchX + anchor.getWidth() / 2;
        int offsetY = touchY + anchor.getHeight() / 2;
        if (screenWidth - touchX - anchor.getWidth() / 2 < popWidth) {
            // rightmost
            offsetX = screenWidth - popWidth - ((int) (anchor.getWidth() / 1.5));
        }
        if (screenHeight - touchY - anchor.getHeight() / 2 < popHeight) {
            // bottommost
            offsetY = screenHeight - popHeight - anchor.getHeight();
        }
        if (showAsLeft) {
            offsetX = offsetX - popWidth;
        }
        if (showAsUp) {
            offsetY = offsetY - popHeight;
        }
        if (p != null) {
            offsetX += p.x;
            offsetY += p.y;
        }
        return new Point(offsetX, offsetY);
    }

    private int[] getLocation(View anchorView) {
        if (location == null) {
            location = new int[2];
            location = ViewUtils.getLocationInWindow(anchorView);
        }
        return location;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dialogHolder.remove(basePopWindowWeakReference);
    }

    public static void dismissAll() {
        for (WeakReference<BasePopWindow> basePopWindowWeakReference : dialogHolder.getCopyOfObjectList()) {
            BasePopWindow basePopWindow = basePopWindowWeakReference.get();
            if (basePopWindow != null) {
                basePopWindow.dismiss();
            }
        }
        dialogHolder.clear();
    }

    public BasePopWindow setShowAsLeft(boolean showAsLeft) {
        this.showAsLeft = showAsLeft;
        return this;
    }

    public BasePopWindow setShowAsUp(boolean showAsUp) {
        this.showAsUp = showAsUp;
        return this;
    }
}
