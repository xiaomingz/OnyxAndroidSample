package com.onyx.gallery.touch;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.android.sdk.utils.TouchUtils;
import com.onyx.gallery.bundle.GlobalEditBundle;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/11/15 15:48
 *     desc   :
 * </pre>
 */
public class ScribbleScaleGestureDetector extends ScaleGestureDetector {

    public ScribbleScaleGestureDetector(Context context, OnScaleGestureListener listener) {
        super(context, listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (TouchUtils.isPenTouchType(event)) {
            return false;
        }
        if (!getGlobalEditBundle().getCanFingerTouch()) {
            return false;
        }
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL) {
        }
        return super.onTouchEvent(event);
    }

    private GlobalEditBundle getGlobalEditBundle() {
        return GlobalEditBundle.Companion.getInstance();
    }

}

