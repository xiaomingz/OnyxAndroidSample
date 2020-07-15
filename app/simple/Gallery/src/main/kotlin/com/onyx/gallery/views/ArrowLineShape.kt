package com.onyx.gallery.views

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.gallery.utils.ExpandShapeFactory

/**
 * Created by Leung 2020/7/14 18:55
 **/
class ArrowLineShape : BaseLineShape() {
    private val arrowSizeFactor = 4.5f
    private val arrowTanAngle = 3.5 / 8
    
    override fun getType(): Int = ExpandShapeFactory.SHAP_ARROW_LINE

    override fun render(renderContext: RenderContext) {
        applyStrokeStyle(renderContext)
        val paint = renderContext.paint
        val canvas = renderContext.canvas
        val path = Path().apply {
            moveTo(downPoint.x, downPoint.y)
            lineTo(currentPoint.x, currentPoint.y)
            transform(getRenderMatrix(renderContext))
        }
        canvas.drawPath(path, paint)
        val arrowSize = paint.strokeWidth * arrowSizeFactor
        drawArrows(canvas, paint, arrowSize, downPoint.x, downPoint.y, currentPoint.x, currentPoint.y)
    }

    private fun drawArrows(canvas: Canvas, paint: Paint, arrowSize: Float, x1: Float, y1: Float, x2: Float, y2: Float) {
        val awrad = Math.atan(arrowTanAngle)
        val arrXY_1 = rotateVec(x2 - x1, y2 - y1, awrad, arrowSize.toDouble())
        val arrXY_2 = rotateVec(x2 - x1, y2 - y1, -awrad, arrowSize.toDouble())

        val x3 = x2 - arrXY_1[0]
        val y3 = y2 - arrXY_1[1]

        val x4 = x2 - arrXY_2[0]
        val y4 = y2 - arrXY_2[1]

        val arrowsPath = Path()
        arrowsPath.moveTo(x2, y2)
        arrowsPath.lineTo(x3, y3)
        arrowsPath.lineTo(x4, y4)
        arrowsPath.close()

        canvas.drawLine(x3, y3, x2, y2, paint)
        canvas.drawLine(x4, y4, x2, y2, paint)
    }

    private fun rotateVec(px: Float, py: Float, angle: Double, arrowSize: Double): FloatArray {
        var vx = px * Math.cos(angle) - py * Math.sin(angle)
        var vy = px * Math.sin(angle) + py * Math.cos(angle)
        val d = Math.sqrt(vx * vx + vy * vy)
        vx = vx / d * arrowSize
        vy = vy / d * arrowSize
        return floatArrayOf(vx.toFloat(), vy.toFloat())
    }

}