package com.onyx.gallery.request.crop

import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/16 15:28
 **/
class UndoCropRequest(editBundle: EditBundle) : BaseRequest(editBundle) {
    private var hasUndo = false
    private var undoAngle: Float = 0f
    override fun execute(drawHandler: DrawHandler) {
        drawHandler.undoCrop()?.run {
            drawHandler.restoreCropSnapshot(this)
            undoAngle = rotateAngle
            hasUndo = true
            renderShapesToBitmap = true
            renderToScreen = true
        }
    }

    override fun afterExecute(drawHandler: DrawHandler) {
        super.afterExecute(drawHandler)
        if (!hasUndo) {
            return
        }
        drawHandler.clearScreen()
        drawHandler.renderContext.matrix.reset()
        drawHandler.rotateScreen(undoAngle, editBundle.getContainerCenterPoint())
        drawHandler.renderShapesToBitmap()
        drawHandler.renderToScreen()
    }
}