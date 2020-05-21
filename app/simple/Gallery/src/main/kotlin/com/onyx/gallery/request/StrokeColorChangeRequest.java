package com.onyx.gallery.request;

import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.helpers.NoteManager;

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

    public StrokeColorChangeRequest setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public void execute(@NotNull NoteManager noteManager) throws Exception {
        getNoteManager().setStrokeColor(color);
    }
}
