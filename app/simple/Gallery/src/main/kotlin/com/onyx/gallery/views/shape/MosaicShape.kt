package com.onyx.gallery.views.shape

import android.graphics.*
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.scribble.shape.BaseShape
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.utils.Benchmark
import com.onyx.gallery.BuildConfig
import com.onyx.gallery.utils.ExpandShapeFactory
import com.onyx.gallery.utils.MosaicUtils

/**
 * Created by Leung 2020/7/18 10:32
 **/
class MosaicShape : BaseShape() {
    private val offset = 100
    private val minRect = Rect()
    private val bounds = RectF()
    private val region = Region()
    lateinit var imageSize: Size
    private lateinit var  path : Path
    lateinit var mosaicBitmap: Bitmap
    private val mosaicPaint: Paint by lazy { MosaicUtils.getMosaicPaint() }

    override fun getType(): Int = ExpandShapeFactory.SHAP_MOSAIC

    override fun render(renderContext: RenderContext) {
        if (mosaicBitmap.isRecycled ||  points.points.isEmpty()) {
            return
        }
        val benchmark = Benchmark()

        applyStrokeStyle(renderContext)
        val paint = renderContext.paint
        val canvas = renderContext.canvas

        path = createPath()
        path.transform(getRenderMatrix(renderContext))

        val orgStyle = paint.style
        val orgColor = paint.color
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK

        val layerCount = canvas.saveLayer(0f, 0f, imageSize.width.toFloat(), imageSize.height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        canvas.drawPath(path, paint)

        val rect = getRenderRect(paint)
        canvas.drawBitmap(mosaicBitmap, rect, rect, mosaicPaint)
        canvas.restoreToCount(layerCount)

        paint.style = orgStyle
        paint.color = orgColor

        if (BuildConfig.DEBUG ) {
            benchmark.report(javaClass.simpleName)
        }
    }

    private fun createPath(): Path {
        val path = Path()
        points.points.forEachIndexed { index, touchPoint ->
            if (index == 0) {
                path.moveTo(touchPoint.x, touchPoint.y)
           } else {
                path.lineTo(touchPoint.x, touchPoint.y)
            }
        }
        return  path
    }

    private fun getRenderRect(paint: Paint): Rect {
        updateMinRect()
        path.computeBounds(bounds, true)
        region.setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
       return Rect().apply {
            RectF(region.bounds).round(this)
            if (isEmpty)  set(minRect)
        }.apply {
            val strokeWidth = Math.round(paint.strokeWidth * 2)
            set(left - strokeWidth, top - strokeWidth, right + strokeWidth, bottom + strokeWidth)
        }
    }

    private fun updateMinRect(){
        val touchPoint = points.points[0]
        minRect.set(touchPoint.x.toInt() - offset, touchPoint.y.toInt() - offset, (offset + touchPoint.x).toInt(), (offset + touchPoint.y).toInt())
    }

}