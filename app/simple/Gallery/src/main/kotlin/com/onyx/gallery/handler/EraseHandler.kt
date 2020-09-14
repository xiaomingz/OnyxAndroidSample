package com.onyx.gallery.handler

import android.graphics.Path
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.ResManager
import com.onyx.gallery.R
import com.onyx.gallery.action.erase.EraseAction
import com.onyx.gallery.action.shape.AddShapesAction
import com.onyx.gallery.action.shape.RenderVarietyShapeAction
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.models.EraseArgs
import com.onyx.gallery.utils.ExpandShapeFactory
import com.onyx.gallery.views.shape.ImageTrackShape
import com.onyx.gallery.views.shape.ImageTrackType

/**
 * Created by Leung 2020/8/24 11:16
 **/

enum class EraseModel {
    MOVE, STROKES, REGION, LAYER
}

enum class EraseWidth(val width: Float) {
    ERASER_WIDTH_1(ResManager.getDimension(R.dimen.eraser_width_1)),
    ERASER_WIDTH_2(ResManager.getDimension(R.dimen.eraser_width_2)),
    ERASER_WIDTH_3(ResManager.getDimension(R.dimen.eraser_width_3)),
    ERASER_WIDTH_4(ResManager.getDimension(R.dimen.eraser_width_4)),
    ERASER_WIDTH_5(ResManager.getDimension(R.dimen.eraser_width_5))
}

class EraseHandler(val editBundle: EditBundle) {

    var eraseModel = EraseArgs.DEFAULT_MODEL
    var eraseWidth = EraseArgs.DEFAULT_WIDTH

    private var eraseShape: Shape? = null
    private val selectionPath = Path()

    private fun getDrawHandler(): DrawHandler = editBundle.drawHandler

    fun isEraseLayer(): Boolean = eraseModel == EraseModel.LAYER

    fun isEraseOnMove(): Boolean = eraseModel == EraseModel.MOVE

    fun isEraseByStrokes(): Boolean = eraseModel == EraseModel.STROKES

    fun isEraseByRegion(): Boolean = eraseModel == EraseModel.REGION

    fun onStartErase(shortcutDrawing: Boolean, point: TouchPoint) {
        if (isEraseOnMove() || isEraseByRegion()) {
            eraseShape = createEraseShape()
        }
        selectionPath.reset()
        selectionPath.moveTo(point.x, point.y)
    }

    fun onReceivedErasePoint(pointList: TouchPointList) {
        for (touchPoint in pointList) {
            selectionPath.lineTo(touchPoint.x, touchPoint.y)
        }
        if (eraseShape == null) {
            eraseStrokes(pointList)
        }
        eraseShape?.let {
            it.addPoints(pointList)
            renderVarietyEraseShape(it)
        }
        renderVarietyEraseRegion()
    }

    fun onEndErase(outLimitRegion: Boolean, point: TouchPoint) {
        eraseShape?.let { addShape(it) }
        eraseShape = null
    }

    private fun eraseStrokes(pointList: TouchPointList) {
        if (pointList.points.isEmpty()) {
            return
        }
        val normalPointList = getDrawHandler().getNormalTouchPointList(pointList)
        val eraserWidth = getDrawHandler().drawingArgs.eraserWidth / 2
        val eraseArgs = EraseArgs(eraserWidth, touchPointList = normalPointList)
        EraseAction(editBundle, eraseArgs).execute(null)
    }

    private fun createEraseShape(): Shape {
        val drawHandler = getDrawHandler()
        val shape = ExpandShapeFactory.createShape(ExpandShapeFactory.SHAPE_IMAGE_TRACK)
        if (isEraseByRegion()) {
            (shape as ImageTrackShape).imageTrackType = ImageTrackType.FILL
        }
        shape.strokeWidth = eraseWidth
        if (shape is ImageTrackShape) {
            shape.backgroundBitmap = drawHandler.getImageBitmap()
            shape.imageSize = Size(drawHandler.surfaceRect.width(), drawHandler.surfaceRect.height())
        }
        return shape
    }

    private fun renderVarietyEraseShape(shape: Shape) {
        if (isEraseOnMove()) {
            RenderVarietyShapeAction(editBundle).addShape(shape).execute(null)
        }
    }

    private fun renderVarietyEraseRegion() {
        if (isEraseByRegion()) {
            RenderVarietyShapeAction(editBundle).setSelectionPath(selectionPath).execute(null)
        }
    }

    private fun addShape(shape: Shape) {
        val pointList = shape.points
        val normalTouchPointList = getDrawHandler().getNormalTouchPointList(pointList)
        pointList.points = normalTouchPointList.points
        shape.updatePoints()
        AddShapesAction(editBundle).setShape(shape).execute(null)
    }

    fun release() {
        eraseModel = EraseArgs.DEFAULT_MODEL
        eraseWidth = EraseArgs.DEFAULT_WIDTH
    }

}