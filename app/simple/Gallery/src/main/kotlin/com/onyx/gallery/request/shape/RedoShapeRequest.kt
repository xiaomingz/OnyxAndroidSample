package com.onyx.gallery.request.shape

import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/10 16:42
 **/
class RedoShapeRequest(editBundle: EditBundle) : BaseRequest(editBundle) {
    override fun execute(drawHandler: DrawHandler) {
        drawHandler.redoShapes()
        renderShapesToBitmap = true
        renderToScreen = true
    }
}