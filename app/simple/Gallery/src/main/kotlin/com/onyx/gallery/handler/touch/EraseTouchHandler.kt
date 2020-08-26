package com.onyx.gallery.handler.touch

import android.graphics.Path
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.action.erase.EraseAction
import com.onyx.gallery.action.shape.AddShapesAction
import com.onyx.gallery.action.shape.RenderVarietyShapeAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.models.EraseArgs
import com.onyx.gallery.utils.ExpandShapeFactory
import com.onyx.gallery.views.shape.ImageTrackShape
import com.onyx.gallery.views.shape.ImageTrackType

/**
 * Created by Leung 2020/8/24 11:12
 **/
class EraseTouchHandler(globalEditBundle: GlobalEditBundle) : BackPressureTouchHandler(globalEditBundle) {

    private var eraseShape: Shape? = null
    private val selectionPath = Path()

    override fun onBeforeBeginRawDraw(shortcutDrawing: Boolean, point: TouchPoint) {
        if (eraseHandler.isEraseOnMove() || eraseHandler.isEraseByRegion()) {
            eraseShape = createEraseShape()
        }
        selectionPath.reset()
        selectionPath.moveTo(point.x, point.y)
    }

    override fun onReceivedBufferPoint(pointList: TouchPointList) {
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

    override fun onAfterEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
        eraseShape?.let { addShape(it) }
        eraseShape = null
    }

    private fun eraseStrokes(pointList: TouchPointList) {
        if (pointList.points.isEmpty()) {
            return
        }
        val normalPointList = getNormalTouchPointList(pointList)
        val eraserWidth = drawHandler.drawingArgs.eraserWidth / 2
        val eraseArgs = EraseArgs(eraserWidth, touchPointList = normalPointList)
        EraseAction(eraseArgs).execute(null)
    }

    private fun createEraseShape(): Shape {
        val shape = ExpandShapeFactory.createShape(ExpandShapeFactory.SHAPE_IMAGE_TRACK)
        if (eraseHandler.isEraseByRegion()) {
            (shape as ImageTrackShape).imageTrackType = ImageTrackType.FILL
        }
        shape.strokeWidth = eraseHandler.eraseWidth
        if (shape is ImageTrackShape) {
            shape.backgroundBitmap = drawHandler.getImageBitmap()
            shape.imageSize = Size(drawHandler.surfaceRect.width(), drawHandler.surfaceRect.height())
        }
        return shape
    }

    private fun renderVarietyEraseShape(shape: Shape) {
        if (eraseHandler.isEraseOnMove()) {
            RenderVarietyShapeAction().addShape(shape).execute(null)
        }
    }

    private fun renderVarietyEraseRegion() {
        if (eraseHandler.isEraseByRegion()) {
            RenderVarietyShapeAction().setSelectionPath(selectionPath).execute(null)
        }
    }


    private fun addShape(shape: Shape) {
        invertRenderStrokeWidth(shape)
        AddShapesAction().setShape(shape).execute(null)
    }

}