package com.onyx.gallery.event.raw;

/**
 * <pre>
 *     author : lxw
 *     time   : 2020/2/24 11:47
 *     desc   :
 * </pre>
 */
public class NoteRenderDialogEvent {

    public boolean show;

    public NoteRenderDialogEvent(boolean show) {
        this.show = show;
    }

    public static NoteRenderDialogEvent show() {
        return new NoteRenderDialogEvent(true);
    }

    public static NoteRenderDialogEvent hide() {
        return new NoteRenderDialogEvent(false);
    }
}
