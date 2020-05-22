package com.simplemobiletools.commons.views

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.onyx.android.appcompat.R

/**
 * <pre>
 *     author : suicheng
 *     time   : 2020/5/14 16:48
 *     desc   :
 * </pre>
 */
class DashLineItemDivider(val context: Context) : RecyclerView.ItemDecoration() {

    var dashLineHeight: Int = context.resources.getDimensionPixelSize(R.dimen.dash_Line_length)
    var dashSpaceWith: Int = context.resources.getDimensionPixelSize(R.dimen.dash_space_length)
    var dashLineWidth: Int = context.resources.getDimensionPixelSize(R.dimen.dash_line_width)
    var dashLineColor: Int = context.resources.getColor(R.color.background_black)

    var paddingLeft = 0
    var paddingRight = 0

    fun setPaddingLeft(paddingLeft: Int): DashLineItemDivider {
        this.paddingLeft = paddingLeft
        return this
    }

    fun setPaddingRight(paddingRight: Int): DashLineItemDivider {
        this.paddingRight = paddingRight
        return this
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i) ?: continue
            val left = child.left + if (paddingLeft == 0) child.paddingLeft else paddingLeft
            val right = child.right - if (paddingRight == 0) child.paddingRight else paddingRight
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin
            drawLineWithPath(c, left, top, right)
        }
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        val height: Int = getDividerHeight()
        if (height > 0) {
            outRect.bottom = height
        }
    }

    fun getDividerHeight(): Int {
        return dashLineWidth
    }

    private fun drawLineWithPath(c: Canvas, left: Int, top: Int, right: Int) {
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.color = dashLineColor
        if (getDividerHeight() > 0) {
            paint.strokeWidth = getDividerHeight().toFloat()
        }
        val path = Path()
        path.moveTo(left.toFloat(), top.toFloat())
        path.lineTo(right.toFloat(), top.toFloat())
        val effects: PathEffect = DashPathEffect(floatArrayOf(dashLineHeight.toFloat(), dashSpaceWith.toFloat()), 0f)
        paint.pathEffect = effects
        c.drawPath(path, paint)
    }
}