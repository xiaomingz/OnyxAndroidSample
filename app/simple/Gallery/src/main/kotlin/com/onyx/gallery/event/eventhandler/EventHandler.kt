package com.onyx.gallery.event.eventhandler

import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList

/**
 * Created by Leung on 2020/5/19
 */
interface EventHandler {
    fun onActivate()
    fun onDeactivate()
    fun onBeginRawDrawEvent(event: Boolean, point: TouchPoint)
    fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint)
    fun onRawDrawingPointsMoveReceived(touchPoint: TouchPoint)
    fun onRawDrawingTouchPointListReceived(touchPointList: TouchPointList)
}