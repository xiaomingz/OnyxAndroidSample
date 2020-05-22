package com.onyx.gallery.event.eventhandler

import android.graphics.Matrix
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.android.sdk.scribble.shape.ShapeFactory.LayoutType
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.gallery.action.shape.AddShapesAction
import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung on 2020/5/19
 */
class PenEventHandler(globalEditBundle: GlobalEditBundle) : BaseEventHandler(globalEditBundle) {

    override fun onRawDrawingTouchPointListReceived(touchPointList: TouchPointList) {
        var normalTouchPointList = getNormalTouchPoint(touchPointList)
        val shape = createEraseShape(normalTouchPointList)
        AddShapesAction().setShape(shape).execute(null)
    }

    private fun createEraseShape(touchPointList: TouchPointList): Shape {
        val shape = ShapeFactory.createShape(globalEditBundle.currShapeType)
        shape.layoutType = LayoutType.FREE.ordinal
        shape.strokeWidth = noteManager.strokeWidth
        shape.color = noteManager.strokeColor
        shape.addPoints(touchPointList)
        return shape
    }

    private fun getNormalTouchPoint(touchPointList: TouchPointList): TouchPointList {
        val normalizedMatrix = Matrix()
        noteManager.renderContext.matrix.invert(normalizedMatrix)
        val newTouchPointList = TouchPointList()
        touchPointList.points.forEach {
            val normalPoint = ShapeUtils.matrixTouchPoint(it, normalizedMatrix)
            newTouchPointList.add(normalPoint)
        }
        return newTouchPointList
    }

}