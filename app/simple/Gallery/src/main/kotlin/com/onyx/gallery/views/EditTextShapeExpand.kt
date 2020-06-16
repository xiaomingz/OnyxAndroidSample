package com.onyx.gallery.views

import android.graphics.*
import android.text.Layout
import android.text.SpannableString
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.LeadingMarginSpan
import com.onyx.android.sdk.scribble.data.ShapeTextStyle
import com.onyx.android.sdk.scribble.shape.EditTextShape
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.utils.TextLayoutUtils

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
        val layout = createTextLayout(this, textPaint)
        renderContext.canvas.translate(0f, TextLayoutUtils.getFontBaseLineTranslate(this).toFloat())
        layout.draw(renderContext.canvas)
        renderContext.canvas.restore()
    }

    private fun createTextLayout(shape: Shape, textPaint: TextPaint): StaticLayout {
        val textStyle = shape.textStyle
        val content = shape.renderText
        buildShapeTextPaint(textPaint, textStyle, content)
        if (shape.isTransparent) {
            textPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        } else {
            textPaint.xfermode = null
        }
        val spannableString = buildTextContent(content)
        return StaticLayout(spannableString,
                textPaint,
                textStyle.textWidth,
                Layout.Alignment.ALIGN_NORMAL,
                textStyle.textSpacing,
                0f,
                false)
    }

    private fun buildTextContent(content: String): SpannableString {
        val spannableString = SpannableString(content)
        if (isIndentation) {
            val what = LeadingMarginSpan.Standard((textStyle.textSize * 2).toInt(), 0)
            spannableString.setSpan(what, 0, spannableString.length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
        }
        return spannableString
    }

    private fun buildShapeTextPaint(textPaint: TextPaint, textStyle: ShapeTextStyle, content: String) {
        val typeface = TextLayoutUtils.getShapeTypeface(textStyle, content)
        textPaint.isUnderlineText = textStyle.isTextUnderline
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.textSize = textStyle.textSize
        textPaint.isFakeBoldText = textStyle.isTextBold
        textPaint.typeface = Typeface.create(typeface, if (textStyle.isTextItalic) Typeface.ITALIC else Typeface.NORMAL)
    }

}