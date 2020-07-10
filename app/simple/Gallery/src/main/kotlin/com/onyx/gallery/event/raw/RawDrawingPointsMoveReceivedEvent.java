package com.onyx.gallery.event.raw;

import com.onyx.android.sdk.pen.data.TouchPoint;

/**
 * Created by lxm on 2018/3/6.
 */

public class RawDrawingPointsMoveReceivedEvent {

    public TouchPoint touchPoint;

    public RawDrawingPointsMoveReceivedEvent(TouchPoint touchPoint) {
        this.touchPoint = touchPoint;
    }
}
