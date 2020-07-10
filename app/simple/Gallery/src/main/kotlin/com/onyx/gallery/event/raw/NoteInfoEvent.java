package com.onyx.gallery.event.raw;

import com.onyx.android.sdk.scribble.data.NoteInfo;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/4/16 14:24
 *     desc   :
 * </pre>
 */
public class NoteInfoEvent {

    public NoteInfo noteInfo;

    public NoteInfoEvent(NoteInfo noteInfo) {
        this.noteInfo = noteInfo;
    }

    public NoteInfo getNoteInfo() {
        return noteInfo;
    }
}
