package com.onyx.gallery.handler.touch

import android.graphics.Matrix
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.gallery.action.shape.AddShapesAction
import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung on 2020/6/7
 */
class ScribbleTouchHandler(globalEditBundle: GlobalEditBundle) : BaseTouchHandler(globalEditBundle) {

    override fun onRawDrawingTouchPointListReceived(touchPointList: TouchPointList) {
        val normalTouchPointList = getNormalTouchPoint(touchPointList)
        val shape = createEraseShape(normalTouchPointList)
        addShape(shape)
    }

    private fun createEraseShape(touchPointList: TouchPointList): Shape {
        val shape = ShapeFactory.createShape(drawHandler.getCurrShapeType())
        shape.layoutType = ShapeFactory.LayoutType.FREE.ordinal
        shape.strokeWidth = drawHandler.getStrokeWidth()
        shape.color = drawHandler.getStrokeColor()
        shape.addPoints(touchPointList)
        return shape
    }

    private fun getNormalTouchPoint(touchPointList: TouchPointList): TouchPointList {
        val normalizedMatrix = Matrix()
        drawHandler.renderContext.matrix.invert(normalizedMatrix)
        val newTouchPointList = TouchPointList()
        touchPointList.points.forEach {
            val normalPoint = ShapeUtils.matrixTouchPoint(it, normalizedMatrix)
            newTouchPointList.add(normalPoint)
        }
        return newTouchPointList
    }

    private fun addShape(shape: Shape) {
        invertShapeStrokeWidth(shape)
        AddShapesAction().setShape(shape).execute(null)
    }

}