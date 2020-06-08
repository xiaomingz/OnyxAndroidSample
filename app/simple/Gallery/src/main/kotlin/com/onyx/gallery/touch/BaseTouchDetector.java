package com.onyx.gallery.touch;

import android.view.MotionEvent;

import com.onyx.gallery.bundle.GlobalEditBundle;
import com.onyx.gallery.handler.DrawHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/8/29 16:14
 *     desc   :
 * </pre>
 */
public abstract class BaseTouchDetector {

    public abstract boolean onTouchEvent(MotionEvent ev);

    boolean isMultiTouch(MotionEvent event) {
        return event.getPointerCount() > 1;
    }

    public EventBus getEventBus() {
        return getEditBundle().getEventBus();
    }

    public GlobalEditBundle getEditBundle() {
        return GlobalEditBundle.Companion.getInstance();
    }

    public DrawHandler getDrawHandler() {
        return getEditBundle().getDrawHandler();
    }

}
