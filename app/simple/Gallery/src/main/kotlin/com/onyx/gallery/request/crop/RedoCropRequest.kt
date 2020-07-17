package com.onyx.gallery.request.crop

import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/16 15:28
 **/
class RedoCropRequest : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        drawHandler.redoCrop()?.run {
            drawHandler.restoreCropSnapshot(this)
            renderShapesToBitmap = true
            renderToScreen = true
        }
    }

}