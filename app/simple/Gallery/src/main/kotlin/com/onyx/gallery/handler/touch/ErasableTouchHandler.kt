package com.onyx.gallery.handler.touch

import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.rx.ObservableHolder
import com.onyx.android.sdk.rx.SingleThreadScheduler
import com.onyx.gallery.bundle.EditBundle
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

/**
 * Created by Leung 2020/7/14 12:01
 **/
open class ErasableTouchHandler(editBundle: EditBundle) : BackPressureTouchHandler(editBundle) {

    companion object {
        const val ERASER_BUFFER = 100L
    }

    private var eraseObservable: ObservableHolder<TouchPoint>? = null

    override fun onBeforeBeginRawDraw(shortcutDrawing: Boolean, point: TouchPoint) {
    }

    override fun onReceivedBufferPoint(pointList: TouchPointList) {
    }

    override fun onAfterEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
    }

    override fun onBeginRawErasing(shortcutErasing: Boolean, point: TouchPoint) {
        onTouchChange(true)
        removeEraseObserver()
        eraseHandler.onStartErase(shortcutErasing, point)
        eraseObservable = ObservableHolder<TouchPoint>().let {
            it.setDisposable(it.observable.buffer(ERASER_BUFFER, TimeUnit.MILLISECONDS)
                    .subscribeOn(SingleThreadScheduler.scheduler())
                    .subscribe(Consumer<List<TouchPoint>> { touchPoints ->
                        eraseObservable ?: return@Consumer
                        val pointList = TouchPointList()
                        for (touchPoint in touchPoints) {
                            pointList.add(touchPoint)
                        }
                        onHandleErasePoints(pointList)
                    }))
                    .onNext(point)
        }
    }

    override fun onRawErasingTouchPointMoveReceived(point: TouchPoint) {
        eraseObservable?.onNext(point)
    }

    override fun onEndRawErasing(outLimitRegion: Boolean, point: TouchPoint) {
        onTouchChange(false)
        removeEraseObserver()
        eraseHandler.onEndErase(outLimitRegion, point)
    }

    open fun onHandleErasePoints(pointList: TouchPointList) {
        eraseHandler.onReceivedErasePoint(pointList)
    }

    private fun removeEraseObserver() {
        eraseObservable?.dispose()
        eraseObservable = null
    }
}