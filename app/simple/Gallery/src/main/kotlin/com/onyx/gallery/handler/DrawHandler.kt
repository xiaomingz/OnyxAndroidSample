package com.onyx.gallery.handler

import android.content.Context
import android.graphics.*
import android.view.SurfaceView
import androidx.core.graphics.values
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
import com.onyx.gallery.models.CropSnapshot
import com.onyx.gallery.views.shape.ImageShapeExpand
import com.onyx.gallery.views.shape.MosaicShape
import org.greenrobot.eventbus.EventBus

/**
 * Created by Leung on 2020/6/5
 */
class DrawHandler(val context: Context, val globalEditBundle: GlobalEditBundle, val eventBus: EventBus) {
    var orgLimitRect = Rect()
    val currLimitRect = Rect()
    val surfaceRect = Rect()
    val drawingArgs = DrawArgs()
    private val undoRedoHander = globalEditBundle.undoRedoHandler
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

    fun updateLimitRect(rawDrawingEnabled: Boolean = true) {
        touchHelper?.run {
            val dstLimitRect = RectF()
            val srcLimitRect = RectF(orgLimitRect)
            readerHandler.renderContext.matrix.mapRect(dstLimitRect, srcLimitRect)
            val newLimit = Rect()
            dstLimitRect.round(newLimit)
            if (newLimit.intersect(surfaceRect)) {
                currLimitRect.set(newLimit)
                readerHandler.limitRect.set(currLimitRect)
                setRawDrawingEnabled(false)
                setLimitRect(listOf(currLimitRect))
                setRawDrawingEnabled(rawDrawingEnabled)
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

    fun partialRefreshScreen() {
        readerHandler.partialRefreshSurfaceView(surfaceView)
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
        undoRedoHander.cleardCropSnapshot()
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

    fun clearHandwritingData() {
        val allShapes = getAllShapes()
        val iterator = allShapes.iterator()
        while (iterator.hasNext()) {
            val shape = iterator.next()
            if (shape !is ImageShapeExpand && shape !is ImageShapeExpand) {
                iterator.remove()
            }
        }
    }

    fun getHandwritingShape(): MutableList<Shape> {
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

    fun undoShapes() {
        undoRedoHander.undoShapes()
    }

    fun redoShapes() {
        undoRedoHander.redoShapes()
    }

    private fun addCropSnapshot(cropSnapshot: CropSnapshot) {
        undoRedoHander.addCropSnapshot(cropSnapshot)
    }

    fun undoCrop() = undoRedoHander.undoCrop()

    fun redoCrop() = undoRedoHander.redoCrop()

    fun updateImageShape(imageShape: ImageShapeExpand) {
        val shapes = getAllShapes()
        shapes.forEachIndexed { index, shape ->
            if (shape is ImageShapeExpand || shape is ImageShape) {
                shapes.set(index, imageShape)
                return@forEachIndexed
            }
        }
    }

    fun invertRenderStrokeWidth(shape: Shape) {
        val matrix = Matrix()
        renderContext.matrix.invert(matrix)
        val scaleFactor = matrix.values()[Matrix.MSCALE_X]
        shape.strokeWidth *= scaleFactor
    }

    fun updateSaveCropSnapshotIndex() {
        undoRedoHander.updateSaveCropSnapshotIndex()
    }

    fun makeCropSnapshot(path: String, imageShape: ImageShapeExpand) {
        val cropSnapshot = CropSnapshot(
                globalEditBundle.initDx,
                globalEditBundle.initDy,
                globalEditBundle.initScaleFactor,
                path,
                orgLimitRect,
                currLimitRect,
                globalEditBundle.cropHandler.cropBoxRect,
                imageShape
        )
        addCropSnapshot(cropSnapshot)
    }

    fun saveHandwritingDataToCropSnapshot() {
        val cropSnapshot = undoRedoHander.getCurrCropSnapshot()
        val handwritingShape = getHandwritingShape()
        val matrix = globalEditBundle.getNormalizedMatrix()
        val scaleFactor = matrix.values()[Matrix.MSCALE_X]
        handwritingShape.forEach { shape ->
            shape.strokeWidth /= scaleFactor
        }
        cropSnapshot.handwritingShape.addAll(handwritingShape)
    }

    fun restoreCropSnapshot(cropSnapshot: CropSnapshot) {
        clearHandwritingData()
        cropSnapshot.run {
            globalEditBundle.filePath = imagePath
            globalEditBundle.initDx = initDx
            globalEditBundle.initDy = initDy
            globalEditBundle.initScaleFactor = initScaleFactor
            updateImageShape(imageShape)
            updateLimitRect(orgLimitRect)
            val restoreMosaicBitmap = readerHandler.restoreMosaicBitmap(imageShape)
            val matrix = globalEditBundle.getInitMatrix()
            val matrixValues = matrix.values()
            handwritingShape.forEach { shape ->
                shape.matrix.setScale(matrixValues[Matrix.MSCALE_X], matrixValues[Matrix.MSCALE_Y])
                shape.matrix.setTranslate(matrixValues[Matrix.MTRANS_X], matrixValues[Matrix.MTRANS_Y])
                shape.strokeWidth * matrixValues[Matrix.MSCALE_X]
                if (shape is MosaicShape) {
                    shape.mosaicBitmap = restoreMosaicBitmap
                }
            }
            addShapes(handwritingShape)
        }
        setRawDrawingRenderEnabled(false)
    }

    fun getMosaicBitmap(): Bitmap = readerHandler.getMosaicBitmap()

    fun hasMosaic(): Boolean {
        val handwritingShape = getHandwritingShape()
        handwritingShape.forEach { shape ->
            if (shape is MosaicShape) {
                return true
            }
        }
        return false
    }

    fun hasModify(): Boolean {
        return !getHandwritingShape().isEmpty()
    }

}

