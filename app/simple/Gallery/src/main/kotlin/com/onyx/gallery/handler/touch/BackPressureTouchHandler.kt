package com.onyx.gallery.handler.touch

import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.rx.SingleThreadScheduler
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.touch.TouchChangeEvent
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable

/**
 * Created by Leung 2020/8/26 14:35
 **/
abstract class BackPressureTouchHandler(editBundle: EditBundle) : BaseTouchHandler(editBundle) {
    companion object {
        private const val TOUCH_POINT_BUFFER_MAX_COUNT = 30
    }

    private var disposable: Disposable? = null
    private var drawEmitter: ObservableEmitter<TouchPoint?>? = null

    override fun onBeginRawDrawEvent(shortcutDrawing: Boolean, point: TouchPoint) {
        onTouchChange(true)
        onBeforeBeginRawDraw(shortcutDrawing, point)
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
                    onReceivedBufferPoint(pointList)
                }
    }

    override fun onRawDrawingPointsMoveReceived(point: TouchPoint) {
        drawEmitter?.run { onNext(point) }
    }

    override fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
        onTouchChange(false)
        drawEmitter?.onComplete()
        disposable?.run { dispose() }
        onAfterEndRawDrawing(outLimitRegion, point)
    }

    protected fun onTouchChange(isTouching: Boolean) {
        isTouch = isTouching
        postEvent(TouchChangeEvent(isTouching))
    }

    abstract fun onBeforeBeginRawDraw(shortcutDrawing: Boolean, point: TouchPoint)
    abstract fun onReceivedBufferPoint(pointList: TouchPointList)
    abstract fun onAfterEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint)

}