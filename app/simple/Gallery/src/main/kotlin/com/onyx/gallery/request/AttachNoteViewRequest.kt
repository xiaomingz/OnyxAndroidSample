package com.onyx.gallery.request

import android.util.Log
import android.view.SurfaceView
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.utils.BitmapUtils
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/5/16
 */
class AttachNoteViewRequest(editBundle: EditBundle, private val hostView: SurfaceView) : BaseRequest(editBundle) {

    override fun execute(drawHandler: DrawHandler) {
        val imageSize = editBundle.orgImageSize
        val imagePath = editBundle.imagePath
        BitmapUtils.decodeBitmapSize(imagePath, imageSize)
        editBundle.renderImageSize = Size(imageSize.width, imageSize.height)
        drawHandler.attachHostView(hostView, imageSize)
    }
}
