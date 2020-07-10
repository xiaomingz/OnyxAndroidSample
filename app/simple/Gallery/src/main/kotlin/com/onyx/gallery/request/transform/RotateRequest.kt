package com.onyx.gallery.request.transform

import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/6/22
 */
class RotateRequest(private val angle: Float) : BaseRequest() {
    override fun execute(drawHandler: DrawHandler) {
        drawHandler.clearScreen()
        drawHandler.rotateScreen(angle, globalEditBundle.getContainerCenterPoint())
        renderShapesToBitmap = true
        renderToScreen = true
    }
}