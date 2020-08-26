package com.onyx.gallery.handler.touch

import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.rx.SingleThreadScheduler
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.action.erase.EraseAction
import com.onyx.gallery.action.shape.AddShapesAction
import com.onyx.gallery.action.shape.RenderVarietyShapeAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.models.EraseArgs
import com.onyx.gallery.utils.ExpandShapeFactory
import com.onyx.gallery.views.shape.ImageTrackShape
import com.onyx.gallery.views.shape.ImageTrackType
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable

/**
 * Created by Leung 2020/8/24 11:12
 **/
class EraseTouchHandler(globalEditBundle: GlobalEditBundle) : BaseTouchHandler(globalEditBundle) {

    companion object {
        private const val TOUCH_POINT_BUFFER_MAX_COUNT = 30
    }

    private var shape: Shape? = null
    private var disposable: Disposable? = null
    private var drawEmitter: ObservableEmitter<TouchPoint?>? = null

    override fun onBeginRawDrawEvent(event: Boolean, point: TouchPoint) {
        if (eraseHandler.isEraseOnMove() || eraseHandler.isEraseByRegion()) {
            shape = createEraseShape()
        }
        disposable = Observable.create<TouchPoint> { e ->
            drawEmitter = e
            drawEmitter!!.onNext(point)
        }
                .buffer(TOUCH_POINT_BUFFER_MAX_COUNT)
                .observeOn(SingleThreadScheduler.scheduler())
                .subscribeOn(SingleThreadScheduler.scheduler())
                .subscribe { touchPoints ->
                    val pointList = TouchPointList()
                    for (touchPoint in touchPoints) {
                        pointList.add(touchPoint)
                    }
                    if (shape == null) {
                        eraseStrokes(pointList)
                    }
                    shape?.let {
                        it.addPoints(pointList)
                        renderVarietyShape(it)
                    }
                }
    }

    override fun onRawDrawingPointsMoveReceived(point: TouchPoint) {
        drawEmitter?.run { onNext(point) }
    }

    override fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
        drawEmitter?.onComplete()
        disposable?.run { dispose() }
        shape?.let { addShape(it) }
        shape = null
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

    private fun renderVarietyShape(shape: Shape) {
        RenderVarietyShapeAction().addShape(shape).execute(null)
    }

    private fun addShape(shape: Shape) {
        invertRenderStrokeWidth(shape)
        AddShapesAction().setShape(shape).execute(null)
    }

}