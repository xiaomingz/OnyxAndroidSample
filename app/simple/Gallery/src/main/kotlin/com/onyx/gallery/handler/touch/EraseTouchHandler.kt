package com.onyx.gallery.handler.touch

import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung 2020/8/24 11:12
 **/
class EraseTouchHandler(globalEditBundle: GlobalEditBundle) : ErasableTouchHandler(globalEditBundle) {

    override fun onBeforeBeginRawDraw(shortcutDrawing: Boolean, point: TouchPoint) {
        eraseHandler.onStartErase(shortcutDrawing, point)
    }

    override fun onReceivedBufferPoint(pointList: TouchPointList) {
        eraseHandler.onReceivedErasePoint(pointList)
    }

    override fun onAfterEndRawDrawing(outLimitRegion: Boolean, point: TouchPoint) {
        eraseHandler.onEndErase(outLimitRegion, point)
    }

}