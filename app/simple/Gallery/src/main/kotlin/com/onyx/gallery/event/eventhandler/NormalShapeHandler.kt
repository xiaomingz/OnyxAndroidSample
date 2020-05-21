package com.onyx.gallery.event.eventhandler

import android.graphics.Matrix
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.SingleThreadScheduler
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.gallery.action.shape.AddShapesAction
import com.onyx.gallery.action.shape.RenderVarietyShapeAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.common.BaseRequest
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by Leung on 2020/5/21
 */
class NormalShapeHandler(globalEditBundle: GlobalEditBundle) : BaseEventHandler(globalEditBundle) {

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
                .buffer(Companion.TOUCH_POINT_BUFFER_MAX_COUNT)
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
        if (drawEmitter != null) {
            drawEmitter!!.onNext(point)
        }
    }

    override fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
        if (disposable != null) {
            disposable!!.dispose()
        }
        drawEmitter!!.onNext(point)
        val renderShape = createShape(downPoint)
        renderShape.onUp(point, point)
        disposeAction()
        addShape(renderShape)
    }

    private fun addShape(renderShape: Shape) {
        val normalizedMatrix = Matrix()
        noteManager.renderContext.matrix.invert(normalizedMatrix)
        for (point in renderShape.points) {
            point.set(ShapeUtils.matrixTouchPoint(point, normalizedMatrix))
        }
        AddShapesAction().setShape(renderShape).execute(null)
    }

    private fun createShape(downTouchPoint: TouchPoint?): Shape {
        val shape: Shape = ShapeFactory.createShape(globalEditBundle.currShapeType)
        shape.layoutType = ShapeFactory.LayoutType.FREE.ordinal
        shape.onDown(downTouchPoint, downTouchPoint)
        return shape
    }

    private fun renderVarietyShape(shape: Shape) {
        RenderVarietyShapeAction().addShape(shape).setDisposableList(actionDisposables).execute(object : RxCallback<BaseRequest>() {
            override fun onSubscribe(d: Disposable) {
                actionDisposables.add(d)
            }

            override fun onNext(p0: BaseRequest) {
            }
        })
    }

    private fun disposeAction() {
        for (d in actionDisposables) {
            d.dispose()
        }
        actionDisposables.clear()
    }


}