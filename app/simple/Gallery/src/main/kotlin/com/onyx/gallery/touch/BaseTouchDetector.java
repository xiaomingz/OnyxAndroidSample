package com.onyx.gallery.touch;

import android.view.MotionEvent;

import com.onyx.gallery.bundle.EditBundle;
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

    private EditBundle editBundle;

    public BaseTouchDetector(EditBundle editBundle) {
        this.editBundle = editBundle;
    }

    public abstract boolean onTouchEvent(MotionEvent ev);

    boolean isMultiTouch(MotionEvent event) {
        return event.getPointerCount() > 1;
    }

    public EventBus getEventBus() {
        return getEditBundle().getEventBus();
    }

    public EditBundle getEditBundle() {
        return editBundle;
    }

    public DrawHandler getDrawHandler() {
        return getEditBundle().getDrawHandler();
    }

}
