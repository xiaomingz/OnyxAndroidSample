package com.onyx.gallery.request.shape

import android.graphics.Path
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler


/**
 * Created by Leung on 2020/5/20
 */
class RenderVarietyShapeRequest(editBundle: EditBundle, private val shape: MutableList<Shape>, private val selectionPath: Path? = null) : BaseRequest(editBundle) {

    override fun execute(drawHandler: DrawHandler) {
        selectionPath?.run { drawHandler.updateSelectionPath(this) }
        drawHandler.renderVarietyShapesToScreen(shape)
    }

}


