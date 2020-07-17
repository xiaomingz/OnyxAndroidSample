package com.onyx.gallery.request.shape

import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/17 16:45
 **/
class AddShapesInBackgroundRequest(val shapes: MutableList<Shape>) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        drawHandler.addShapes(shapes)
        drawHandler.renderToBitmap(shapes)
    }

}