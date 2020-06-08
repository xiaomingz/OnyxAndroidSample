package com.onyx.gallery.handler.touch

import androidx.annotation.CallSuper
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.raw.BeginRawDrawEvent
import com.onyx.gallery.event.raw.EndRawDrawingEvent
import com.onyx.gallery.event.raw.RawDrawingPointsMoveReceivedEvent
import com.onyx.gallery.event.raw.RawDrawingPointsReceivedEvent
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

    override fun onBeginRawDrawEvent(event: Boolean, point: TouchPoint) {

    }

    override fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {

    }

    override fun onRawDrawingPointsMoveReceived(touchPoint: TouchPoint) {

    }

    override fun onRawDrawingTouchPointListReceived(touchPointList: TouchPointList) {

    }
}