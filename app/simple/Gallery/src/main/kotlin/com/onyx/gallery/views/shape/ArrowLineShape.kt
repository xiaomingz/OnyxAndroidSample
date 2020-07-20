package com.onyx.gallery.views.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.gallery.utils.ExpandShapeFactory

/**
 * Created by Leung 2020/7/14 18:55
 **/
class ArrowLineShape : BaseLineShape() {
    private val arrowLenFactor = 4.5f
    private val arrowHeight = 8
    private val halfBottomLine = 3.5

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
        val arrowLen = paint.strokeWidth * arrowLenFactor
        drawArrows(canvas, paint, arrowLen, downPoint.x, downPoint.y, currentPoint.x, currentPoint.y)
    }

    private fun drawArrows(canvas: Canvas, paint: Paint, arrowLen: Float, x1: Float, y1: Float, x2: Float, y2: Float) {
        val awrad = Math.atan(halfBottomLine / arrowHeight)
        val arrXY_3 = rotateVector(x2 - x1, y2 - y1, awrad, arrowLen.toDouble())
        val arrXY_4 = rotateVector(x2 - x1, y2 - y1, -awrad, arrowLen.toDouble())

        val x3 = x2 - arrXY_3[0]
        val y3 = y2 - arrXY_3[1]

        val x4 = x2 - arrXY_4[0]
        val y4 = y2 - arrXY_4[1]

        val arrowsPath = Path()
        arrowsPath.moveTo(x2, y2)
        arrowsPath.lineTo(x3, y3)
        arrowsPath.moveTo(x2, y2)
        arrowsPath.lineTo(x4, y4)
        canvas.drawPath(arrowsPath, paint)
    }

    private fun rotateVector(px: Float, py: Float, angle: Double, arrowLen: Double): FloatArray {
        var vx = px * Math.cos(angle) - py * Math.sin(angle)
        var vy = px * Math.sin(angle) + py * Math.cos(angle)
        val d = Math.sqrt(vx * vx + vy * vy)
        vx = vx / d * arrowLen
        vy = vy / d * arrowLen
        return floatArrayOf(vx.toFloat(), vy.toFloat())
    }

}