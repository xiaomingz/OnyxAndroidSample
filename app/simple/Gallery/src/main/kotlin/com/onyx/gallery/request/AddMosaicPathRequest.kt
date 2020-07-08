package com.onyx.gallery.request

import android.graphics.Path
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/7/8
 */
class AddMosaicPathRequest(val path: Path) : BaseRequest() {
    override fun execute(drawHandler: DrawHandler) {
        drawHandler.addMosaicPath(path)
    }
}