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
class SaveEditPictureRequest(private var filePath: String, private val isSaveAs: Boolean) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val imageBitmap = ScribbleUtils.drawScribbleToImage(drawHandler, filePath, globalEditBundle.getNormalizedMatrix())
        if (isSaveAs) {
            filePath = File(FileUtils.getParent(globalEditBundle.filePath), "save_${DateTimeUtil.getCurrentTime()}.png").absolutePath
            globalEditBundle.filePath = filePath
        }
        BitmapUtils.saveBitmapToFile(context, filePath, imageBitmap)
        drawHandler.updateSaveCropSnapshotIndex()
        imageBitmap.recycle()
    }

}