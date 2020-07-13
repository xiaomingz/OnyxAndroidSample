package com.onyx.gallery.handler

import android.content.Context
import android.graphics.*
import android.view.SurfaceView
import com.onyx.android.sdk.pen.TouchHelper
import com.onyx.android.sdk.scribble.data.SelectionBundle
import com.onyx.android.sdk.scribble.data.SelectionRect
import com.onyx.android.sdk.scribble.shape.ImageShape
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.CollectionUtils
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.raw.SelectionBundleEvent
import com.onyx.gallery.helpers.DrawArgs
import com.onyx.gallery.helpers.RawInputCallbackImp
import com.onyx.gallery.views.ImageShapeExpand
import org.greenrobot.eventbus.EventBus

/**
 * Created by Leung on 2020/6/5
 */
class DrawHandler(val context: Context, val globalEditBundle: GlobalEditBundle, val eventBus: EventBus) {
    var orgLimitRect = Rect()
    val currLimitRect = Rect()
    val surfaceRect = Rect()
    val drawingArgs = DrawArgs()
    private val undoRedoHander = globalEditBundle.undoRedoHander
    private var readerHandler = RenderHandler(globalEditBundle)
    val renderContext = readerHandler.renderContext

    private lateinit var surfaceView: SurfaceView
    private var rawInputCallback = RawInputCallbackImp(eventBus)
    var touchHelper: TouchHelper? = null

    fun attachHostView(hostView: SurfaceView) {
        checkSizeIsZero(hostView)
        surfaceView = hostView
        touchHelper = if (touchHelper == null) {
            TouchHelper.create(surfaceView, rawInputCallback)
        } else {
            touchHelper!!.bindHostView(surfaceView, rawInputCallback)
        }
        touchHelper?.run {
            bindHostView(surfaceView, rawInputCallback)
            openRawDrawing()
            setRawDrawingEnabled(false)
            initDrawArgs()
        }
        surfaceView.run {
            getLocalVisibleRect(surfaceRect)
            readerHandler.surfaceRect.set(surfaceRect)
            readerHandler.createRendererBitmap(Rect(0, 0, width, height))
        }
    }

    private fun checkSizeIsZero(surfaceView: SurfaceView) {
        check(!(surfaceView.width == 0 && surfaceView.height == 0)) { "can not start when view width and height is 0" }
    }

    private fun initDrawArgs() {
        setStrokeColor(DrawArgs.defaultStrokeColor)
        setStrokeWidth(DrawArgs.defaultStrokeWidth.toFloat())
    }

    fun updateLimitRect(newLimitRect: Rect) {
        orgLimitRect.set(newLimitRect)
        currLimitRect.set(newLimitRect)
        updateLimitRect()
    }

    fun updateLimitRect() {
        touchHelper?.run {
            val dstLimitRect = RectF()
            val srcLimitRect = RectF(orgLimitRect)
            readerHandler.renderContext.matrix.mapRect(dstLimitRect, srcLimitRect)
            val newLimit = Rect()
            dstLimitRect.round(newLimit)
            if (newLimit.intersect(surfaceRect)) {
                currLimitRect.set(newLimit)
                setRawDrawingEnabled(false)
                setLimitRect(listOf(currLimitRect))
                setRawDrawingEnabled(true)
            }
        }
    }

    fun clearScreen() {
        readerHandler.clearCanvas()
    }

    fun rotateScreen(angle: Float, centerPoint: PointF) {
        readerHandler.renderContext.matrix.run {
            postRotate(angle, centerPoint.x, centerPoint.y)
        }
    }

    fun renderMirror(mirrorModel: MirrorModel) {
        readerHandler.renderMirror(surfaceView, currLimitRect, mirrorModel)
    }

    fun renderToBitmap(shape: Shape) {
        val shapes = mutableListOf(shape)
        renderToBitmap(shapes)
    }

    fun renderToBitmap(shapes: List<Shape>) {
        readerHandler.renderToBitmap(shapes)
    }

    fun renderToScreen() {
        readerHandler.renderToSurfaceView(surfaceView)
    }

    fun renderShapesToBitmap() {
        val shapes = getAllShapes()
        readerHandler.refreshBitmap(shapes)
    }

    fun renderVarietyShapesToScreen(shape: List<Shape>) {
        readerHandler.renderVarietyShapesToSurfaceView(surfaceView, shape)
    }

    fun release() {
        drawingArgs.reset()
        undoRedoHander.clearShapes()
        orgLimitRect.setEmpty()
        currLimitRect.setEmpty()
        touchHelper?.closeRawDrawing()
        readerHandler.release()
        touchHelper = null
    }

    fun addShape(shape: Shape) {
        undoRedoHander.addShape(shape)
    }

    fun addShapes(shapes: MutableList<Shape>) {
        undoRedoHander.addShapes(shapes)
    }

    fun getAllShapes(): MutableList<Shape> {
        return undoRedoHander.getShapes()
    }

    fun getHandwritingShape(): List<Shape> {
        val shapeList: MutableList<Shape> = ArrayList()
        for (shape in getAllShapes()) {
            if (shape is ImageShape || shape is ImageShapeExpand) {
                continue
            }
            shapeList.add(shape)
        }
        return shapeList
    }

    fun setRawDrawingEnabled(enable: Boolean) {
        touchHelper?.setRawDrawingEnabled(enable)
    }

    fun setRawDrawingRenderEnabled(enable: Boolean) {
        touchHelper?.isRawDrawingRenderEnabled = enable
    }

    fun setRawInputReaderEnable(enable: Boolean) {
        touchHelper?.setRawInputReaderEnable(enable)
    }

    fun setStrokeColor(color: Int) {
        drawingArgs.strokeColor = color
        touchHelper?.setStrokeColor(color)
    }

    fun getStrokeColor(): Int {
        return drawingArgs.strokeColor
    }

    fun setStrokeWidth(penWidth: Float) {
        drawingArgs.strokeWidth = penWidth.toInt()
        touchHelper?.setStrokeWidth(penWidth)
    }

    fun getStrokeWidth(): Float = drawingArgs.strokeWidth.toFloat()

    fun updateCurrShapeType(newShape: Int) {
        drawingArgs.currShapeType = newShape
    }

    fun getCurrShapeType(): Int = drawingArgs.currShapeType

    fun postSelectionBundle() {
        val bundle = SelectionBundle()
                .setSelectionRect(SelectionRect(renderContext.selectionRect))
        eventBus.post(SelectionBundleEvent(bundle))
    }

    fun clearSelectionRect() {
        renderContext.selectionRect = null
    }

    fun getImageBitmap(): Bitmap {
        val imageBitmap = getImageShape()?.getImageBitmap()
                ?: throw RuntimeException("imageBitmap must be not null")
        return imageBitmap
    }

    fun getImageShape(): ImageShapeExpand? {
        val shapes = getAllShapes()
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            return null
        }
        for (shape in shapes) {
            if (shape is ImageShapeExpand) {
                return shape
            }
        }
        return null
    }

    fun addMosaicPath(path: Path) {
        readerHandler.addMosaicPath(path)
    }

    fun setCurrMosaicPath(currPath: Path) {
        readerHandler.currMosaicPath.set(currPath)
    }

    fun getMosaicPathList(): MutableList<Path> {
        return readerHandler.getAllMosaicPath()
    }

    fun hasMosaic(): Boolean {
        return !readerHandler.getAllMosaicPath().isEmpty()
    }

    fun undoShapes() {
        undoRedoHander.undoShapes()
    }

    fun undoMosaic() {
        readerHandler.currMosaicPath.reset()
        undoRedoHander.undoMosaic()
    }

    fun redoShapes() {
        undoRedoHander.redoShapes()
    }

    fun redoMosaic() {
        val redoMosaic = undoRedoHander.redoMosaic()
        redoMosaic?.let { readerHandler.currMosaicPath.set(redoMosaic) }

    }
}

