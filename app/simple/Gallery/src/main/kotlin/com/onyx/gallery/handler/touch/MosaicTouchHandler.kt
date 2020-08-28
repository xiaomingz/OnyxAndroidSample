package com.onyx.gallery.handler.touch

import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.gallery.action.shape.AddShapesAction
import com.onyx.gallery.action.shape.RenderVarietyShapeAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.utils.ExpandShapeFactory
import com.onyx.gallery.views.shape.MosaicShape

/**
 * Created by Leung on 2020/7/8
 */
class MosaicTouchHandler(globalEditBundle: GlobalEditBundle) : BackPressureTouchHandler(globalEditBundle) {

    private lateinit var mosaicShape: MosaicShape

    override fun onBeforeBeginRawDraw(shortcutDrawing: Boolean, point: TouchPoint) {
        mosaicShape = createShape() as MosaicShape
        mosaicShape.backgroundBitmap = drawHandler.getMosaicBitmap()
        mosaicShape.imageSize = drawHandler.getSurfaceSize()
    }

    override fun onReceivedBufferPoint(pointList: TouchPointList) {
        mosaicShape.addPoints(pointList)
        renderVarietyShape(mosaicShape)
    }

    override fun onAfterEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
        addShape(mosaicShape)
    }

    private fun createShape(): Shape {
        return ExpandShapeFactory.createShape(drawHandler.getCurrShapeType()).apply {
            layoutType = ShapeFactory.LayoutType.FREE.ordinal
            strokeWidth = drawHandler.getStrokeWidth()
            color = drawHandler.getStrokeColor()
        }
    }

    private fun renderVarietyShape(shape: Shape) {
        RenderVarietyShapeAction().addShape(shape).execute(null)
    }

    private fun addShape(shape: Shape) {
        invertRenderStrokeWidth(shape)
        AddShapesAction().setShape(shape).execute(null)
    }

    override fun onFloatButtonChanged(active: Boolean) {
        drawHandler.setRawInputReaderEnable(!active)
    }


}
