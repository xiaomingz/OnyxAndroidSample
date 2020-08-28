package com.onyx.gallery.request.shape

import com.onyx.android.sdk.utils.CollectionUtils
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/8/24 16:33
 **/
class ClearLayerRequest : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val handwritingShape = drawHandler.getHandwritingShape()
        if (CollectionUtils.isNullOrEmpty(handwritingShape)) {
            return
        }
        undoRedoHandler.eraseShapes(handwritingShape)
        renderShapesToBitmap = true
        renderToScreen = true
    }

}