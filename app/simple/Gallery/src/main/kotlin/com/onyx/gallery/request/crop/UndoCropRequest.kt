package com.onyx.gallery.request.crop

import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/16 15:28
 **/
class UndoCropRequest : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        drawHandler.undoCrop()?.run {
            drawHandler.restoreCropSnapshot(this)
            renderShapesToBitmap = true
            renderToScreen = true
        }
    }

}