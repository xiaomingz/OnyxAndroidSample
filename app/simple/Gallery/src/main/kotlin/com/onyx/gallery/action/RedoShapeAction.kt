package com.onyx.gallery.action

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.RedoShapeRequest
import com.onyx.gallery.request.UndoMosaicRequest

/**
 * Created by Leung 2020/7/10 16:41
 **/
class RedoShapeAction : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        globalEditBundle.enqueue(RedoShapeRequest(), null)
    }

}