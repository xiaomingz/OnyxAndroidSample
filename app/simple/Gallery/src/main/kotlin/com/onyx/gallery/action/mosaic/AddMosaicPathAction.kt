package com.onyx.gallery.action.mosaic

import android.graphics.Path
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.mosaic.AddMosaicPathRequest

/**
 * Created by Leung on 2020/7/8
 */
class AddMosaicPathAction(val path: Path) : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        globalEditBundle.enqueue(AddMosaicPathRequest(path), null)
    }

}