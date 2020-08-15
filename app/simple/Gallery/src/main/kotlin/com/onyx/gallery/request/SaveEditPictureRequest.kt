package com.onyx.gallery.request

import com.onyx.android.sdk.utils.DateTimeUtil
import com.onyx.android.sdk.utils.FileUtils
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.utils.BitmapUtils
import com.onyx.gallery.utils.ScribbleUtils
import java.io.File

/**
 * Created by Leung on 2020/5/8
 */
class SaveEditPictureRequest(var imagePath: String, private val isSaveAs: Boolean) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        if (insertTextHandler.hasModify()) {
            insertTextHandler.textShape?.let { drawHandler.addShape(it) }
        }
        val imageBitmap = ScribbleUtils.drawScribbleToImage(drawHandler, imagePath, globalEditBundle.getNormalizedMatrix())
        if (isSaveAs) {
            imagePath = File(FileUtils.getParent(globalEditBundle.orgImagePath), "save_${DateTimeUtil.getCurrentTime()}.png").absolutePath
            globalEditBundle.imagePath = imagePath
        } else {
            imagePath = globalEditBundle.orgImagePath
        }
        BitmapUtils.saveBitmapToFile(context, imagePath, imageBitmap)
        imageBitmap.recycle()
        drawHandler.resetEditState()
        globalEditBundle.imagePath = imagePath
    }

}