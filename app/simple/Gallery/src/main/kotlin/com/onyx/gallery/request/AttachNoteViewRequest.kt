package com.onyx.gallery.request

import android.view.SurfaceView
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/5/16
 */
class AttachNoteViewRequest(private val hostView: SurfaceView) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        drawHandler.attachHostView(hostView)
    }
}
