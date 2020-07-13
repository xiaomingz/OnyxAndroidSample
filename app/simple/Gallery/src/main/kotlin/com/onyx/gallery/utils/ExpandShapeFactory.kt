package com.onyx.gallery.utils

import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.views.DashLineShape
import com.onyx.gallery.views.EditTextShapeExpand
import com.onyx.gallery.views.ImageShapeExpand
import com.onyx.gallery.views.WaveLineShape

/**
 * Created by Leung on 2020/6/30
 */
object ExpandShapeFactory {

    const val IMAGE_SHAPE_EXPAND = 1
    const val EDIT_TEXT_SHAPE_EXPAND = 2
    const val SHAP_DASH_LINE = 3
    const val SHAP_WAVE_LINE = 4

    @JvmStatic
    fun createShape(shapeType: Int): Shape {
        val shape = when (shapeType) {
            IMAGE_SHAPE_EXPAND -> ImageShapeExpand()
            EDIT_TEXT_SHAPE_EXPAND -> EditTextShapeExpand()
            SHAP_DASH_LINE -> DashLineShape()
            SHAP_WAVE_LINE -> WaveLineShape()
            else -> NoteUtils.createShape(shapeType, 0)
        }
        return shape
    }

}