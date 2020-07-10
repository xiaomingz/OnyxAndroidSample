package com.onyx.gallery.touch;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.android.sdk.utils.ResManager;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/12/13 18:52
 *     desc   :
 * </pre>
 */
public class ScribbleTouchDistributor {

    private final ScaleGestureDetector scaleGestureDetector = new ScribbleScaleGestureDetector(ResManager.getAppContext(), new ZoomGestureListener());
    private final MoveGestureDetector moveGestureDetector = new MoveGestureDetector();
    private final ScribbleTouchDetector scribbleTouchDetector = new ScribbleTouchDetector();

    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        moveGestureDetector.onTouchEvent(event);
        scribbleTouchDetector.onTouchEvent(event);
        return true;
    }
}
