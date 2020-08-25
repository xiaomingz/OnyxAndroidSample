package com.onyx.gallery.handler.touch

import android.graphics.Matrix
import android.graphics.RectF
import androidx.annotation.CallSuper
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.raw.*
import com.onyx.gallery.event.touch.TouchDownEvent
import com.onyx.gallery.event.touch.TouchMoveEvent
import com.onyx.gallery.event.touch.TouchUpEvent
import com.onyx.gallery.event.ui.RedoShapeEvent
import com.onyx.gallery.event.ui.UndoShapeEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/6/7
 */
abstract class BaseTouchHandler(val globalEditBundle: GlobalEditBundle) : TouchHandler {
    private val eventBus by lazy { globalEditBundle.eventBus }
    protected val drawHandler by lazy { globalEditBundle.drawHandler }
    protected val eraseHandler by lazy { globalEditBundle.eraseHandler }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBeginRawErasingEvent(event: BeginRawErasingEvent) = onBeginRawErasing(event.shortcutErasing, event.point)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRawErasingPointMoveEvent(event: RawErasingPointMoveEvent) = onRawErasingTouchPointMoveReceived(event.touchPoint)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRawErasingPointsReceived(event: RawErasingPointsReceived) = onRawErasingTouchPointListReceived(event.touchPointList)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEndRawErasingEvent(event: EndRawErasingEvent) = onEndRawErasing(event.outLimitRegion, event.point)

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPenUpRefreshEvent(event: PenUpRefreshEvent) = onPenUpRefresh(event.refreshRect)

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

    override fun onEndRawErasing(outLimitRegion: Boolean, point: TouchPoint) {

    }

    override fun onRawErasingTouchPointMoveReceived(point: TouchPoint) {
    }

    override fun onRawErasingTouchPointListReceived(pointList: TouchPointList) {

    }

    override fun onBeginRawErasing(shortcutErasing: Boolean, point: TouchPoint) {

    }

    override fun onPenUpRefresh(refreshRect: RectF) {

    }

    fun invertRenderStrokeWidth(shape: Shape) {
        drawHandler.invertRenderStrokeWidth(shape)
    }

    override fun undo() {
        postEvent(UndoShapeEvent())
    }

    override fun redo() {
        postEvent(RedoShapeEvent())
    }

    override fun canDrawErase(): Boolean = false

    override fun canRawInputReaderEnable(): Boolean = true

    override fun canRawDrawingRenderEnabled(): Boolean = false

    override fun onFloatButtonChanged(active: Boolean) {

    }

    override fun onStatusBarChangedEvent(show: Boolean) {

    }

    override fun onNoFocusSystemDialogChanged(open: Boolean) {

    }

    override fun onHideToastEvent() {

    }

    override fun onShowToastEvent() {

    }

    override fun onActivityWindowFocusChanged(hasFocus: Boolean) {

    }

    protected fun getNormalTouchPointList(touchPointList: TouchPointList): TouchPointList {
        val normalizedMatrix = Matrix()
        drawHandler.renderContext.matrix.invert(normalizedMatrix)
        val newTouchPointList = TouchPointList()
        touchPointList.points.forEach {
            val normalPoint = ShapeUtils.matrixTouchPoint(it, normalizedMatrix)
            newTouchPointList.add(normalPoint)
        }
        return newTouchPointList
    }

}
