package com.onyx.gallery.handler.touch

import android.graphics.RectF
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.android.sdk.utils.RxTimerUtil
import com.onyx.gallery.action.shape.AddShapesInBackgroundAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.request.PartialRefreshRequest
import com.onyx.gallery.utils.ToastUtils

/**
 * Created by Leung on 2020/6/7
 */
class EpdShapeTouchHandler(globalEditBundle: GlobalEditBundle) : ErasableTouchHandler(globalEditBundle) {
    private var toastShowing = false

    private val toastHideTimerObserver: RxTimerUtil.TimerObserver = object : RxTimerUtil.TimerObserver() {
        override fun onNext(aLong: Long) {
            onHideToast()
        }
    }

    override fun onDeactivate() {
        super.onDeactivate()
        toastShowing = false
        RxTimerUtil.cancel(toastHideTimerObserver)
    }

    override fun onPenUpRefresh(refreshRect: RectF) {
        globalEditBundle.enqueue(PartialRefreshRequest(refreshRect), null)
    }

    override fun onRawDrawingTouchPointListReceived(touchPointList: TouchPointList) {
        val normalTouchPointList = getNormalTouchPointList(touchPointList)
        val shape = createEpdShape(normalTouchPointList)
        addShapeInBackground(shape)
    }

    private fun createEpdShape(touchPointList: TouchPointList): Shape {
        val shape = ShapeFactory.createShape(drawHandler.getCurrShapeType())
        shape.layoutType = ShapeFactory.LayoutType.FREE.ordinal
        shape.strokeWidth = drawHandler.getStrokeWidth()
        shape.color = drawHandler.getStrokeColor()
        shape.addPoints(touchPointList)
        return shape
    }

    private fun addShapeInBackground(shape: Shape) {
        invertRenderStrokeWidth(shape)
        AddShapesInBackgroundAction(mutableListOf(shape)).execute(null)
    }

    override fun onFloatButtonChanged(active: Boolean) {
        drawHandler.setRawDrawingEnabled(!active)
    }

    override fun onStatusBarChangedEvent(show: Boolean) {
        drawHandler.setRawDrawingEnabled(!show)
    }

    override fun onNoFocusSystemDialogChanged(open: Boolean) {
        drawHandler.setRawDrawingEnabled(!open)
    }

    override fun onShowToastEvent() {
        toastShowing = true
        RxTimerUtil.cancel(toastHideTimerObserver)
        drawHandler.setRawDrawingEnabled(false)
        RxTimerUtil.timer(ToastUtils.LONG_DELAY.toLong(), toastHideTimerObserver)
    }

    override fun onHideToastEvent() {
        RxTimerUtil.cancel(toastHideTimerObserver)
        onHideToast()
    }

    fun onHideToast() {
        toastShowing = false
        drawHandler.setRawDrawingEnabled(true)
    }

    override fun onActivityWindowFocusChanged(hasFocus: Boolean) {
        drawHandler.setRawDrawingEnabled(hasFocus)
    }

}