package com.onyx.gallery.request

import com.onyx.android.sdk.utils.FileUtils
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/31 10:38
 **/
class DeleteFileRequest(private val path: String) : BaseRequest() {
    override fun execute(drawHandler: DrawHandler) {
        FileUtils.deleteFile(path)
    }
}