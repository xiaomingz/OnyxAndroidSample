package com.onyx.gallery.event.raw;

import com.onyx.android.sdk.pen.data.TouchPoint;

/**
 * Created by lxm on 2018/3/1.
 */

public class EndRawErasingEvent {

    public boolean outLimitRegion;
    public TouchPoint point;

    public EndRawErasingEvent(boolean outLimitRegion, TouchPoint point) {
        this.outLimitRegion = outLimitRegion;
        this.point = point;
    }
}
