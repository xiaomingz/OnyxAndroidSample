package com.onyx.gallery.views.shape

import android.graphics.*
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.scribble.shape.BaseShape
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.android.sdk.utils.Benchmark
import com.onyx.gallery.BuildConfig
import com.onyx.gallery.utils.ExpandShapeFactory

/**
 * Created by Leung 2020/8/25 14:56
 **/
enum class ImageTrackType {
    FILL, STROKE
}

open class ImageTrackShape(var imageTrackType: ImageTrackType = ImageTrackType.STROKE) : BaseShape() {
    private val offset = 100
    private val minRect = Rect()
    private val bounds = RectF()
    private val region = Region()
    lateinit var imageSize: Size
    lateinit var backgroundBitmap: Bitmap
    private val transparentPaint by lazy { createTransparentPaint() }

    override fun getType(): Int = ExpandShapeFactory.SHAPE_IMAGE_TRACK

    override fun render(renderContext: RenderContext) {
        val benchmark = Benchmark()

        applyStrokeStyle(renderContext)
        val path = ShapeUtils.renderShape(getRenderMatrix(renderContext), normalizedPoints) ?: return
        val paint = renderContext.paint

        val orgStyle = paint.style
        val orgColor = paint.color
        paint.style = Paint.Style.STROKE
        if (imageTrackType == ImageTrackType.FILL) {
            paint.style = Paint.Style.FILL
        }
        paint.color = Color.BLACK

        val canvas = renderContext.canvas
        val layerCount = canvas.saveLayer(0f, 0f, imageSize.width.toFloat(), imageSize.height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        canvas.drawPath(path, paint)

        val rect = getRenderRect(paint.strokeWidth, path)
        canvas.drawBitmap(backgroundBitmap, rect, rect, transparentPaint)

        canvas.restoreToCount(layerCount)

        paint.style = orgStyle
        paint.color = orgColor

        if (BuildConfig.DEBUG) {
            benchmark.report(javaClass.simpleName)
        }
    }

    private fun createTransparentPaint(): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setFilterBitmap(false)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        return paint
    }

    private fun getRenderRect(strokeWidth: Float, path: Path): Rect {
        updateMinRect()
        path.computeBounds(bounds, true)
        region.setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
        return Rect().apply {
            RectF(region.bounds).round(this)
            if (isEmpty) set(minRect)
        }.apply {
            val strokeWidth = Math.round(strokeWidth * 2)
            set(left - strokeWidth, top - strokeWidth, right + strokeWidth, bottom + strokeWidth)
        }
    }

    private fun updateMinRect() {
        val touchPoint = points.points[0]
        minRect.set(touchPoint.x.toInt() - offset, touchPoint.y.toInt() - offset, (offset + touchPoint.x).toInt(), (offset + touchPoint.y).toInt())
    }

}
