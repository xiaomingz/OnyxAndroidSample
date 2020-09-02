package com.onyx.gallery.action.shape

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.request.shape.AddShapesInBackgroundRequest

/**
 * Created by Leung 2020/7/17 16:48
 **/
class AddShapesInBackgroundAction(editBundle: EditBundle, val shapes: MutableList<Shape>) : BaseEditAction<RxRequest>(editBundle) {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        editBundle.enqueue(AddShapesInBackgroundRequest(editBundle, shapes), object : RxCallback<BaseRequest>() {
            override fun onNext(rxRequest: BaseRequest) {
                RxCallback.onNext(rxCallback, rxRequest)
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                RxCallback.onError(rxCallback, e)
            }

            override fun onFinally() {
                super.onFinally()
                RxCallback.onFinally(rxCallback)
            }
        })
    }

}