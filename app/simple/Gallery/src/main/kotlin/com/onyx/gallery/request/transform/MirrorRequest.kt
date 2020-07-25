package com.onyx.gallery.request.transform

import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.handler.MirrorModel

/**
 * Created by Leung on 2020/6/22
 */
class MirrorRequest(private val mirrorModel: MirrorModel) : BaseRequest() {
    override fun execute(drawHandler: DrawHandler) {
        drawHandler.renderMirror(mirrorModel)
    }

}