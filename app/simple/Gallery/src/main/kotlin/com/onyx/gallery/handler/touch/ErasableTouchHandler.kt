package com.onyx.gallery.handler.touch

import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.rx.ObservableHolder
import com.onyx.android.sdk.rx.SingleThreadScheduler
import com.onyx.gallery.action.erase.EraseAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.models.EraseArgs
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

/**
 * Created by Leung 2020/7/14 12:01
 **/
open class ErasableTouchHandler(globalEditBundle: GlobalEditBundle) : BaseTouchHandler(globalEditBundle) {

    companion object {
        const val ERASER_BUFFER = 100L
    }

    private var eraseObservable: ObservableHolder<TouchPoint>? = null

    override fun onBeginRawErasing(shortcutErasing: Boolean, point: TouchPoint) {
        removeEraseObserver()
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
        removeEraseObserver()
    }

    open fun onHandleErasePoints(pointList: TouchPointList) {
        erasingShape(pointList)
    }

    private fun erasingShape(pointList: TouchPointList) {
        if (pointList.points.isEmpty()) {
            return
        }
        val normalPointList = getNormalTouchPointList(pointList)
        val eraserWidth = drawHandler.drawingArgs.eraserWidth / 2
        val eraseArgs = EraseArgs(eraserWidth, touchPointList = normalPointList)
        EraseAction(eraseArgs).execute(null)
    }

    private fun removeEraseObserver() {
        eraseObservable?.dispose()
        eraseObservable = null
    }
}