package com.onyx.gallery.event.raw;

import com.onyx.android.sdk.pen.data.TouchPoint;

/**
 * Created by lxm on 2018/2/6.
 */

public class BeginRawDrawEvent {

    public boolean shortcutDrawing;
    public TouchPoint point;

    public BeginRawDrawEvent(boolean shortcutDrawing, TouchPoint point) {
        this.shortcutDrawing = shortcutDrawing;
        this.point = point;
    }
}
