package com.onyx.gallery.event.raw;

import com.onyx.android.sdk.pen.data.TouchPoint;

/**
 * Created by lxm on 2018/3/1.
 */

public class BeginRawErasingEvent {

    public boolean shortcutErasing;
    public TouchPoint point;

    public BeginRawErasingEvent(boolean shortcutErasing, TouchPoint point) {
        this.shortcutErasing = shortcutErasing;
        this.point = point;
    }
}
