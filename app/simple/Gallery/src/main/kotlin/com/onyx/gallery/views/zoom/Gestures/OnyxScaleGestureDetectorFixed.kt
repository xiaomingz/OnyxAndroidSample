package com.onyx.gallery.views.zoom.Gestures

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector

/**
 * Created by Leung 2020/8/10 19:37
 **/
class OnyxScaleGestureDetectorFixed (context: Context, listener: ScaleGestureDetector.OnScaleGestureListener) : ScaleGestureDetector(context, listener) {
    private var currY = 0f
    private var prevY = 0f

    private fun getIsInDoubleTapMode() = isQuickScaleEnabled && currentSpan == currentSpanY

    init {
        warmUpScaleDetector()
    }

    private fun warmUpScaleDetector() {
        val time = System.currentTimeMillis()
        val event = MotionEvent.obtain(time, time, MotionEvent.ACTION_CANCEL, 0f, 0f, 0)
        onTouchEvent(event)
        event.recycle()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val result = super.onTouchEvent(event)

        prevY = currY
        currY = event.y

        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            prevY = event.y
        }

        return result
    }

    override fun getScaleFactor(): Float {
        val factor = super.getScaleFactor()

        return if (getIsInDoubleTapMode()) {
            if (currY > prevY && factor > 1f || currY < prevY && factor < 1f) Math.max(0.8f, Math.min(factor, 1.25f)) else 1f
        } else {
            factor
        }
    }
}
