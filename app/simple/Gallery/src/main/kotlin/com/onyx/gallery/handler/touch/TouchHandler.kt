package com.onyx.gallery.handler.touch

import android.graphics.RectF
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList

/**
 * Created by Leung on 2020/5/19
 */
interface TouchHandler {
    fun onActivate()
    fun onDeactivate()
    fun onBeginRawDrawEvent(shortcutDrawing: Boolean, point: TouchPoint)
    fun onEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint)
    fun onRawDrawingPointsMoveReceived(touchPoint: TouchPoint)
    fun onRawDrawingTouchPointListReceived(touchPointList: TouchPointList)
    fun onTouchDown(touchPoint: TouchPoint)
    fun onTouchMove(touchPoint: TouchPoint)
    fun onTouchUp(touchPoint: TouchPoint)
    fun onBeginRawErasing(shortcutErasing: Boolean, point: TouchPoint)
    fun onRawErasingTouchPointMoveReceived(point: TouchPoint)
    fun onRawErasingTouchPointListReceived(pointList: TouchPointList)
    fun onEndRawErasing(outLimitRegion: Boolean, point: TouchPoint)
    fun onPenUpRefresh(refreshRect: RectF)
    fun undo()
    fun redo()
    fun canRawDrawingRenderEnabled(): Boolean
}