package com.onyx.gallery.request.transform

import android.graphics.PointF
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.android.sdk.utils.CollectionUtils
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.utils.ExpandShapeFactory

/**
 * Created by Leung on 2020/6/11
 */
class TranslateRequest(editBundle: EditBundle, private val shapes: MutableList<Shape>, private val movedPoint: TouchPoint, lastPoint: TouchPoint, private val offsetPoint: PointF) : BaseRequest(editBundle) {

    override fun execute(drawHandler: DrawHandler) {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            return
        }
        val renderContext: RenderContext = drawHandler.renderContext
        val selectionRect = renderContext.selectionRect ?: return
        val translatePoint: TouchPoint = selectionRect.onTranslate(offsetPoint.x, offsetPoint.y)
        ShapeUtils.translate(shapes, translatePoint.x, translatePoint.y)

        val initMatrix = drawHandler.getInitMatrix()
        val renderShapes = mutableListOf<Shape>()
        shapes.forEach { shape ->
            val shapeClone = ExpandShapeFactory.ShapeClone(shape)
            renderShapes.add(shapeClone)
            shape.applyTransformMatrix()
        }

        drawHandler.renderVarietyShapesToScreen(renderShapes, initMatrix)
        drawHandler.postSelectionBundle()
    }

}