package com.onyx.gallery.event.raw;

import com.onyx.android.sdk.scribble.data.SelectionBundle;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/7/16 17:30
 *     desc   :
 * </pre>
 */
public class SelectionBundleEvent {

    public SelectionBundle bundle;

    public SelectionBundleEvent(SelectionBundle bundle) {
        this.bundle = bundle;
    }
}
