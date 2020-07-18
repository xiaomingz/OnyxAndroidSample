package com.onyx.gallery.views.shape

import android.graphics.Matrix
import android.text.TextPaint
import com.onyx.android.sdk.scribble.shape.EditTextShape
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.utils.TextLayoutUtils
import com.onyx.gallery.utils.ChineseConvertorUtils
import com.onyx.gallery.utils.StaticLayoutUtils

/**
 * Created by Leung on 2020/6/16
 */
class EditTextShapeExpand : EditTextShape() {

    companion object {
        const val INDENTATION_COUNT = 2
    }

    var isIndentation = false
    var isTraditional = false

    fun getIndentationOffset(): Float {
        val textStyle = textStyle ?: return 0f
        return textStyle.textSize * INDENTATION_COUNT
    }

    override fun render(renderContext: RenderContext) {
        val textStyle = textStyle ?: return
        val sx = downPoint.getX()
        val sy = downPoint.getY()
        val pts = floatArrayOf(sx, sy)
        getRenderMatrix(renderContext).mapPoints(pts)
        text = convertText(text)
        renderContext.canvas.save()
        renderContext.canvas.translate(pts[0], pts[1])
        if (renderContext.matrix != null) {
            val matrixValues = FloatArray(9)
            renderContext.matrix.getValues(matrixValues)
            val scaleX = matrixValues[Matrix.MSCALE_X]
            val scaleY = matrixValues[Matrix.MSCALE_Y]
            renderContext.canvas.scale(scaleX * textStyle.pointScale, scaleY * textStyle.pointScale)
        }
        val textPaint = TextPaint()
        textPaint.color = getRenderColor(renderContext)
        val layout = StaticLayoutUtils.createTextLayout(this, textPaint)
        renderContext.canvas.translate(0f, TextLayoutUtils.getFontBaseLineTranslate(this).toFloat())
        layout.draw(renderContext.canvas)
        renderContext.canvas.restore()
    }

    private fun convertText(text: String?): String? {
        return if (isTraditional) {
            ChineseConvertorUtils.toTraditional(text)
        } else {
            ChineseConvertorUtils.toSimplified(text)
        }
    }


}