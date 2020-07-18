package com.onyx.gallery.utils

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableString
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.LeadingMarginSpan
import com.onyx.android.sdk.scribble.data.ShapeTextStyle
import com.onyx.android.sdk.scribble.utils.TextLayoutUtils
import com.onyx.gallery.views.shape.EditTextShapeExpand


/**
 * Created by Leung on 2020/6/16
 */
object StaticLayoutUtils {

    @JvmStatic
    fun createTextLayout(shape: EditTextShapeExpand): StaticLayout {
        val tp = TextPaint()
        tp.color = shape.color
        return createTextLayout(shape, tp)
    }

    @JvmStatic
    fun createTextLayout(shape: EditTextShapeExpand, textPaint: TextPaint): StaticLayout {
        val textStyle = shape.textStyle
        val content = shape.renderText
        buildShapeTextPaint(textPaint, textStyle, content)
        if (shape.isTransparent) {
            textPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        } else {
            textPaint.xfermode = null
        }
        val span = buildTextContent(content, textStyle.textSize, shape.isIndentation)
        return StaticLayout(span,
                textPaint,
                textStyle.textWidth,
                Layout.Alignment.ALIGN_NORMAL,
                textStyle.textSpacing,
                0f,
                false)
    }

    @JvmStatic
    fun createTextLayout(content: String, textStyle: ShapeTextStyle, isIndentation: Boolean): StaticLayout {
        val tp = TextPaint()
        val spannableString = buildTextContent(content, textStyle.textSize, isIndentation)
        buildShapeTextPaint(tp, textStyle, content)
        return StaticLayout(spannableString,
                tp,
                textStyle.textWidth,
                Layout.Alignment.ALIGN_NORMAL,
                textStyle.textSpacing,
                0f,
                false)
    }

    @JvmStatic
    private fun buildTextContent(content: String, textSize: Float, isIndentation: Boolean): SpannableString {
        val spannableString = SpannableString(content)
        if (isIndentation) {
            val what = LeadingMarginSpan.Standard((textSize * EditTextShapeExpand.INDENTATION_COUNT).toInt(), 0)
            spannableString.setSpan(what, 0, spannableString.length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
        }
        return spannableString
    }

    @JvmStatic
    private fun buildShapeTextPaint(textPaint: TextPaint, textStyle: ShapeTextStyle, content: String) {
        val typeface = TextLayoutUtils.getShapeTypeface(textStyle, content)
        textPaint.isUnderlineText = textStyle.isTextUnderline
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.textSize = textStyle.textSize
        textPaint.isFakeBoldText = textStyle.isTextBold
        textPaint.typeface = Typeface.create(typeface, if (textStyle.isTextItalic) Typeface.ITALIC else Typeface.NORMAL)
    }
}