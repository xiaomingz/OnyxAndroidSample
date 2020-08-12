package com.onyx.gallery.request.crop

import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/16 15:28
 **/
class UndoCropRequest : BaseRequest() {
    private var undoAngle: Float = 0f
    override fun execute(drawHandler: DrawHandler) {
        drawHandler.undoCrop()?.run {
            drawHandler.restoreCropSnapshot(this)
            undoAngle = rotateAngle
            renderShapesToBitmap = true
            renderToScreen = true
        }
    }

    override fun afterExecute(drawHandler: DrawHandler) {
        super.afterExecute(drawHandler)
        drawHandler.clearScreen()
        drawHandler.renderContext.matrix.reset()
        drawHandler.rotateScreen(undoAngle, globalEditBundle.getContainerCenterPoint())
        drawHandler.renderShapesToBitmap()
        drawHandler.renderToScreen()
    }
}