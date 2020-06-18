package com.onyx.gallery.action.textInput

import com.onyx.android.sdk.rx.RequestChain
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.android.sdk.scribble.data.SelectionRect
import com.onyx.android.sdk.scribble.shape.EditTextShape
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.request.textInput.RenderInputTextShapeRequest
import com.onyx.gallery.request.textInput.UpdateSelectionRectRequest

/**
 * Created by Leung on 2020/6/18
 */
class TextIndentationAction(private val textShape: EditTextShape, private val cursorShape: Shape, val cursorOffset: Int) : BaseEditAction<RxRequest>() {

    override fun execute(callback: RxCallback<RxRequest>?) {

        val updateSelectionRectRequest = UpdateSelectionRectRequest(this.textShape)
        var createCursorShapeByOffsetAction: CreateCursorShapeByOffsetAction? = null
        val renderInputTextShapeRequest = RenderInputTextShapeRequest(textShape)

        val requestChain = object : RequestChain<BaseRequest>() {
            override fun beforeExecute(request: RxRequest?) {
                super.beforeExecute(request)
                if (request is RenderInputTextShapeRequest) {
                    createCursorShapeByOffsetAction = CreateCursorShapeByOffsetAction()
                            .setCursorOffset(cursorOffset)
                            .setNormalizeScale(getNormalizeScale())
                            .setTextShape(textShape)
                            .setSelectionRect(getSelectionRect())
                    createCursorShapeByOffsetAction?.execute(null)
                    renderInputTextShapeRequest.setCursorShape(createCursorShapeByOffsetAction!!.cursorShape)
                }
            }
        }
        requestChain.addRequest(updateSelectionRectRequest)
        requestChain.addRequest(renderInputTextShapeRequest)

        globalEditBundle.enqueue(requestChain, object : RxCallback<RxRequest>() {
            override fun onNext(request: RxRequest) {
                callback?.onNext(request)
            }
        })
    }

    private fun getSelectionRect(): SelectionRect = globalEditBundle.drawHandler.renderContext.selectionRect

    private fun getNormalizeScale(): Float = globalEditBundle.drawHandler.drawingArgs.normalizeScale


}