package com.onyx.gallery.handler

import android.content.Context
import android.graphics.*
import android.view.SurfaceView
import androidx.annotation.WorkerThread
import com.onyx.android.sdk.api.device.epd.EpdController
import com.onyx.android.sdk.api.device.epd.UpdateMode
import com.onyx.android.sdk.scribble.data.RenderColorConfig
import com.onyx.android.sdk.scribble.data.SelectionRect
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.Benchmark
import com.onyx.android.sdk.utils.CollectionUtils
import com.onyx.android.sdk.utils.RectUtils
import com.onyx.gallery.BuildConfig
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.utils.MosaicUtils
import com.onyx.gallery.utils.RendererUtils
import com.onyx.gallery.views.shape.ImageShapeExpand
import java.util.*

/**
 * Created by Leung on 2020/6/5
 */

enum class MirrorModel {
    LEFT, TOP, RIGHT, BOTTOM
}

class RenderHandler(val globalEditBundle: GlobalEditBundle) {
    val limitRect = Rect()
    val surfaceRect = Rect()
    private var mosaicBitmap: Bitmap? = null
    private val strokePaint: Paint by lazy { initStrokePaint() }
    private val fillPaint: Paint by lazy { initFillPaint() }

    var renderContext: RenderContext = RendererUtils.createRenderContext()
            .setEnableBitmapCache(true)
            .setRenderColorConfig(RenderColorConfig.RAW_RENDER_COLOR)

    fun resetRenderContext() {
        renderContext.selectionRect = null
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
    fun clearCanvas() {
        renderContext.canvas?.drawColor(Color.WHITE)
    }

    @WorkerThread
    fun refreshBitmap(shapes: MutableList<Shape>) {
        clearCanvas()
        renderToBitmap(shapes)
        updateMosaicBitmap()
    }

    @WorkerThread
    fun renderToBitmap(shapes: List<Shape>) {
        val benchmark = Benchmark()
        for (shape in shapes) {
            shape.render(renderContext)
        }
        updateMosaicBitmap()
        if (BuildConfig.DEBUG) {
            benchmark.report(javaClass.simpleName + " -->> renderToBitmap ")
        }
    }

    @WorkerThread
    fun renderVarietyShapesToSurfaceView(surfaceView: SurfaceView, shapes: List<Shape>): Boolean {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            return false
        }
        return renderToSurfaceViewImp(surfaceView) { canvas ->
            val rect = RendererUtils.checkSurfaceView(surfaceView)
            renderBackground(surfaceView.context, canvas, renderContext, rect)
            if (!limitRect.isEmpty) {
                canvas.clipRect(limitRect)
            }
            canvas.drawBitmap(renderContext.bitmap, 0f, 0f, null)
            drawSelectionRect(canvas, renderContext)
            renderShapeToCanvas(shapes, canvas)
            drawLimitRect(canvas)
            true
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
    fun renderToSurfaceView(surfaceView: SurfaceView) = renderToSurfaceViewImp(surfaceView) { canvas ->
        renderContext.scalingMatrix?.let { canvas.matrix = it }
        val rect = RendererUtils.checkSurfaceView(surfaceView)
        renderBackground(surfaceView.context, canvas, renderContext, rect)
        if (!limitRect.isEmpty) {
            canvas.clipRect(limitRect)
        }
        canvas.drawBitmap(renderContext.getBitmap(), 0f, 0f, null)
        drawLimitRect(canvas)
        true
    }

    @WorkerThread
    fun partialRefreshSurfaceView(surfaceView: SurfaceView) {
        val renderRect = RectUtils.toRect(renderContext.clipRect)
        val viewRect = RendererUtils.checkSurfaceView(surfaceView)
        EpdController.setViewDefaultUpdateMode(surfaceView, UpdateMode.HAND_WRITING_REPAINT_MODE)
        val canvas = surfaceView.holder.lockCanvas(renderRect) ?: return
        try {
            if (!limitRect.isEmpty) {
                canvas.clipRect(limitRect)
            }
            canvas.clipRect(renderRect)
            RendererUtils.renderBackground(surfaceView.context, canvas, renderContext, viewRect)
            canvas.drawBitmap(renderContext.getBitmap(), 0f, 0f, null)
            drawLimitRect(canvas)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            surfaceView.holder.unlockCanvasAndPost(canvas)
            EpdController.resetViewUpdateMode(surfaceView)
        }
    }

    @WorkerThread
    fun renderMirror(surfaceView: SurfaceView, limitRect: Rect, mirrorModel: MirrorModel) = renderToSurfaceViewImp(surfaceView) { canvas ->
        renderContext.scalingMatrix?.let { canvas.matrix = it }
        val rect = RendererUtils.checkSurfaceView(surfaceView)
        renderBackground(surfaceView.context, canvas, renderContext, rect)

        val matrix = Matrix()
        when (mirrorModel) {
            MirrorModel.LEFT,
            MirrorModel.TOP -> {
                matrix.postTranslate(0f, 0f);
                matrix.postScale(1f, 1f);
            }
            MirrorModel.RIGHT -> {
                val dx = limitRect.right.toFloat() + limitRect.left
                matrix.postTranslate(-dx, 0f);
                matrix.postScale(-1f, 1f)
            }
            MirrorModel.BOTTOM -> {
                val dy = limitRect.bottom.toFloat() + limitRect.top
                matrix.postTranslate(0f, -dy);
                matrix.postScale(1f, -1f)
            }
        }
        canvas.drawBitmap(renderContext.getBitmap(), matrix, Paint())
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

    private fun drawLimitRect(canvas: Canvas) {
        if (limitRect.isEmpty) {
            return
        }
        canvas.drawRect(limitRect, strokePaint)
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
        drawScalePoint(canvas, selectionRect)
    }

    private fun drawScalePoint(canvas: Canvas, selectionRect: SelectionRect) {
        val scalePointList = ArrayList<PointF>()
        val originRect = selectionRect.originRect
        if (!selectionRect.isTextSelection) {
            scalePointList.add(selectionRect.getRenderMatrixPoint(originRect.left, originRect.top))
            scalePointList.add(selectionRect.getRenderMatrixPoint(originRect.centerX(), originRect.top))
            scalePointList.add(selectionRect.getRenderMatrixPoint(originRect.right, originRect.top))
            scalePointList.add(selectionRect.getRenderMatrixPoint(originRect.left, originRect.bottom))
            scalePointList.add(selectionRect.getRenderMatrixPoint(originRect.centerX(), originRect.bottom))
            scalePointList.add(selectionRect.getRenderMatrixPoint(originRect.right, originRect.bottom))
        }
        scalePointList.add(selectionRect.getRenderMatrixPoint(originRect.left, originRect.centerY()))
        scalePointList.add(selectionRect.getRenderMatrixPoint(originRect.right, originRect.centerY()))
        for (pointF in scalePointList) {
            canvas.drawCircle(pointF.x, pointF.y, SelectionRect.SELECTION_CIRCLE_RADIUS, fillPaint)
            canvas.drawCircle(pointF.x, pointF.y, SelectionRect.SELECTION_CIRCLE_RADIUS, strokePaint)
        }
    }

    private fun initStrokePaint(): Paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2f
    }

    private fun initFillPaint(): Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    fun getMosaicBitmap(): Bitmap {
        mosaicBitmap ?: updateMosaicBitmap()
        return mosaicBitmap!!
    }

    fun restoreMosaicBitmap(imageShape: ImageShapeExpand): Bitmap {
        mosaicBitmap?.recycle()
        mosaicBitmap = MosaicUtils.getMosaicBitmap(imageShape.getImageBitmap())
        return mosaicBitmap!!
    }

    private fun updateMosaicBitmap() {
        mosaicBitmap = MosaicUtils.getMosaicBitmap(renderContext.bitmap)
    }

    fun release() {
        mosaicBitmap?.let { it.recycle() }
        mosaicBitmap = null
        resetRenderContext()
    }

}

