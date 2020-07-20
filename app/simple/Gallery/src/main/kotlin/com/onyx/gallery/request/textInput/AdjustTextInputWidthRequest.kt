package com.onyx.gallery.request.textInput

import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.scribble.data.SelectionRect
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.utils.RenderHandlerUtils
import com.onyx.gallery.utils.StaticLayoutUtils.createTextLayout
import com.onyx.gallery.views.shape.EditTextShapeExpand

/**¬
 * Created by Leung on 2020/6/11
 */
class AdjustTextInputWidthRequest(private val shape: Shape,
                                  private val movedPoint: TouchPoint,
                                  private val cursorShape: Shape?,
                                  private val lastPoint: TouchPoint) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val rectF = shape.boundingRect
        val textStyle = shape.textStyle ?: return
        val up = shape.points[1]
        val down = shape.points[0]
        val x = movedPoint.x - lastPoint.x
        val selectionRect: SelectionRect = drawHandler.renderContext.selectionRect
        if (movedPoint.x > rectF.centerX()) {
            up.x = up.x + x
            selectionRect.originRect.right = movedPoint.x
        } else {
            down.x = down.x + x
            selectionRect.originRect.left = movedPoint.x
        }
        textStyle.textWidth = RenderHandlerUtils.getMatrixNormalValue(drawHandler.renderContext, up.x - down.x).toInt()
        shape.updatePoints()
        val staticLayout = createTextLayout(shape as EditTextShapeExpand)
        val height = staticLayout.height
        val originRect = shape.originRect
        originRect.bottom = originRect.top + height
        RenderHandlerUtils.renderSelectionRect(drawHandler, shape, cursorShape)
    }

}