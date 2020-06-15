package com.onyx.gallery.request

import android.graphics.Matrix
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/6/15
 */
class RestoreTransformRequest : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val renderContext = drawHandler.renderContext
        val matrix = Matrix()
        matrix.setScale(1f, 1f)
        renderContext.setMatrix(matrix)
        renderToScreen = true
    }

}