package com.onyx.gallery.request.textInput

import android.graphics.PointF
import android.graphics.RectF
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.utils.RenderHandlerUtils
import com.onyx.gallery.utils.StaticLayoutUtils.createTextLayout
import com.onyx.gallery.views.shape.EditTextShapeExpand

/**
 * Created by Leung on 2020/6/11
 */
class AdjustTextInputWidthRequest(editBundle: EditBundle,
                                  private val shape: Shape,
                                  private val movedPoint: TouchPoint,
                                  private val cursorShape: Shape?,
                                  private val lastPoint: TouchPoint) : BaseRequest(editBundle) {

    override fun execute(drawHandler: DrawHandler) {
        val shapeRectF = shape.boundingRect
        val textStyle = shape.textStyle ?: return
        val limitRect = drawHandler.currLimitRect
        if (!limitRect.contains(movedPoint.x.toInt(), movedPoint.y.toInt())) {
            return
        }
        val orgDown = shape.points[0]
        val down = PointF(orgDown.x, orgDown.y)
        val orgUp = shape.points[1]
        val up = PointF(orgUp.x, orgUp.y)

        calculatePoint(shapeRectF, up, down)
        updateTextShape(down, up)
        textStyle.textWidth = (up.x - down.x).toInt()
        RenderHandlerUtils.renderSelectionRect(drawHandler, shape, cursorShape)
    }

    private fun calculatePoint(shapeRectF: RectF, up: PointF, down: PointF) {
        val x = movedPoint.x - lastPoint.x
        if (movedPoint.x > shapeRectF.centerX()) {
            up.x += x
        } else {
            down.x += x
        }
    }

    private fun updateTextShape(down: PointF, up: PointF) {
        shape.points[0].x = down.x
        shape.points[0].y = down.y
        shape.points[1].x = up.x
        shape.points[1].y = up.y
        shape.updatePoints()
        val staticLayout = createTextLayout(shape as EditTextShapeExpand)
        val originRect = shape.originRect
        originRect.bottom = originRect.top + staticLayout.height
    }

}