package com.onyx.gallery.request.shape

import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler


/**
 * Created by Leung on 2020/5/20
 */
class RenderVarietyShapeRequest(private val shape: MutableList<Shape>) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        renderShapesToBitmap = true
        renderToScreen = false
        drawHandler.renderVarietyShapesToScreen(shape)
    }

}


