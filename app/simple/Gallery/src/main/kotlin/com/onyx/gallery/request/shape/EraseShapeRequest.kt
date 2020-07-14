package com.onyx.gallery.request.shape

import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import java.util.*

/**
 * Created by Leung 2020/7/13 16:47
 **/
class EraseShapeRequest(private val touchPointList: TouchPointList) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val drawRadius = drawHandler.drawingArgs.eraserWidth / 2
        val handwritingShape = drawHandler.getHandwritingShape()
        val removeShapes = removeShapes(handwritingShape, touchPointList, drawRadius)
        if (removeShapes.isEmpty()) {
            return
        }
        undoRedoHandler.eraseShapes(removeShapes)
        renderShapesToBitmap = true
        renderToScreen = true
    }

    fun removeShapes(handwritingShapes: List<Shape>, touchPointList: TouchPointList, radius: Float): ArrayList<Shape> {
        val shapes = ArrayList<Shape>()
        val hitShapes = ArrayList<Shape>()
        for (shape in handwritingShapes) {
            if (!shape.isVisible || !shape.isFreePosition) {
                continue
            }
            fastHitTest(touchPointList, radius, hitShapes, shape)
        }
        for (shape in hitShapes) {
            if (hitTestAndRemoveShape(shape, touchPointList, radius)) {
                shapes.add(shape)
            }
        }
        return shapes
    }

    private fun fastHitTest(touchPointList: TouchPointList, radius: Float, outputHitShapes: ArrayList<Shape>, shape: Shape) {
        for (touchPoint in touchPointList.points) {
            if (shape.hasSubShapeList()) {
                if (fastHitTestSubShapeList(radius, outputHitShapes, shape, touchPoint)) {
                    break
                }
            } else if (shape.fastHitTest(touchPoint.getX(), touchPoint.getY(), radius)) {
                outputHitShapes.add(shape)
                break
            }
        }
    }

    private fun fastHitTestSubShapeList(radius: Float, outputHitShapes: ArrayList<Shape>, shape: Shape, touchPoint: TouchPoint): Boolean {
        for (subShape in shape.subShapeList) {
            if (subShape.fastHitTest(touchPoint.getX(), touchPoint.getY(), radius)) {
                outputHitShapes.add(shape)
                return true
            }
        }
        return false
    }

    private fun hitTestAndRemoveShape(shape: Shape, touchPointList: TouchPointList, radius: Float): Boolean {
        for (touchPoint in touchPointList.points) {
            if (shape.hasSubShapeList()) {
                if (hitTestSubShapeListAndRemoveShape(shape, radius, touchPoint)) {
                    return true
                }
            } else if (shape.hitTest(touchPoint.getX(), touchPoint.getY(), radius)) {
                return true
            }
        }
        return false
    }

    private fun hitTestSubShapeListAndRemoveShape(shape: Shape, radius: Float, touchPoint: TouchPoint): Boolean {
        for (subShape in shape.subShapeList) {
            if (subShape.hitTest(touchPoint.getX(), touchPoint.getY(), radius)) {
                return true
            }
        }
        return false
    }

}