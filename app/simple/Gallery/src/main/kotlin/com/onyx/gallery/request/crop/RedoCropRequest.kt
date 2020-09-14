package com.onyx.gallery.request.crop

import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/16 15:28
 **/
class RedoCropRequest(editBundle: EditBundle) : BaseRequest(editBundle) {
    private var hasRedo = false
    private var redoAngle: Float = 0f
    override fun execute(drawHandler: DrawHandler) {
        drawHandler.redoCrop()?.run {
            drawHandler.restoreCropSnapshot(this)
            redoAngle = rotateAngle
            hasRedo = false
            renderShapesToBitmap = true
            renderToScreen = true
        }
    }

    override fun afterExecute(drawHandler: DrawHandler) {
        super.afterExecute(drawHandler)
        if (!hasRedo) {
            return
        }
        drawHandler.clearScreen()
        drawHandler.rotateScreen(redoAngle, editBundle.getContainerCenterPoint())
        drawHandler.renderShapesToBitmap()
        drawHandler.renderToScreen()
    }

}