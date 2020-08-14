package com.onyx.gallery.action.crop

import com.onyx.android.sdk.rx.RequestChain
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.event.result.SaveCropTransformResultEvent
import com.onyx.gallery.request.RestoreTransformRequest
import com.onyx.gallery.request.crop.SaveCropTransformRequest

/**
 * Created by Leung on 2020/6/29
 */
class SaveCropTransformAction(private val filePath: String) : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        val requestChain = object : RequestChain<BaseRequest>() {}
        requestChain.addRequest(SaveCropTransformRequest())
        requestChain.addRequest(RestoreTransformRequest(true))
        globalEditBundle.enqueue(requestChain, object : RxCallback<RxRequest>() {
            override fun onNext(request: RxRequest) {
                RxCallback.onNext(rxCallback, request)
                eventBus.post(SaveCropTransformResultEvent())
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                RxCallback.onError(rxCallback, e)
                eventBus.post(SaveCropTransformResultEvent(e))
            }
        })
    }

}