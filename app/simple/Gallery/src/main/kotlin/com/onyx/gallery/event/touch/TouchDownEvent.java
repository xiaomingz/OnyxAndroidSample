package com.onyx.gallery.event.touch;

import android.view.MotionEvent;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/1/10 10:37
 *     desc   :
 * </pre>
 */
public class TouchDownEvent {

    public MotionEvent motionEvent;

    public TouchDownEvent(MotionEvent motionEvent) {
        this.motionEvent = motionEvent;
    }
}
