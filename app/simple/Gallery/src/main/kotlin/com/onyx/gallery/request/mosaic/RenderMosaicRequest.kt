package com.onyx.gallery.request.mosaic

import android.graphics.Path
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/7/8
 */
class RenderMosaicRequest(val currPath: Path) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        drawHandler.setCurrMosaicPath(currPath)
        renderToScreen = true
    }
}