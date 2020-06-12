package com.onyx.gallery.handler.touch

import android.graphics.Matrix
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.rx.SingleThreadScheduler
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.gallery.action.shape.AddShapesAction
import com.onyx.gallery.action.shape.RenderVarietyShapeAction
import com.onyx.gallery.bundle.GlobalEditBundle
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by Leung on 2020/6/7
 */
class GraphicsTouchHandler(globalEditBundle: GlobalEditBundle) : BaseTouchHandler(globalEditBundle) {

    companion object {
        private const val TOUCH_POINT_BUFFER_MAX_COUNT = 30
    }

    private var disposable: Disposable? = null
    private val actionDisposables: MutableList<Disposable> = ArrayList()
    private var drawEmitter: ObservableEmitter<TouchPoint?>? = null
    private var downPoint: TouchPoint? = null

    override fun onBeginRawDrawEvent(event: Boolean, point: TouchPoint) {
        downPoint = point
        disposable = Observable.create<TouchPoint> { e ->
            drawEmitter = e
            drawEmitter!!.onNext(point)
        }
                .buffer(TOUCH_POINT_BUFFER_MAX_COUNT)
                .observeOn(SingleThreadScheduler.scheduler())
                .subscribeOn(SingleThreadScheduler.scheduler())
                .subscribe { touchPoints ->
                    val shape = createShape(downPoint)
                    for (point in touchPoints) {
                        shape.onMove(point, point)
                    }
                    renderVarietyShape(shape)
                }
    }

    override fun onRawDrawingPointsMoveReceived(point: TouchPoint) {
        drawEmitter?.run { onNext(point) }
    }

    override fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
        disposable?.run { dispose() }
        drawEmitter?.onNext(point)

        normalMatrixMapPoint(downPoint!!, point)

        val renderShape = createShape(downPoint)
        renderShape.onUp(point, point)
        disposeAction()
        addShape(renderShape)
    }

    private fun normalMatrixMapPoint(downPoint: TouchPoint, upPoint: TouchPoint) {
        val normalizedMatrix = Matrix()
        drawHandler.renderContext.matrix.invert(normalizedMatrix)
        downPoint.set(ShapeUtils.matrixTouchPoint(downPoint, normalizedMatrix))
        upPoint.set(ShapeUtils.matrixTouchPoint(upPoint, normalizedMatrix))
    }

    private fun addShape(renderShape: Shape) {
        AddShapesAction().setShape(renderShape).execute(null)
    }

    private fun createShape(downTouchPoint: TouchPoint?): Shape {
        val shape: Shape = ShapeFactory.createShape(drawHandler.getCurrShapeType())
        shape.layoutType = ShapeFactory.LayoutType.FREE.ordinal
        shape.strokeWidth = drawHandler.getStrokeWidth()
        shape.color = drawHandler.getStrokeColor()
        shape.onDown(downTouchPoint, downTouchPoint)
        return shape
    }

    private fun renderVarietyShape(shape: Shape) {
        RenderVarietyShapeAction().addShape(shape).setDisposableList(actionDisposables).execute(null)
    }

    private fun disposeAction() {
        for (d in actionDisposables) {
            d.dispose()
        }
        actionDisposables.clear()
    }

}