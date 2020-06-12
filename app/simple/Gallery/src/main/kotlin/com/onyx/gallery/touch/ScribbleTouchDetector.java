package com.onyx.gallery.touch;

import android.view.MotionEvent;

import com.onyx.gallery.event.touch.TouchDownEvent;
import com.onyx.gallery.event.touch.TouchMoveEvent;
import com.onyx.gallery.event.touch.TouchUpEvent;


/**
 * <pre>
 *     author : lxw
 *     time   : 2019/8/29 16:05
 *     desc   :
 * </pre>
 */
public class ScribbleTouchDetector extends BaseTouchDetector {

    public boolean onTouchEvent(MotionEvent event) {
        if (isMultiTouch(event)) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getEventBus().post(new TouchDownEvent(event));
                break;
            case MotionEvent.ACTION_MOVE:
                getEventBus().post(new TouchMoveEvent(event));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getEventBus().post(new TouchUpEvent(event));
                break;
        }
        return true;
    }

}
