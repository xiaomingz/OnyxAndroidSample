package com.onyx.gallery.request.textInput

import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.CollectionUtils
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/6/11
 */
class SaveTextShapesRequest(private val shapes: MutableList<Shape>) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            return
        }
        for (shape in shapes) {
            shape.applyTransformMatrix()
        }
        drawHandler.addShape(shapes)
        drawHandler.renderToBitmap(shapes)
        renderToScreen = true
    }

}