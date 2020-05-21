package com.onyx.gallery.action

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.event.result.SaveEditPictureResultEvent
import com.onyx.gallery.request.SaveEditPictureRequest

/**
 * Created by Leung on 2020/5/20
 */
class SaveEditPictureAction(private val filePath: String) : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        noteManager.enqueue(SaveEditPictureRequest(filePath), object : RxCallback<SaveEditPictureRequest>() {

            override fun onNext(request: SaveEditPictureRequest) {
                RxCallback.onNext(rxCallback, request)
                eventBus.post(SaveEditPictureResultEvent())
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                RxCallback.onError(rxCallback, e)
                eventBus.post(SaveEditPictureResultEvent(e))
            }
        })
    }

}