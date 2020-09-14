package com.onyx.gallery.request

import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import com.onyx.android.sdk.scribble.data.SelectionRect
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.utils.ExpandShapeFactory
import java.util.*


/**
 * Created by Leung on 2020/6/10
 *
 */
class StartTransformRequest(editBundle: EditBundle, private val transformShape: Shape, private var limitRect: RectF) : BaseRequest(editBundle) {

    override fun execute(drawHandler: DrawHandler) {
        if (transformShape == null) {
            return
        }
        val renderContext: RenderContext = drawHandler.renderContext
        renderContext.clearSelectionRect()
        renderContext.eraseBitmap()
        val pageShapes: MutableList<Shape> = ArrayList(drawHandler.getAllShapes())
        pageShapes.remove(transformShape)
        drawHandler.renderToBitmap(pageShapes)

        val limitRectF = RectF(limitRect)
        val selectionRect = SelectionRect.buildSelectionRect(mutableListOf(transformShape), RenderContext.create(Paint(), Matrix()), limitRectF)
        renderContext.selectionRect = selectionRect

        val shapeClone = ExpandShapeFactory.ShapeClone(transformShape)
        val initMatrix = drawHandler.getInitMatrix()

        drawHandler.renderVarietyShapesToScreen(mutableListOf(shapeClone), initMatrix)
        drawHandler.postSelectionBundle()
    }

}