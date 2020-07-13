package com.onyx.gallery.handler.touch

import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.rx.ObservableHolder
import com.onyx.android.sdk.rx.SingleThreadScheduler
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.request.shape.EraseShapeRequest
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

/**
 * Created by Leung 2020/7/13 17:53
 **/
open class GraffitiTouchHandler(globalEditBundle: GlobalEditBundle) : BaseTouchHandler(globalEditBundle) {
    companion object {
        private const val ERASER_BUFFER = 100L
    }

    private var eraseObservable: ObservableHolder<TouchPoint>? = null

    override fun onBeginRawErasing(shortcutErasing: Boolean, point: TouchPoint) {
        removeEraseObserver()
        eraseObservable = ObservableHolder<TouchPoint>()
        eraseObservable!!.setDisposable(eraseObservable!!.observable.buffer(ERASER_BUFFER, TimeUnit.MILLISECONDS)
                .subscribeOn(SingleThreadScheduler.scheduler())
                .subscribe(Consumer<List<TouchPoint?>> { touchPoints ->
                    eraseObservable ?: return@Consumer
                    val pointList = TouchPointList()
                    for (touchPoint in touchPoints) {
                        pointList.add(touchPoint)
                    }
                    erasingShape(pointList)
                }))
                .onNext(point)
    }

    override fun onRawErasingTouchPointMoveReceived(point: TouchPoint) {
        eraseObservable?.onNext(point)
    }

    override fun onEndRawErasing(outLimitRegion: Boolean, point: TouchPoint) {
        removeEraseObserver()
    }

    private fun erasingShape(pointList: TouchPointList) {
        pointList.points.isEmpty() ?: return
        globalEditBundle.enqueue(EraseShapeRequest(pointList), null)
    }

    private fun removeEraseObserver() {
        eraseObservable?.dispose()
        eraseObservable = null
    }

}