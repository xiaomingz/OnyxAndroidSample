package com.onyx.gallery.handler

import android.content.Context
import android.graphics.*
import android.view.SurfaceView
import androidx.annotation.WorkerThread
import com.onyx.android.sdk.api.device.epd.EpdController
import com.onyx.android.sdk.scribble.data.RenderColorConfig
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.Benchmark
import com.onyx.android.sdk.utils.CollectionUtils
import com.onyx.gallery.BuildConfig
import com.onyx.gallery.utils.RendererUtils

/**
 * Created by Leung on 2020/6/5
 */

class RenderHandler {
    private val strokePaint: Paint by lazy { initStrokePaint() }

    var renderContext: RenderContext = RendererUtils.createRenderContext()
            .setEnableBitmapCache(true)
            .setRenderColorConfig(RenderColorConfig.RAW_RENDER_COLOR)

    fun resetRenderContext() {
        renderContext.reset()
        renderContext.matrix.reset()
    }

    fun eraseRendererBitmap() {
        renderContext.eraseBitmap()
    }

    fun resetRendererBitmap(rect: Rect) {
        recycleRendererBitmap()
        createRendererBitmap(rect)
    }

    fun createRendererBitmap(rect: Rect) {
        renderContext.createBitmap(rect)
        renderContext.updateCanvas()
    }

    private fun recycleRendererBitmap() {
        renderContext.recycleBitmap()
    }

    @WorkerThread
    fun renderToBitmap(shapes: List<Shape>) {
        val benchmark = Benchmark()
        for (shape in shapes) {
            shape.render(renderContext)
        }
        if (BuildConfig.DEBUG) {
            benchmark.report(javaClass.simpleName + " -->> renderToBitmap ")
        }
    }

    @WorkerThread
    fun renderVarietyShapesToSurfaceView(surfaceView: SurfaceView, shapes: List<Shape>): Boolean {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            return false
        }
        return renderToSurfaceViewImp(surfaceView) {
            val rect = RendererUtils.checkSurfaceView(surfaceView)
            renderBackground(surfaceView.context, it, renderContext, rect)
            it.drawBitmap(renderContext.bitmap, 0f, 0f, null)
            drawSelectionRect(it, renderContext)
            renderShapeToCanvas(shapes, it)
        }
    }

    private fun renderShapeToCanvas(shapes: List<Shape>, canvas: Canvas): Boolean {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            return false
        }
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        val renderContext = RenderContext.create(canvas, paint, null)
        for (shape in shapes) {
            shape.render(renderContext)
        }
        return true
    }

    @WorkerThread
    fun renderToSurfaceView(surfaceView: SurfaceView) = renderToSurfaceViewImp(surfaceView) {
        if (renderContext.scalingMatrix != null) {
            it.matrix = renderContext.getScalingMatrix()
        }
        val rect = RendererUtils.checkSurfaceView(surfaceView)
        renderBackground(surfaceView.context, it, renderContext, rect)
        it.drawBitmap(renderContext.getBitmap(), 0f, 0f, null)
        true
    }

    private fun renderBackground(context: Context,
                                 canvas: Canvas,
                                 renderContext: RenderContext,
                                 viewRect: Rect) {
        RendererUtils.clearBackground(canvas, Paint(), viewRect)
        val matrix = Matrix(renderContext.viewPortMatrix)
        if (renderContext.scalingMatrix != null) {
            matrix.postConcat(renderContext.scalingMatrix)
        }
        renderContext.drawBackGround(context, canvas, viewRect, matrix)
    }

    private inline fun renderToSurfaceViewImp(surfaceView: SurfaceView, block: (canvas: Canvas) -> Boolean): Boolean {
        val canvas = surfaceView.holder.lockCanvas()
        val benchmark = Benchmark()
        try {
            return block(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            beforeUnlockCanvas(surfaceView)
            surfaceView.holder.unlockCanvasAndPost(canvas)
            if (BuildConfig.DEBUG) {
                benchmark.report(javaClass.simpleName + " -->> renderToScreen ")
            }
        }
        return false
    }

    private fun beforeUnlockCanvas(surfaceView: SurfaceView) {
        EpdController.enablePost(surfaceView, 1)
    }

    private fun drawSelectionRect(canvas: Canvas, renderContext: RenderContext) {
        val selectionRect = renderContext.selectionRect ?: return
        val rectF = RectF(selectionRect.originRect)
        val path = Path()
        path.addRect(rectF, Path.Direction.CW)
        path.transform(renderContext.selectionRect.getMatrix())
        if (renderContext.matrix != null) {
            path.transform(renderContext.matrix)
        }
        canvas.drawPath(path, strokePaint)
    }

    private fun initStrokePaint(): Paint {
        val strokePaint = Paint()
        strokePaint.apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth = 2f
        }
        return strokePaint
    }

}

