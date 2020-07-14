package com.onyx.gallery.handler.touch

import android.graphics.Path
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.rx.SingleThreadScheduler
import com.onyx.gallery.action.AddMosaicPathAction
import com.onyx.gallery.action.RenderMosaicAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.ui.RedoMosaicEvent
import com.onyx.gallery.event.ui.UndoMosaicEvent
import com.onyx.gallery.request.EraseMosaicRequest
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import java.util.*

/**¬
 * Created by Leung on 2020/7/8
 */
class MosaicTouchHandler(globalEditBundle: GlobalEditBundle) : ErasableTouchHandler(globalEditBundle) {
    companion object {
        private const val TOUCH_POINT_BUFFER_MAX_COUNT = 30
    }

    private var path = Path()
    private var disposable: Disposable? = null
    private val actionDisposables: MutableList<Disposable> = ArrayList()
    private var drawEmitter: ObservableEmitter<TouchPoint?>? = null

    override fun onBeginRawDrawEvent(event: Boolean, point: TouchPoint) {
        path = Path()
        path.moveTo(point.x, point.y)
        disposable = Observable.create<TouchPoint> { e ->
            drawEmitter = e
            drawEmitter!!.onNext(point)
        }
                .buffer(TOUCH_POINT_BUFFER_MAX_COUNT)
                .observeOn(SingleThreadScheduler.scheduler())
                .subscribeOn(SingleThreadScheduler.scheduler())
                .subscribe { touchPoints ->
                    for (point in touchPoints) {
                        path.lineTo(point.x, point.y)
                    }
                    drawMosaic(path)
                }
    }

    override fun onRawDrawingPointsMoveReceived(point: TouchPoint) {
        drawEmitter?.run { onNext(point) }
    }

    override fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
        disposable?.run { dispose() }
        disposeAction()
        addMosaicPath(path)
        drawMosaic(path)
    }

    private fun addMosaicPath(path: Path) {
        AddMosaicPathAction(path).execute(null)
    }

    private fun drawMosaic(path: Path) {
        RenderMosaicAction(path).execute(null)
    }

    private fun disposeAction() {
        for (d in actionDisposables) {
            d.dispose()
        }
        actionDisposables.clear()
    }

    override fun undo() {
        postEvent(UndoMosaicEvent())
    }

    override fun redo() {
        postEvent(RedoMosaicEvent())
    }

    override fun handlerErasePoints(pointList: TouchPointList) {
        pointList.points.isEmpty() ?: return
        globalEditBundle.enqueue(EraseMosaicRequest(pointList), null)
    }

}


