package com.onyx.gallery.handler

import android.content.Context
import android.graphics.*
import android.view.SurfaceView
import androidx.annotation.WorkerThread
import com.onyx.android.sdk.api.device.epd.EpdController
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.scribble.data.RenderColorConfig
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.Benchmark
import com.onyx.android.sdk.utils.CollectionUtils
import com.onyx.gallery.BuildConfig
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.utils.RendererUtils
import com.onyx.gallery.utils.SaveMosaicUtils

/**
 * Created by Leung on 2020/6/5
 */

enum class MirrorModel {
    LEFT, TOP, RIGHT, BOTTOM
}

class RenderHandler(val globalEditBundle: GlobalEditBundle) {
    val currMosaicPath = Path()
    private val pathPaint: Paint by lazy { initPathPaint() }
    private val mosaicPaint: Paint by lazy { initMosaicPaint() }
    val surfaceRect = Rect()

    private var mosaicBitmap: Bitmap? = null
    private val strokePaint: Paint by lazy { initStrokePaint() }
    val mosaicPathList = mutableListOf<Path>()

    var renderContext: RenderContext = RendererUtils.createRenderContext()
            .setEnableBitmapCache(true)
            .setRenderColorConfig(RenderColorConfig.RAW_RENDER_COLOR)

    fun initPathPaint(): Paint {
        val pathPaint = SaveMosaicUtils.getPathPaint(globalEditBundle.drawHandler)
        updateMosaicStrokeWidth(pathPaint)
        return pathPaint
    }

    private fun updateMosaicStrokeWidth(pathPaint: Paint) {
        val strokeWidth = globalEditBundle.drawHandler.getStrokeWidth()
        pathPaint.setStrokeWidth(strokeWidth)
    }

    fun initMosaicPaint(): Paint {
        return SaveMosaicUtils.getMosaicPaint()
    }

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

    fun addMosaicPath(path: Path) {
        mosaicPathList.add(path)
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
    fun renderToSurfaceView(surfaceView: SurfaceView) = renderToSurfaceViewImp(surfaceView) { canvas ->
        renderContext.scalingMatrix?.let { canvas.matrix = it }
        val rect = RendererUtils.checkSurfaceView(surfaceView)
        renderBackground(surfaceView.context, canvas, renderContext, rect)
        canvas.drawBitmap(renderContext.getBitmap(), 0f, 0f, null)
        renderMosaic(canvas)
        true
    }

    private fun renderMosaic(canvas: Canvas) {
        if (mosaicPathList.isEmpty() && currMosaicPath.isEmpty) {
            return
        }
        val benchmark = Benchmark()
        mosaicBitmap ?: updateMosaicBitmap()
        val imageSize = getImageSize()
        val left = (surfaceRect.width() - imageSize.width) / 2f
        val top = (surfaceRect.height() - imageSize.height) / 2f
        val layerCount = canvas.saveLayer(
                left,
                top,
                left + imageSize.width.toFloat(),
                top + imageSize.height.toFloat(),
                null,
                Canvas.ALL_SAVE_FLAG
        )
        updateMosaicStrokeWidth(pathPaint)
        for (mosaicPath in mosaicPathList) {
            canvas.drawPath(mosaicPath, pathPaint)
        }
        currMosaicPath?.let { canvas.drawPath(it, pathPaint) }
        canvas.drawBitmap(mosaicBitmap, left, top, mosaicPaint)
        canvas.restoreToCount(layerCount)
        if (BuildConfig.DEBUG) {
            benchmark.report(javaClass.simpleName + " -->> renderMosaic ")
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

    private fun updateMosaicBitmap() {
        if (mosaicPathList.isEmpty() && currMosaicPath.isEmpty) return
        mosaicBitmap = SaveMosaicUtils.getMosaicBitmap(renderContext.bitmap)
    }

    fun getImageSize(): Size {
        val bitmap = renderContext.bitmap
        return Size(bitmap.width, bitmap.height)
    }

    fun release() {
        currMosaicPath.set(Path())
        mosaicPathList.clear()
        mosaicBitmap?.let { it.recycle() }
        mosaicBitmap = null
        resetRenderContext()
    }

}

