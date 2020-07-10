package com.onyx.gallery.action

import android.graphics.Path
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.RenderMosaicRequest

/**
 * Created by Leung on 2020/7/8
 */
class RenderMosaicAction(val currPath: Path) : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        globalEditBundle.enqueue(RenderMosaicRequest(currPath), null)
    }

}