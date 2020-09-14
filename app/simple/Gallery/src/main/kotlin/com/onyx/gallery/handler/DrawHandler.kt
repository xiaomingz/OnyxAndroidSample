package com.onyx.gallery.handler

import android.content.Context
import android.graphics.*
import android.view.SurfaceView
import androidx.core.graphics.values
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.pen.TouchHelper
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.data.SelectionBundle
import com.onyx.android.sdk.scribble.data.SelectionRect
import com.onyx.android.sdk.scribble.shape.ImageShape
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.android.sdk.utils.BitmapUtils
import com.onyx.android.sdk.utils.CollectionUtils
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.raw.SelectionBundleEvent
import com.onyx.gallery.helpers.DrawArgs
import com.onyx.gallery.helpers.RawInputCallbackImp
import com.onyx.gallery.models.CropSnapshot
import com.onyx.gallery.utils.ExpandShapeFactory
import com.onyx.gallery.views.shape.ImageShapeExpand
import com.onyx.gallery.views.shape.ImageTrackShape
import com.onyx.gallery.views.shape.MosaicShape
import org.greenrobot.eventbus.EventBus

/**
 * Created by Leung on 2020/6/5
 */
class DrawHandler(val context: Context, val editBundle: EditBundle, val eventBus: EventBus) {
    var isSurfaceCreated = false
    var orgLimitRect = Rect()
    var currLimitRect = Rect()
    internal val lastZoomLimitRect = Rect()
    val surfaceRect = Rect()
    val drawingArgs = DrawArgs()
    private var imageBitmap: Bitmap? = null
    private val undoRedoHander = editBundle.undoRedoHandler
    var readerHandler = RenderHandler(editBundle)
    val renderContext = readerHandler.renderContext

    private lateinit var surfaceView: SurfaceView
    private var rawInputCallback = RawInputCallbackImp(eventBus)

    @Volatile
    var touchHelper: TouchHelper? = null

    fun attachHostView(hostView: SurfaceView, renderBitmapSize: Size) {
        checkSizeIsZero(hostView)
        surfaceView = hostView
        touchHelper = TouchHelper.create(surfaceView, rawInputCallback)
        touchHelper!!.run {
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

    private fun checkSurfaceReady(): Boolean {
        return isSurfaceCreated
    }

    private fun initDrawArgs() {
        setStrokeColor(DrawArgs.defaultStrokeColor)
        setStrokeWidth(DrawArgs.defaultStrokeWidth.toFloat())
    }

    fun getLastZoomLimitRect(): Rect {
        if (lastZoomLimitRect.isEmpty) {
            lastZoomLimitRect.set(currLimitRect)
        }
        return lastZoomLimitRect;
    }

    fun updateLimitRect(newLimitRect: Rect) {
        orgLimitRect.set(newLimitRect)
        currLimitRect.set(newLimitRect)
        updateLimitRect()
        lastZoomLimitRect.set(newLimitRect)
    }

    fun zoomLimitRect(newLimitRect: Rect) {
        currLimitRect.set(newLimitRect)
        updateLimitRect()
        lastZoomLimitRect.set(newLimitRect)
    }

    fun updateLimitRect(rawDrawingEnabled: Boolean = true) {
        touchHelper?.run {
            val newLimit = Rect(currLimitRect)
            if (newLimit.intersect(surfaceRect)) {
                currLimitRect.set(newLimit)
                readerHandler.limitRect.set(currLimitRect)
                setRawDrawingEnabled(false)
                setLimitRect(listOf(currLimitRect))
                setRawDrawingEnabled(rawDrawingEnabled)
                readerHandler.limitRect.set(currLimitRect)
            }
            setExcludeRect(mutableListOf(Rect(0, 0, 0, 0)))
        }
    }

    fun clearScreen() {
        readerHandler.clearCanvas()
    }

    fun rotateScreen(angle: Float, centerPoint: PointF) {
        if (!checkSurfaceReady()) {
            return
        }
        readerHandler.renderContext.matrix.run {
            postRotate(angle, centerPoint.x, centerPoint.y)
        }
    }

    fun renderMirror(mirrorModel: MirrorModel) {
        if (!checkSurfaceReady()) {
            return
        }
        readerHandler.renderMirror(surfaceView, currLimitRect, mirrorModel)
    }

    fun renderToBitmap(shape: Shape) {
        if (!checkSurfaceReady()) {
            return
        }
        val shapes = mutableListOf(shape)
        renderToBitmap(shapes)
    }

    fun renderToBitmap(shapes: List<Shape>) {
        if (!checkSurfaceReady()) {
            return
        }
        readerHandler.renderToBitmap(shapes)
    }

    fun renderToScreen() {
        if (!checkSurfaceReady()) {
            return
        }
        readerHandler.renderToSurfaceView(surfaceView)
    }

    fun partialRefreshScreen() {
        if (!checkSurfaceReady()) {
            return
        }
        readerHandler.partialRefreshSurfaceView(surfaceView)
    }

    fun renderShapesToBitmap() {
        if (!checkSurfaceReady()) {
            return
        }
        val shapes = getAllShapes()
        readerHandler.refreshBitmap(shapes)
    }

    fun renderVarietyShapesToScreen(shape: List<Shape>) {
        if (!checkSurfaceReady()) {
            return
        }
        readerHandler.renderVarietyShapesToSurfaceView(surfaceView, shape)
    }

    fun renderVarietyShapesToScreen(shape: List<Shape>, renderShapeMatrix: Matrix) {
        if (!checkSurfaceReady()) {
            return
        }
        readerHandler.renderVarietyShapesToSurfaceView(surfaceView, shape, renderShapeMatrix)
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
        touchHelper?.setRawDrawingRenderEnabled(enable)
    }

    fun setRawInputReaderEnable(enable: Boolean) {
        touchHelper?.setRawInputReaderEnable(enable)
    }

    fun setStrokeStyle(strokeStyle: Int) {
        touchHelper?.setStrokeStyle(strokeStyle)
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

    fun getImageShapeBitmap(): Bitmap {
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

    fun makeCropSnapshot(path: String, imageShape: ImageShapeExpand) {
        val orgImageSize = editBundle.orgImageSize
        val renderImageSize = editBundle.renderImageSize
        val cropSnapshot = CropSnapshot(
                Size(orgImageSize.width, orgImageSize.height),
                Size(renderImageSize.width, renderImageSize.height),
                editBundle.offsetX,
                editBundle.offsetY,
                editBundle.initScaleFactor,
                path,
                Rect(orgLimitRect),
                Rect(currLimitRect),
                RectF(editBundle.cropHandler.cropBoxRect),
                editBundle.cropHandler.currAngle,
                imageShape,
                imageBitmap!!
        )
        addCropSnapshot(cropSnapshot)
    }

    fun saveHandwritingDataToCropSnapshot() {
        val cropSnapshot = undoRedoHander.getCurrCropSnapshot()
        val handwritingShape = ExpandShapeFactory.ShapeListClone(getHandwritingShape())
        cropSnapshot.handwritingShape.addAll(handwritingShape)
    }

    fun restoreCropSnapshot(cropSnapshot: CropSnapshot) {
        clearHandwritingData()
        cropSnapshot.run {
            editBundle.orgImageSize = Size(orgImageSize.width, orgImageSize.height)
            editBundle.renderImageSize = Size(renderImageSize.width, renderImageSize.height)
            editBundle.imagePath = imagePath
            editBundle.offsetX = offsetX
            editBundle.offsetY = offsetY
            editBundle.initScaleFactor = initScaleFactor
            updateImageShape(imageShape)
            updateLimitRect(orgLimitRect)
            editBundle.cropHandler.currAngle = rotateAngle
            this@DrawHandler.imageBitmap = imageBitmap
            val restoreMosaicBitmap = readerHandler.restoreMosaicBitmap(imageShape)
            handwritingShape.forEach { shape ->
                if (shape is MosaicShape) {
                    shape.backgroundBitmap = restoreMosaicBitmap
                } else if (shape is ImageTrackShape) {
                    shape.backgroundBitmap = imageBitmap
                }
            }
            addShapes(handwritingShape)
            setRenderContextMatrix(getInitMatrix())
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
        return !getHandwritingShape().isEmpty() || undoRedoHander.hasCropModify()
    }

    fun resetEditState() {
        clearSelectionRect()
        undoRedoHander.clearShapes()
        undoRedoHander.cleardCropSnapshot()
        editBundle.cropHandler.resetCropState()
        editBundle.insertTextHandler.clearTextShape()
    }

    fun getSurfaceSize(): Size {
        return Size(surfaceRect.width(), surfaceRect.height())
    }

    fun afterCreateImageShape() {
        val renderImageSize = editBundle.renderImageSize
        if (renderContext.bitmap != null) {
            imageBitmap = Bitmap.createBitmap(renderContext.bitmap)
            return
        }
        imageBitmap = BitmapUtils.decodeBitmap(editBundle.imagePath, renderImageSize.width, renderImageSize.height)
    }

    fun getImageBitmap(): Bitmap = imageBitmap!!

    fun updateSelectionPath(path: Path) {
        readerHandler.updateSelectionPath(path)
    }

    fun getNormalTouchPoint(touchPoint: TouchPoint): TouchPoint {
        val normalizedMatrix = getNormalizedMatrix()
        val normalPoint = ShapeUtils.matrixTouchPoint(touchPoint, normalizedMatrix)
        return normalPoint
    }

    fun getInitMatrixTouchPoint(touchPoint: TouchPoint): TouchPoint {
        return ShapeUtils.matrixTouchPoint(touchPoint, getInitMatrix())
    }

    fun getNormalTouchPointList(touchPointList: TouchPointList): TouchPointList {
        val normalizedMatrix = getNormalizedMatrix()
        val newTouchPointList = TouchPointList()
        touchPointList.points.forEach {
            val normalPoint = ShapeUtils.matrixTouchPoint(it, normalizedMatrix)
            newTouchPointList.add(normalPoint)
        }
        return newTouchPointList
    }

    fun getInitMatrix(): Matrix {
        return editBundle.getInitMatrix()
    }

    fun getNormalizedMatrix(): Matrix {
        val normalizedMatrix = Matrix()
        val matrix = Matrix(renderContext.matrix)
        matrix.invert(normalizedMatrix)
        return normalizedMatrix
    }

    fun getRenderContextScale(): Float {
        return renderContext.matrix.values()[Matrix.MSCALE_X]
    }

    fun getNormalizedScale(): Float {
        return getNormalizedMatrix().values()[Matrix.MSCALE_X]
    }

    fun getRenderViewPortScale(): Float {
        return renderContext.viewPortScale
    }

    fun resetRenderContextMatrix() {
        renderContext.matrix.reset()
    }

    fun setRenderContextMatrix(matrix: Matrix) {
        resetRenderContextMatrix()
        renderContext.matrix = Matrix(matrix)
    }

}

