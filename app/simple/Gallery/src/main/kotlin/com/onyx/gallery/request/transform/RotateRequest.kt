package com.onyx.gallery.request.transform

import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/6/22
 */
class RotateRequest(editBundle: EditBundle, private val angle: Float) : BaseRequest(editBundle) {
    override fun execute(drawHandler: DrawHandler) {
        drawHandler.clearScreen()
        drawHandler.rotateScreen(angle, editBundle.getContainerCenterPoint())
        renderShapesToBitmap = true
        renderToScreen = true
    }
}