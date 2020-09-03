package com.onyx.gallery.action.shape

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.shape.UpdateCurrentShapeTypeRequest

/**
 * Created by Leung 2020/7/17 18:05
 **/
class ShapeChangeAction(editBundle: EditBundle, var shapeType: Int = ShapeFactory.SHAPE_BRUSH_SCRIBBLE) : BaseEditAction<RxRequest>(editBundle) {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        editBundle.enqueue(UpdateCurrentShapeTypeRequest(editBundle, shapeType), object : RxCallback<RxRequest>() {
            override fun onNext(request: RxRequest) {
                RxCallback.onNext(rxCallback, request)
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                RxCallback.onError(rxCallback, e)
            }
        })
    }
}