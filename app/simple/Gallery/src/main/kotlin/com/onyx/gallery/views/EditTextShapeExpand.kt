package com.onyx.gallery.views

import android.graphics.Matrix
import android.text.TextPaint
import com.onyx.android.sdk.scribble.shape.EditTextShape
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.utils.TextLayoutUtils
import com.onyx.gallery.utils.StaticLayoutUtils

/**
 * Created by Leung on 2020/6/16
 */
class EditTextShapeExpand : EditTextShape() {

    var isIndentation = false

    override fun render(renderContext: RenderContext) {
        val textStyle = textStyle ?: return
        val sx = downPoint.getX()
        val sy = downPoint.getY()
        val pts = floatArrayOf(sx, sy)
        getRenderMatrix(renderContext).mapPoints(pts)

        renderContext.canvas.save()
        renderContext.canvas.translate(pts[0], pts[1])
        if (renderContext.matrix != null) {
            val f = FloatArray(9)
            renderContext.matrix.getValues(f)
            val scaleX = f[Matrix.MSCALE_X]
            val scaleY = f[Matrix.MSCALE_Y]
            renderContext.canvas.scale(scaleX * textStyle.pointScale, scaleY * textStyle.pointScale)
        }
        val textPaint = TextPaint()
        textPaint.color = getRenderColor(renderContext)
        val layout = StaticLayoutUtils.createTextLayout(this, textPaint, isIndentation)
        renderContext.canvas.translate(0f, TextLayoutUtils.getFontBaseLineTranslate(this).toFloat())
        layout.draw(renderContext.canvas)
        renderContext.canvas.restore()
    }


}