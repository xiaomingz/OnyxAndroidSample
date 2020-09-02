package com.onyx.gallery.action.shape

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.shape.RedoShapeRequest

/**
 * Created by Leung 2020/7/10 16:41
 **/
class RedoShapeAction(editBundle: EditBundle) : BaseEditAction<RxRequest>(editBundle) {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        editBundle.enqueue(RedoShapeRequest(editBundle), null)
    }

}