package com.onyx.gallery.views.shape

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Rect
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.gallery.utils.ExpandShapeFactory


/**
 * Created by Leung 2020/7/11 15:04
 **/
class WaveLineShape : BaseLineShape() {
    private val waveHeightFactor = 1.2f
    private val waveLengthFactor = 1.5f
    val limitRect = Rect()

    override fun getType(): Int = ExpandShapeFactory.SHAP_WAVE_LINE

    override fun render(renderContext: RenderContext) {
        applyStrokeStyle(renderContext)
        val path = Path().apply {
            applyWave(this, renderContext.paint)
            transform(getRenderMatrix(renderContext))
        }
        renderContext.canvas.drawPath(path, renderContext.paint)
    }

    private fun applyWave(path: Path, paint: Paint) {
        path.moveTo(downPoint.getX(), downPoint.getY())
        val strokeWidth = paint.strokeWidth
        val waveLength = strokeWidth * waveLengthFactor
        val waveHeight = strokeWidth * waveHeightFactor
        val distance = Math.abs(downPoint.x - currentPoint.x)
        val waveCount = (distance / waveLength).toInt()
        val pathMeasure = PathMeasure()
        for (i in 1..waveCount / 2) {
            pathMeasure.setPath(path, false)
            if (downPoint.x > currentPoint.x) {
                path.rQuadTo(-waveLength / 2, -waveHeight, -waveLength, 0f);
                path.rQuadTo(-waveLength / 2, waveHeight, -waveLength, 0f);
            } else {
                path.rQuadTo(waveLength / 2, -waveHeight, waveLength, 0f);
                path.rQuadTo(waveLength / 2, waveHeight, waveLength, 0f);
            }
            if (checkBeyondLimitRect(waveLength) && i == waveCount / 2) {
                break
            }
        }
    }

    private fun checkBeyondLimitRect(waveLength: Float): Boolean {
        if (limitRect.isEmpty) {
            return false
        }
        if (downPoint.x > currentPoint.x) {
            val x = currentPoint.getX() - waveLength * 2
            return x < limitRect.left
        }
        val x = currentPoint.getX() + waveLength * 2
        return x > limitRect.right
    }

}