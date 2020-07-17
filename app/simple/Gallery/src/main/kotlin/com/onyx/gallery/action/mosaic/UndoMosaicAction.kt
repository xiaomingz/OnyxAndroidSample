package com.onyx.gallery.action.mosaic

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.mosaic.UndoMosaicRequest

/**
 * Created by Leung 2020/7/10 16:41
 **/
class UndoMosaicAction : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        globalEditBundle.enqueue(UndoMosaicRequest(), null)
    }

}