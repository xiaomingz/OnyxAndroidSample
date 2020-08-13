package com.onyx.gallery.handler.touch

import android.graphics.Matrix
import android.graphics.RectF
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.gallery.action.shape.AddShapesAction
import com.onyx.gallery.action.shape.AddShapesInBackgroundAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.request.PartialRefreshRequest

/**
 * Created by Leung on 2020/6/7
 */
class EpdShapeTouchHandler(globalEditBundle: GlobalEditBundle) : ErasableTouchHandler(globalEditBundle) {

    override fun onPenUpRefresh(refreshRect: RectF) {
        globalEditBundle.enqueue(PartialRefreshRequest(refreshRect), null)
    }

    override fun onRawDrawingTouchPointListReceived(touchPointList: TouchPointList) {
        val normalTouchPointList = getNormalTouchPointList(touchPointList)
        val shape = createEpdShape(normalTouchPointList)
        addShapeInBackground(shape)
    }

    private fun createEpdShape(touchPointList: TouchPointList): Shape {
        val shape = ShapeFactory.createShape(drawHandler.getCurrShapeType())
        shape.layoutType = ShapeFactory.LayoutType.FREE.ordinal
        shape.strokeWidth = drawHandler.getStrokeWidth()
        shape.color = drawHandler.getStrokeColor()
        shape.addPoints(touchPointList)
        return shape
    }

    private fun addShapeInBackground(shape: Shape) {
        invertRenderStrokeWidth(shape)
        AddShapesInBackgroundAction(mutableListOf(shape)).execute(null)
    }

    override fun onFloatButtonChanged(active: Boolean) {
        drawHandler.setRawDrawingEnabled(!active)
    }

}