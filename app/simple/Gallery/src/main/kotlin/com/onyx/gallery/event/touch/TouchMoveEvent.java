package com.onyx.gallery.event.touch;

import android.view.MotionEvent;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/1/10 10:37
 *     desc   :
 * </pre>
 */
public class TouchMoveEvent {

    public MotionEvent motionEvent;

    public TouchMoveEvent(MotionEvent motionEvent) {
        this.motionEvent = motionEvent;
    }
}
