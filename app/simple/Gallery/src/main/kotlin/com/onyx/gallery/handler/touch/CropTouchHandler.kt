package com.onyx.gallery.handler.touch

import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.ui.RedoCropEvent
import com.onyx.gallery.event.ui.UndoCropEvent

/**
 * Created by Leung on 2020/6/19
 */
class CropTouchHandler(globalEditBundle: GlobalEditBundle) : BaseTouchHandler(globalEditBundle) {

    override fun undo() {
        postEvent(UndoCropEvent())
    }

    override fun redo() {
        postEvent(RedoCropEvent())
    }

    override fun canRawDrawingRenderEnabled(): Boolean = false

}