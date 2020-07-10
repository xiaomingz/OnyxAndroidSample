package com.onyx.gallery.event.touch;

import android.view.MotionEvent;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/1/10 10:37
 *     desc   :
 * </pre>
 */
public class TouchUpEvent {

    public MotionEvent motionEvent;

    public TouchUpEvent(MotionEvent motionEvent) {
        this.motionEvent = motionEvent;
    }
}
