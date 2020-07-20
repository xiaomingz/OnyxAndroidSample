package com.onyx.gallery.action.shape

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.shape.UndoShapeRequest

/**
 * Created by Leung 2020/7/10 16:41
 **/
class UndoShapeAction : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        globalEditBundle.enqueue(UndoShapeRequest(), null)
    }

}