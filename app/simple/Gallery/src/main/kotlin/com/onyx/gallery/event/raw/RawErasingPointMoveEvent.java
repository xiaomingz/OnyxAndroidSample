package com.onyx.gallery.event.raw;

import com.onyx.android.sdk.pen.data.TouchPoint;

/**
 * Created by lxm on 2018/3/1.
 */

public class RawErasingPointMoveEvent {

    public TouchPoint touchPoint;

    public RawErasingPointMoveEvent(TouchPoint touchPoint) {
        this.touchPoint = touchPoint;
    }
}
