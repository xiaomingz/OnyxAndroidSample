package com.onyx.gallery.handler.touch

import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.gallery.action.shape.AddShapesAction
import com.onyx.gallery.action.shape.RenderVarietyShapeAction
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.utils.ExpandShapeFactory
import com.onyx.gallery.views.shape.WaveLineShape

/**
 * Created by Leung on 2020/6/7
 * @description :create different graphics
 */
class NormalShapeTouchHandler(editBundle: EditBundle) : ErasableTouchHandler(editBundle) {

    private var downPoint: TouchPoint? = null

    override fun onBeforeBeginRawDraw(shortcutDrawing: Boolean, point: TouchPoint) {
        super.onBeforeBeginRawDraw(shortcutDrawing, point)
        downPoint = point
    }

    override fun onReceivedBufferPoint(pointList: TouchPointList) {
        super.onReceivedBufferPoint(pointList)
        val shape = createShape(downPoint)
        for (point in pointList) {
            point?.let { shape.onMove(it, it) }
        }
        renderVarietyShape(shape)
    }

    override fun onAfterEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
        super.onAfterEndRawDrawing(outLimitRegion, point)
        normalMatrixMapPoint(downPoint!!, point)

        val renderShape = createShape(downPoint)
        renderShape.onUp(point, point)
        addShape(renderShape)
    }

    private fun normalMatrixMapPoint(downPoint: TouchPoint, upPoint: TouchPoint) {
        val normalizedMatrix = drawHandler.getNormalizedMatrix()
        downPoint.set(ShapeUtils.matrixTouchPoint(downPoint, normalizedMatrix))
        upPoint.set(ShapeUtils.matrixTouchPoint(upPoint, normalizedMatrix))
    }

    private fun addShape(shape: Shape) {
        AddShapesAction(editBundle).setShape(shape).execute(null)
    }

    private fun createShape(downTouchPoint: TouchPoint?): Shape {
        val shape: Shape = ExpandShapeFactory.createShape(drawHandler.getCurrShapeType())
        shape.layoutType = ShapeFactory.LayoutType.FREE.ordinal
        shape.strokeWidth = drawHandler.getStrokeWidth()
        shape.color = drawHandler.getStrokeColor()
        shape.onDown(downTouchPoint, downTouchPoint)
        if (shape is WaveLineShape) {
            shape.limitRect.set(drawHandler.currLimitRect)
        }
        return shape
    }

    private fun renderVarietyShape(shape: Shape) {
        RenderVarietyShapeAction(editBundle).addShape(shape).execute(null)
    }

    override fun onFloatButtonChanged(active: Boolean) {
        drawHandler.setRawInputReaderEnable(!active)
    }

}