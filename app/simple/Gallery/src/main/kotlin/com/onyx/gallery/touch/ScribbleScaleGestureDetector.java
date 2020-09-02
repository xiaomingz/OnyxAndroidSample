package com.onyx.gallery.touch;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.android.sdk.utils.TouchUtils;
import com.onyx.gallery.bundle.EditBundle;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/11/15 15:48
 *     desc   :
 * </pre>
 */
public class ScribbleScaleGestureDetector extends ScaleGestureDetector {

    private final EditBundle editBundle;

    public ScribbleScaleGestureDetector(Context context, EditBundle editBundle, OnScaleGestureListener listener) {
        super(context, listener);
        this.editBundle = editBundle;
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

    private EditBundle getGlobalEditBundle() {
        return editBundle;
    }

}

