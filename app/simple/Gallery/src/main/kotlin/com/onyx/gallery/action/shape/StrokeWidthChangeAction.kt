package com.onyx.gallery.action.shape

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.shape.StrokeWidthChangeRequest

/**
 * Created by Leung 2020/7/21 17:50
 **/
class StrokeWidthChangeAction(editBundle: EditBundle, val strokeWidth: Float) : BaseEditAction<RxRequest>(editBundle) {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        editBundle.enqueue(StrokeWidthChangeRequest(editBundle, strokeWidth), null)
    }

}