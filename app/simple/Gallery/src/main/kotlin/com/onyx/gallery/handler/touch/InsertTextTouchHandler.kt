package com.onyx.gallery.handler.touch

import android.text.TextUtils
import androidx.annotation.NonNull
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.scribble.shape.EditTextShape
import com.onyx.gallery.action.StartTransformAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.raw.SelectionBundleEvent
import com.onyx.gallery.handler.InsertTextHandler
import com.onyx.gallery.request.textInput.HitTestTextShapeRequest
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/6/8
 */
class InsertTextTouchHandler(globalEditBundle: GlobalEditBundle) : ErasableTouchHandler(globalEditBundle) {
    private val insertTextHandler: InsertTextHandler by lazy { globalEditBundle.insertTextHandler }

    override fun onActivate() {
        super.onActivate()
        showInitInputEdit()
    }

    override fun onActivityWindowFocusChanged(hasFocus: Boolean) {
        if (!hasFocus) {
            insertTextHandler.textShape?.run {
                if (!TextUtils.isEmpty(text)) {
                    insertTextHandler.saveTextShape(this, false)
                }
            }
        }
    }

    private fun showInitInputEdit() {
        val limitRect = globalEditBundle.drawHandler.currLimitRect
        val point = TouchPoint(limitRect.centerX().toFloat(), limitRect.centerY().toFloat())
        hitTestTextShape(point)
    }

    override fun onTouchDown(touchPoint: TouchPoint) {
        super.onTouchDown(touchPoint)
        insertTextHandler.onTouchDown(touchPoint)
    }

    override fun onTouchMove(touchPoint: TouchPoint) {
        super.onTouchMove(touchPoint)
        insertTextHandler.onTouchMove(touchPoint)
    }

    override fun onTouchUp(touchPoint: TouchPoint) {
        super.onTouchUp(touchPoint)
        if (insertTextHandler.isUndefinedTransform()) {
            insertTextHandler.saveTextShape(false)
            hitTestTextShape(touchPoint)
        }
        insertTextHandler.onTouchUp(touchPoint)
    }

    private fun hitTestTextShape(point: TouchPoint) {
        val request = HitTestTextShapeRequest(drawHandler, point)
                .setInsertTextConfig(insertTextHandler.insertTextConfig)
                .setDrawingArgs(drawHandler.drawingArgs)
        globalEditBundle.enqueue(request, object : RxCallback<HitTestTextShapeRequest?>() {
            override fun onNext(@NonNull hitTestTextShapeRequest: HitTestTextShapeRequest) {
                startTransformAction(hitTestTextShapeRequest.hitTextShape as EditTextShape)
            }
        })
    }

    private fun startTransformAction(textShape: EditTextShape) {
        insertTextHandler.textShape = textShape as EditTextShape
        StartTransformAction(mutableListOf(textShape)).execute(null)
    }

    override fun onFloatButtonChanged(active: Boolean) {
        drawHandler.setRawInputReaderEnable(!active)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectionBundle(event: SelectionBundleEvent) {
        insertTextHandler.selectionRect = event.bundle.selectionRect
    }

    override fun undo() {
        super.undo()
        insertTextHandler.clear()
    }

    override fun redo() {
        super.redo()
        insertTextHandler.clear()
    }

}