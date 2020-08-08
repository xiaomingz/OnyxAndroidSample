package com.onyx.android.sdk.kui.utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.utils.RectUtils;

import io.reactivex.annotations.NonNull;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/6/6 12:29
 *     desc   :
 * </pre>
 */
public class ViewUtils {

    public static int[] getLocationInWindow(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return location;
    }

    public static int[] getLocationOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location;
    }

    public static Rect globalVisibleRect(@NonNull View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return rect;
    }

    public static Rect localVisibleRect(@NonNull View view) {
        Rect rect = new Rect();
        view.getLocalVisibleRect(rect);
        return rect;
    }

    public static RectF globalVisibleRectF(@NonNull View view) {
        return RectUtils.toRectF(globalVisibleRect(view));
    }

    public static Rect relativelyParentRect(@NonNull View view) {
        return new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    public static void removeViewFromParent(View view) {
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    public static void safelyAddView(ViewGroup parent, View view) {
        if (view == null) {
            return;
        }
        if (parent == null) {
            return;
        }
        ViewParent oldParent = view.getParent();
        if (oldParent == parent) {
            return;
        }
        removeViewFromParent(view);
        parent.addView(view);
    }

    public static void updateRelativeLayoutViewPosition(View view, int posX, int poxY) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
        lp.leftMargin = posX;
        lp.topMargin = poxY;
        view.setLayoutParams(lp);
    }

    public static void unlockCanvasAndPost(SurfaceView view, Canvas canvas) {
        if (view == null || canvas == null) {
            return;
        }
        view.getHolder().unlockCanvasAndPost(canvas);
    }

    public static boolean isPointInView(View v, float localX, float localY, float slop) {
        return localX >= -slop && localY >= -slop && localX < (v.getWidth() + slop) &&
                localY < (v.getHeight() + slop);
    }

    public static void relayoutView(View view, int width, int height) {
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        view.measure(measuredWidth, measuredHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    public static void setViewVisibleOrGone(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public static void setViewVisibleOrInvisible(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
}
