package com.onyx.gallery.handler.touch

import android.graphics.Matrix
import androidx.annotation.CallSuper
import androidx.core.graphics.values
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.raw.BeginRawDrawEvent
import com.onyx.gallery.event.raw.EndRawDrawingEvent
import com.onyx.gallery.event.raw.RawDrawingPointsMoveReceivedEvent
import com.onyx.gallery.event.raw.RawDrawingPointsReceivedEvent
import com.onyx.gallery.event.touch.TouchDownEvent
import com.onyx.gallery.event.touch.TouchMoveEvent
import com.onyx.gallery.event.touch.TouchUpEvent
import com.onyx.gallery.event.ui.RedoShapeEvent
import com.onyx.gallery.event.ui.UndoShapeEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/6/7
 */
abstract class BaseTouchHandler(val globalEditBundle: GlobalEditBundle) : TouchHandler {
    private val eventBus: EventBus = globalEditBundle.eventBus

    protected val drawHandler = globalEditBundle.drawHandler

    protected fun postEvent(event: Any) = eventBus.post(event)

    @CallSuper
    override fun onActivate() = EventBusUtils.ensureRegister(eventBus, this)

    @CallSuper
    override fun onDeactivate() = EventBusUtils.ensureUnregister(eventBus, this)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBeginRawDrawEvent(event: BeginRawDrawEvent) = onBeginRawDrawEvent(event.shortcutDrawing, event.point)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEndRawDrawingEvent(event: EndRawDrawingEvent) = onEndRawDrawing(event.outLimitRegion, event.point)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRawDrawingPointsMoveReceivedEvent(event: RawDrawingPointsMoveReceivedEvent) = onRawDrawingPointsMoveReceived(event.touchPoint)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRawDrawingTouchPointListReceivedEvent(event: RawDrawingPointsReceivedEvent) = onRawDrawingTouchPointListReceived(event.touchPointList)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTouchDownEvent(event: TouchDownEvent) {
        val motionEvent = event.motionEvent
        onTouchDown(TouchPoint(motionEvent.x, motionEvent.y))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTouchMoveEvent(event: TouchMoveEvent) {
        val motionEvent = event.motionEvent
        onTouchMove(TouchPoint(motionEvent.x, motionEvent.y))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTouchUpEvent(event: TouchUpEvent) {
        val motionEvent = event.motionEvent
        onTouchUp(TouchPoint(motionEvent.x, motionEvent.y))
    }

    override fun onBeginRawDrawEvent(event: Boolean, point: TouchPoint) {

    }

    override fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {

    }

    override fun onRawDrawingPointsMoveReceived(touchPoint: TouchPoint) {

    }

    override fun onRawDrawingTouchPointListReceived(touchPointList: TouchPointList) {

    }

    override fun onTouchDown(touchPoint: TouchPoint) {
    }

    override fun onTouchMove(touchPoint: TouchPoint) {
    }

    override fun onTouchUp(touchPoint: TouchPoint) {
    }

    fun invertShapeStrokeWidth(shape: Shape) {
        val matrix = Matrix()
        drawHandler.renderContext.matrix.invert(matrix)
        val scaleFactor = matrix.values()[Matrix.MSCALE_X]
        shape.strokeWidth *= scaleFactor
    }

    override fun undo() {
        postEvent(UndoShapeEvent())
    }

    override fun redo() {
        postEvent(RedoShapeEvent())
    }

}
