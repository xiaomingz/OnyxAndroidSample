package com.onyx.gallery.handler.touch

import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList

/**
 * Created by Leung on 2020/5/19
 */
interface TouchHandler {
    fun onActivate()
    fun onDeactivate()
    fun onBeginRawDrawEvent(event: Boolean, point: TouchPoint)
    fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint)
    fun onRawDrawingPointsMoveReceived(touchPoint: TouchPoint)
    fun onRawDrawingTouchPointListReceived(touchPointList: TouchPointList)
}