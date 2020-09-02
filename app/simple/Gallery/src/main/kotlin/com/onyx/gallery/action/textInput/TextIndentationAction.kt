package com.onyx.gallery.action.textInput

import com.onyx.android.sdk.rx.RequestChain
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.android.sdk.scribble.data.SelectionRect
import com.onyx.android.sdk.scribble.shape.EditTextShape
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.request.textInput.RenderInputTextShapeRequest
import com.onyx.gallery.request.textInput.UpdateSelectionRectRequest

/**
 * Created by Leung on 2020/6/18
 */
class TextIndentationAction(editBundle: EditBundle, private val textShape: EditTextShape, private var cursorShape: Shape?, val cursorOffset: Int) : BaseEditAction<RxRequest>(editBundle) {

    override fun execute(callback: RxCallback<RxRequest>?) {

        val updateSelectionRectRequest = UpdateSelectionRectRequest(editBundle, this.textShape)
        var createCursorShapeByOffsetAction: CreateCursorShapeByOffsetAction? = null
        val renderInputTextShapeRequest = RenderInputTextShapeRequest(editBundle, textShape)

        val requestChain = object : RequestChain<BaseRequest>() {
            override fun beforeExecute(request: RxRequest?) {
                super.beforeExecute(request)
                if (request is RenderInputTextShapeRequest) {
                    createCursorShapeByOffsetAction = CreateCursorShapeByOffsetAction(editBundle)
                            .setCursorOffset(cursorOffset)
                            .setNormalizeScale(getNormalizeScale())
                            .setTextShape(textShape)
                            .setSelectionRect(getSelectionRect())
                    createCursorShapeByOffsetAction?.execute(null)
                    cursorShape = createCursorShapeByOffsetAction!!.cursorShape
                    renderInputTextShapeRequest.setCursorShape(cursorShape)
                }
            }
        }
        requestChain.addRequest(updateSelectionRectRequest)
        requestChain.addRequest(renderInputTextShapeRequest)

        editBundle.enqueue(requestChain, object : RxCallback<RxRequest>() {
            override fun onNext(request: RxRequest) {
                callback?.onNext(request)
            }
        })
    }

    private fun getSelectionRect(): SelectionRect = editBundle.drawHandler.renderContext.selectionRect

    private fun getNormalizeScale(): Float = editBundle.drawHandler.drawingArgs.normalizeScale


}