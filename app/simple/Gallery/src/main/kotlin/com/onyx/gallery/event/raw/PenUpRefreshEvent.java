package com.onyx.gallery.event.raw;

import android.graphics.RectF;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/7/15 14:38
 *     desc   :
 * </pre>
 */
public class PenUpRefreshEvent {

    public RectF refreshRect;

    public PenUpRefreshEvent(RectF refreshRect) {
        this.refreshRect = refreshRect;
    }
}
