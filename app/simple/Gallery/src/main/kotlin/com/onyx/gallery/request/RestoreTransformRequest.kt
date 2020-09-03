package com.onyx.gallery.request

import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/6/15
 */
class RestoreTransformRequest(editBundle: EditBundle, val refresh: Boolean = true) : BaseRequest(editBundle) {

    override fun execute(drawHandler: DrawHandler) {
        val renderContext = drawHandler.renderContext
        renderContext.selectionRect = null
        renderContext.viewPortScale = 1.0f
        renderContext.matrix.reset()
        drawHandler.updateLimitRect(false)
        renderShapesToBitmap = refresh
        renderToScreen = refresh
    }

}