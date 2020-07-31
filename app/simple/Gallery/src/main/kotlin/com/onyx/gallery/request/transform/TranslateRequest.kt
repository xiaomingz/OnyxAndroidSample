package com.onyx.gallery.request.transform

import android.graphics.PointF
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.android.sdk.utils.CollectionUtils
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.utils.RenderHandlerUtils

/**
 * Created by Leung on 2020/6/11
 */
class TranslateRequest(private val shapes: MutableList<Shape>, private val movedPoint: TouchPoint, private val offsetPoint: PointF) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            return
        }
        val renderContext: RenderContext = drawHandler.renderContext
        val selectionRect = renderContext.selectionRect ?: return
        if (!drawHandler.currLimitRect.contains(movedPoint.x.toInt(), movedPoint.y.toInt())) {
            return
        }
        val translatePoint: TouchPoint = RenderHandlerUtils.onTranslate(selectionRect, offsetPoint.x, offsetPoint.y)
        ShapeUtils.translate(shapes, translatePoint.x, translatePoint.y)
        drawHandler.renderVarietyShapesToScreen(shapes)
        drawHandler.postSelectionBundle()
    }

}