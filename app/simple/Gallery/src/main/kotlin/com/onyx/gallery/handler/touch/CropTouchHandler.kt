package com.onyx.gallery.handler.touch

import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.ui.RedoCropEvent
import com.onyx.gallery.event.ui.UndoCropEvent

/**
 * Created by Leung on 2020/6/19
 */
class CropTouchHandler(editBundle: EditBundle) : BaseTouchHandler(editBundle) {

    override fun undo() {
        postEvent(UndoCropEvent())
    }

    override fun redo() {
        postEvent(RedoCropEvent())
    }

}