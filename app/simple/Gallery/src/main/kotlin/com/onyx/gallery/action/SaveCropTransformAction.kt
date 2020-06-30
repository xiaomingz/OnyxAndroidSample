package com.onyx.gallery.action

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.event.result.SaveCropTransformResultEvent
import com.onyx.gallery.request.SaveCropTransformRequest

/**
 * Created by Leung on 2020/6/29
 */
class SaveCropTransformAction(private val filePath: String) : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        val saveCropTransformRequest = SaveCropTransformRequest()
        globalEditBundle.enqueue(saveCropTransformRequest, object : RxCallback<SaveCropTransformRequest>() {
            override fun onNext(request: SaveCropTransformRequest) {
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