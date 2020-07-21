package com.onyx.gallery.action.shape

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.shape.StrokeWidthChangeRequest

/**
 * Created by Leung 2020/7/21 17:50
 **/
class StrokeWidthChangeAction(val strokeWidth: Float) : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        globalEditBundle.enqueue(StrokeWidthChangeRequest(strokeWidth), null)
    }

}