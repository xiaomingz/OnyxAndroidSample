package com.onyx.gallery.action.crop

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.crop.UndoCropRequest

/**
 * Created by Leung 2020/7/16 15:26
 **/
class UndoCropAction : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        globalEditBundle.enqueue(UndoCropRequest(), object : RxCallback<RxRequest>() {
            override fun onNext(request: RxRequest) {
                RxCallback.onNext(rxCallback, request)
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                RxCallback.onError(rxCallback, e)
            }
        })
    }

}