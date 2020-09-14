package com.onyx.gallery.handler.touch

import android.graphics.Rect
import android.text.TextUtils
import androidx.annotation.NonNull
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.scribble.shape.EditTextShape
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.action.StartTransformAction
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.raw.SelectionBundleEvent
import com.onyx.gallery.event.ui.DismissFontSelectMenuEvent
import com.onyx.gallery.handler.InsertTextHandler
import com.onyx.gallery.request.textInput.HitTestTextShapeRequest
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/6/8
 */
class InsertTextTouchHandler(editBundle: EditBundle) : ErasableTouchHandler(editBundle) {
    private var hitTextShape: Shape? = null
    private val insertTextHandler: InsertTextHandler by lazy { editBundle.insertTextHandler }

    override fun onActivate() {
        super.onActivate()
        showInitInputEdit()
    }

    override fun onDeactivate() {
        super.onDeactivate()
        hitTextShape = null
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
        val orgImageSize = editBundle.orgImageSize
        val limitRect = Rect(0, 0, orgImageSize.width, orgImageSize.height)
        val point = TouchPoint(limitRect.centerX() / 2.toFloat(), limitRect.centerY().toFloat())
        hitTestTextShape(point)
    }

    override fun onTouchDown(touchPoint: TouchPoint) {
        super.onTouchDown(touchPoint)
        val touchPoint = drawHandler.getNormalTouchPoint(touchPoint)
        insertTextHandler.onTouchDown(touchPoint)
    }

    override fun onTouchMove(touchPoint: TouchPoint) {
        super.onTouchMove(touchPoint)
        val touchPoint = drawHandler.getNormalTouchPoint(touchPoint)
        insertTextHandler.onTouchMove(touchPoint)
    }

    override fun onTouchUp(touchPoint: TouchPoint) {
        super.onTouchUp(touchPoint)
        val touchPoint = drawHandler.getNormalTouchPoint(touchPoint)
        if (insertTextHandler.isUndefinedTransform()) {
            insertTextHandler.saveTextShape(false)
            hitTestTextShape(touchPoint)
        }
        insertTextHandler.onTouchUp(touchPoint)
    }

    private fun hitTestTextShape(point: TouchPoint) {
        val request = HitTestTextShapeRequest(editBundle, drawHandler, point)
                .setInsertTextConfig(insertTextHandler.insertTextConfig)
                .setDrawingArgs(drawHandler.drawingArgs)
        editBundle.enqueue(request, object : RxCallback<HitTestTextShapeRequest?>() {
            override fun onNext(@NonNull hitTestTextShapeRequest: HitTestTextShapeRequest) {
                hitTextShape = hitTestTextShapeRequest.hitTextShape
                startTransformAction(hitTextShape as EditTextShape)
            }
        })
    }

    private fun startTransformAction(textShape: EditTextShape) {
        insertTextHandler.textShape = textShape as EditTextShape
        StartTransformAction(editBundle, textShape).execute(null)
    }

    override fun onFloatButtonChanged(active: Boolean) {
        drawHandler.setRawInputReaderEnable(!active)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectionBundle(event: SelectionBundleEvent) {
        insertTextHandler.selectionRect = event.bundle.selectionRect
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDismissFontSelectMenuEvent(event: DismissFontSelectMenuEvent) {
        hitTextShape?.let {
            startTransformAction(it as EditTextShape)
        }
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