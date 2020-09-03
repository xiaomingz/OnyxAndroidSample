package com.onyx.gallery.request.shape;

import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;

import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/4/19 16:39
 *     desc   :
 * </pre>
 */
public class StrokeColorChangeRequest extends BaseRequest {

    private volatile int color;

    public StrokeColorChangeRequest(@NotNull EditBundle editBundle) {
        super(editBundle);
    }

    public StrokeColorChangeRequest setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public void execute(@NotNull DrawHandler drawHandler) throws Exception {
        drawHandler.setStrokeColor(color);
        setRenderToScreen(true);
    }
}
