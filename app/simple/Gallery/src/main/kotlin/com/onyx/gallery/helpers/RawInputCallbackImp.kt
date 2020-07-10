package com.onyx.gallery.helpers

import android.graphics.RectF
import com.onyx.android.sdk.pen.RawInputCallback
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.gallery.event.raw.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by Leung on 2020/6/5
 */
class RawInputCallbackImp(private val eventBus: EventBus) : RawInputCallback() {
    override fun onBeginRawDrawing(shortcutDrawing: Boolean, point: TouchPoint) {
        eventBus.post(BeginRawDrawEvent(shortcutDrawing, point))
    }

    override fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
        eventBus.post(EndRawDrawingEvent(outLimitRegion, point))
    }

    override fun onRawDrawingTouchPointMoveReceived(point: TouchPoint) {
        eventBus.post(RawDrawingPointsMoveReceivedEvent(point))
    }

    override fun onRawDrawingTouchPointListReceived(pointList: TouchPointList) {
        eventBus.post(RawDrawingPointsReceivedEvent(pointList))
    }

    override fun onBeginRawErasing(shortcutErasing: Boolean, point: TouchPoint) {
        eventBus.post(BeginRawErasingEvent(shortcutErasing, point))
    }

    override fun onEndRawErasing(outLimitRegion: Boolean, point: TouchPoint) {
        eventBus.post(EndRawErasingEvent(outLimitRegion, point))
    }

    override fun onRawErasingTouchPointMoveReceived(point: TouchPoint) {
        eventBus.post(RawErasingPointMoveEvent(point))
    }

    override fun onRawErasingTouchPointListReceived(pointList: TouchPointList) {
        eventBus.post(RawErasingPointsReceived(pointList))
    }

    override fun onPenUpRefresh(refreshRect: RectF) {
        eventBus.post(PenUpRefreshEvent(refreshRect))
    }

}