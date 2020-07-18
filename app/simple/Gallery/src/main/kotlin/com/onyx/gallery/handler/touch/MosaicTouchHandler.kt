package com.onyx.gallery.handler.touch

import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.rx.SingleThreadScheduler
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.gallery.action.shape.AddShapesInBackgroundAction
import com.onyx.gallery.action.shape.RenderVarietyShapeAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.utils.ExpandShapeFactory
import com.onyx.gallery.views.shape.MosaicShape
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable

/**¬
 * Created by Leung on 2020/7/8
 */
class MosaicTouchHandler(globalEditBundle: GlobalEditBundle) : ErasableTouchHandler(globalEditBundle) {
    companion object {
        private const val TOUCH_POINT_BUFFER_MAX_COUNT = 30
    }

    private lateinit var mosaicShape: MosaicShape
    private var disposable: Disposable? = null
    private var drawEmitter: ObservableEmitter<TouchPoint?>? = null

    override fun onBeginRawDrawEvent(event: Boolean, point: TouchPoint) {
        mosaicShape = createShape() as MosaicShape
        mosaicShape.mosaicBitmap = drawHandler.getMosaicBitmap()
        mosaicShape.imageSize = Size(drawHandler.surfaceRect.width(), drawHandler.surfaceRect.height())
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
                    mosaicShape.addPoints(pointList)
                    renderVarietyShape(mosaicShape)
                }
    }

    override fun onRawDrawingPointsMoveReceived(point: TouchPoint) {
        drawEmitter?.run { onNext(point) }
    }

    override fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
        disposable?.run { dispose() }
        addShapInBackground(mosaicShape)
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

    private fun addShapInBackground(shape: Shape) {
        invertRenderStrokeWidth(shape)
        AddShapesInBackgroundAction(mutableListOf(shape)).execute(null)
    }
}
