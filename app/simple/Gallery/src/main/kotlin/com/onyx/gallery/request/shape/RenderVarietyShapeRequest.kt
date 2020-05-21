package com.onyx.gallery.request.shape

import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.helpers.NoteManager


/**
 * Created by Leung on 2020/5/20
 */
class RenderVarietyShapeRequest(private val shape: MutableList<Shape>) : BaseRequest() {

    override fun execute(noteManager: NoteManager) {
        renderShapesToBitmap = true
        renderToScreen = false
        noteManager.renderVarietyShapesToScreen(shape)
    }

}