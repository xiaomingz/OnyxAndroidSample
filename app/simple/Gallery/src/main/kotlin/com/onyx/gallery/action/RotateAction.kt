package com.onyx.gallery.action

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.event.ui.StartRotateEvent
import com.onyx.gallery.request.transform.RotateRequest

/**
 * Created by Leung 2020/9/15 18:04
 **/
class RotateAction(editBundle: EditBundle, val currAngle: Float, val singleRotateAngle: Float) : BaseEditAction<RxRequest>(editBundle) {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        editBundle.enqueue(RotateRequest(editBundle, singleRotateAngle), object : RxCallback<RxRequest>() {
            override fun onNext(rxRequest: RxRequest) {
                editBundle.postEvent(StartRotateEvent(currAngle, cropHandler.getRotatedCropBox()))
            }
        })
    }

}