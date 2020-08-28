package com.onyx.gallery.action.erase

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.handler.EraseModel
import com.onyx.gallery.handler.EraseWidth

/**
 * Created by Leung 2020/8/24 17:01
 **/
class UpdateEraseAction(val eraseModel: EraseModel, val eraseWidth: EraseWidth) : BaseEditAction<RxRequest>() {
    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        eraseHandler.eraseModel = eraseModel
        eraseHandler.eraseWidth = eraseWidth.width
        if (eraseModel != EraseModel.MOVE) {
            eraseHandler.eraseWidth = drawHandler.drawingArgs.eraserWidth
        }
    }
}