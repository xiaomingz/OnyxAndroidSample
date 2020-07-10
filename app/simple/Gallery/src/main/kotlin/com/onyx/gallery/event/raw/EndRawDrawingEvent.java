package com.onyx.gallery.event.raw;

import com.onyx.android.sdk.pen.data.TouchPoint;

/**
 * Created by lxm on 2018/3/6.
 */

public class EndRawDrawingEvent {

    public boolean outLimitRegion;
    public TouchPoint point;

    public EndRawDrawingEvent(boolean outLimitRegion, TouchPoint point) {
        this.outLimitRegion = outLimitRegion;
        this.point = point;
    }
}
