package com.onyx.gallery.request.textInput

import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.StringUtils
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/6/11
 */
class SaveTextShapesRequest(private val shape: Shape) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val handwritingShape = drawHandler.getHandwritingShape()
        handwritingShape.forEach {
            if (it == shape && StringUtils.isEquals(it.text, shape.text)) {
                return
            }
        }
        shape.applyTransformMatrix()
        drawHandler.addShape(shape)
        drawHandler.renderToBitmap(shape)
        renderToScreen = true
    }

}