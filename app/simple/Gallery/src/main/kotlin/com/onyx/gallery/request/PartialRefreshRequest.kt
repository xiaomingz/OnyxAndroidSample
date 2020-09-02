package com.onyx.gallery.request

import android.graphics.RectF
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/23 17:12
 **/
class PartialRefreshRequest(editBundle: EditBundle, private val refreshRect: RectF) : BaseRequest(editBundle) {

    override fun execute(drawHandler: DrawHandler) {
        drawHandler.renderContext.setClipRect(refreshRect)
        drawHandler.partialRefreshScreen()
        drawHandler.renderContext.resetClipRect()
    }

}