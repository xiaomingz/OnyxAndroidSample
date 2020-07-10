package com.onyx.gallery.request.textInput

import android.graphics.RectF
import com.onyx.android.sdk.scribble.data.SelectionRect
import com.onyx.android.sdk.scribble.shape.EditTextShape
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.RectUtils
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/6/18
 */
class UpdateSelectionRectRequest(private val textShape: EditTextShape) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        textShape.applyTransformMatrix()
        val renderContext = drawHandler.renderContext
        renderContext.clearSelectionRect()
        val shapes = mutableListOf<Shape>(textShape)
        val normalizeScale = drawHandler.drawingArgs.normalizeScale
        val limitRect = RectF(drawHandler.currLimitRect)
        RectUtils.scale(limitRect, normalizeScale, normalizeScale)
        renderContext.selectionRect = SelectionRect.buildSelectionRect(shapes, renderContext, limitRect)
    }

}