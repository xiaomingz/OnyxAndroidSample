package com.onyx.gallery.handler.touch

import androidx.annotation.NonNull
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.scribble.shape.EditTextShape
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.StringUtils
import com.onyx.gallery.action.StartTransformAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.raw.SelectionBundleEvent
import com.onyx.gallery.handler.InsertTextHandler
import com.onyx.gallery.request.textInput.HitTestTextShapeRequest
import com.onyx.gallery.request.textInput.SaveTextShapesRequest
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/6/8
 */
class InsertTextTouchHandler(globalEditBundle: GlobalEditBundle) : BaseTouchHandler(globalEditBundle) {
    private val insertTextHandler: InsertTextHandler by lazy { globalEditBundle.insertTextHandler }

    override fun onActivate() {
        super.onActivate()
        showInitInputEdit()
    }

    private fun showInitInputEdit() {
        val limitRect = globalEditBundle.drawHandler.orgLimitRect
        val point = TouchPoint((limitRect.width() / 4).toFloat(), (limitRect.height() / 2).toFloat())
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
            saveTextShape(false)
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
        insertTextHandler.showSoftInput()
        StartTransformAction(mutableListOf(textShape)).execute(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectionBundle(event: SelectionBundleEvent) {
        insertTextHandler.selectionRect = event.bundle.selectionRect
    }

    private fun saveTextShape(clear: Boolean) {
        val textShape: Shape = insertTextHandler.textShape ?: return
        if (StringUtils.isNullOrEmpty(textShape.text)) {
            return
        }
        saveTextShape(textShape, clear)
    }

    private fun saveTextShape(textShape: Shape, clear: Boolean) {
        val request = SaveTextShapesRequest(mutableListOf(textShape))
        globalEditBundle.enqueue(request, object : RxCallback<SaveTextShapesRequest?>() {
            override fun onNext(@NonNull saveTextShapesRequest: SaveTextShapesRequest) {
                if (clear) {
                    insertTextHandler.clear()
                }
            }
        })
    }

}