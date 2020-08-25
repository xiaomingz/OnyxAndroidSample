package com.onyx.gallery.action.erase

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.handler.EraseModel
import com.onyx.gallery.models.EraseArgs
import com.onyx.gallery.request.shape.ClearLayerRequest
import com.onyx.gallery.request.shape.EraseStrokesRequest

/**
 * Created by Leung 2020/8/24 16:51
 **/
class EraseAction(val eraseArgs: EraseArgs) : BaseEditAction<RxRequest>() {
    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        val touchPointList = eraseArgs.touchPointList
        val request = when (eraseArgs.eraseModel) {
            EraseModel.LAYER -> ClearLayerRequest()
            else -> EraseStrokesRequest(touchPointList, eraseArgs.eraseWidth)
        }
        globalEditBundle.enqueue(request, null)
    }

}