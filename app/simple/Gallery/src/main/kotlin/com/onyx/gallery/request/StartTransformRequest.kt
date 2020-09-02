package com.onyx.gallery.request

import android.graphics.RectF
import com.onyx.android.sdk.scribble.data.SelectionRect
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import java.util.*

/**
 * Created by Leung on 2020/6/10
 *
 */
class StartTransformRequest(editBundle: EditBundle, private val transformShapes: List<Shape>, private var limitRect: RectF) : BaseRequest(editBundle) {

    override fun execute(drawHandler: DrawHandler) {
        if (transformShapes == null || transformShapes.isEmpty()) {
            return
        }
        val renderContext: RenderContext = drawHandler.renderContext
        renderContext.clearSelectionRect()
        renderContext.eraseBitmap()
        val pageShapes: MutableList<Shape> = ArrayList(drawHandler.getAllShapes())
        pageShapes.removeAll(transformShapes)
        drawHandler.renderToBitmap(pageShapes)

        val selectionRect = SelectionRect.buildSelectionRect(transformShapes, renderContext, limitRect)
        renderContext.selectionRect = selectionRect
        drawHandler.renderVarietyShapesToScreen(transformShapes)
        drawHandler.postSelectionBundle()
    }

}