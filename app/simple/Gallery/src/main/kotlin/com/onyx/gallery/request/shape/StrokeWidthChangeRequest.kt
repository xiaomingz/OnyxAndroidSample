package com.onyx.gallery.request.shape

import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/21 17:52
 **/
class StrokeWidthChangeRequest(val strokeWidth: Float) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        drawHandler.setStrokeWidth(strokeWidth)
        renderToScreen = true
    }

}