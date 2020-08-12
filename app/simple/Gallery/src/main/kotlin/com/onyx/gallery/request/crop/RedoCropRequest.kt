package com.onyx.gallery.request.crop

import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/16 15:28
 **/
class RedoCropRequest : BaseRequest() {
    private var redoAngle: Float = 0f
    override fun execute(drawHandler: DrawHandler) {
        drawHandler.redoCrop()?.run {
            drawHandler.restoreCropSnapshot(this)
            redoAngle = rotateAngle
            renderShapesToBitmap = true
            renderToScreen = true
        }
    }

    override fun afterExecute(drawHandler: DrawHandler) {
        super.afterExecute(drawHandler)
        drawHandler.clearScreen()
        drawHandler.renderContext.matrix.reset()
        drawHandler.rotateScreen(redoAngle, globalEditBundle.getContainerCenterPoint())
        drawHandler.renderShapesToBitmap()
        drawHandler.renderToScreen()
    }

}