package com.onyx.gallery.touch;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.android.sdk.utils.ResManager;
import com.onyx.gallery.bundle.EditBundle;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/12/13 18:52
 *     desc   :
 * </pre>
 */
public class ScribbleTouchDistributor {

    private final EditBundle editBundle;
    private final ScaleGestureDetector scaleGestureDetector;
    private final MoveGestureDetector moveGestureDetector;
    private final ScribbleTouchDetector scribbleTouchDetector;

    public ScribbleTouchDistributor(EditBundle editBundle) {
        this.editBundle = editBundle;
        scaleGestureDetector = new ScribbleScaleGestureDetector(ResManager.getAppContext(), editBundle, new ZoomGestureListener(editBundle));
        moveGestureDetector = new MoveGestureDetector(editBundle);
        scribbleTouchDetector = new ScribbleTouchDetector(editBundle);
    }

    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        moveGestureDetector.onTouchEvent(event);
        scribbleTouchDetector.onTouchEvent(event);
        return true;
    }
}
