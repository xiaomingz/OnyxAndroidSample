package com.onyx.gallery.views

import android.graphics.DashPathEffect
import android.graphics.Path
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.gallery.utils.ExpandShapeFactory


/**
 * Created by Leung 2020/7/11 11:30
 **/
class DashLineShape : BaseLineShape() {
    private val intervalFactor = 1.3f

    override fun getType(): Int = ExpandShapeFactory.SHAP_DASH_LINE

    override fun render(renderContext: RenderContext) {
        val path = Path().apply {
            moveTo(downPoint.getX(), downPoint.getY())
            lineTo(currentPoint.getX(), currentPoint.getY())
            transform(getRenderMatrix(renderContext))
        }
        applyStrokeStyle(renderContext)
        val interval = renderContext.paint.strokeWidth * intervalFactor
        val dashPathEffect = DashPathEffect(floatArrayOf(interval, interval), 0f)
        renderContext.run {
            paint.setPathEffect(dashPathEffect)
            canvas.drawPath(path, paint)
            paint.setPathEffect(null)
        }
    }

}