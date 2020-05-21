package com.onyx.gallery.event.raw;


import com.onyx.android.sdk.pen.data.TouchPointList;

/**
 * Created by lxm on 2018/2/6.
 */

public class RawDrawingPointsReceivedEvent {

    public TouchPointList touchPointList;

    public RawDrawingPointsReceivedEvent(TouchPointList touchPointList) {
        this.touchPointList = touchPointList;
    }
}
