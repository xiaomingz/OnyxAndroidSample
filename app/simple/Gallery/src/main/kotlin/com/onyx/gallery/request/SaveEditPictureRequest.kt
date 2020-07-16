package com.onyx.gallery.request

import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.utils.BitmapUtils
import com.onyx.gallery.utils.ScribbleUtils

/**
 * Created by Leung on 2020/5/8
 */
class SaveEditPictureRequest(private val filePath: String) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val imageBitmap = ScribbleUtils.drawScribbleToImage(drawHandler, filePath, globalEditBundle.getNormalizedMatrix())
        BitmapUtils.saveBitmapToFile(context, filePath, imageBitmap)
        imageBitmap.recycle()
    }

}