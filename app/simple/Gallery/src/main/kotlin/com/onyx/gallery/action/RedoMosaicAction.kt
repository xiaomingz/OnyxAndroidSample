package com.onyx.gallery.action

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.RedoMosaicRequest

/**
 * Created by Leung 2020/7/10 16:41
 **/
class RedoMosaicAction : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        globalEditBundle.enqueue(RedoMosaicRequest(), null)
    }

}