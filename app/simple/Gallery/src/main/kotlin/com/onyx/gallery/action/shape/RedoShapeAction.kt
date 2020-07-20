package com.onyx.gallery.action.shape

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.shape.RedoShapeRequest

/**
 * Created by Leung 2020/7/10 16:41
 **/
class RedoShapeAction : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        globalEditBundle.enqueue(RedoShapeRequest(), null)
    }

}