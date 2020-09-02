package com.onyx.gallery.request.zoom

import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/24 16:12
 **/
class ZoomBeginRequest(editBundle: EditBundle) : BaseRequest(editBundle) {

    init {
        setPauseRawDraw(true)
    }

    override fun execute(drawHandler: DrawHandler) {
        renderToScreen = true
    }

}