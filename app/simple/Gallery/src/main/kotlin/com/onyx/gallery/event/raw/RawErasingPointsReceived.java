package com.onyx.gallery.event.raw;

import com.onyx.android.sdk.pen.data.TouchPointList;

/**
 * Created by lxm on 2018/3/2.
 */

public class RawErasingPointsReceived {

    public TouchPointList touchPointList;

    public RawErasingPointsReceived(TouchPointList touchPointList) {
        this.touchPointList = touchPointList;
    }
}
