package com.onyx.gallery.utils

import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.views.shape.*

/**
 * Created by Leung on 2020/6/30
 */
object ExpandShapeFactory {

    const val IMAGE_SHAPE_EXPAND = 22
    const val EDIT_TEXT_SHAPE_EXPAND = 23
    const val SHAP_DASH_LINE = 24
    const val SHAP_WAVE_LINE = 25
    const val SHAP_ARROW_LINE = 26
    const val SHAP_MOSAIC = 27
    const val CROP = 28

    @JvmStatic
    fun createShape(shapeType: Int): Shape {
        val shape = when (shapeType) {
            IMAGE_SHAPE_EXPAND -> ImageShapeExpand()
            EDIT_TEXT_SHAPE_EXPAND -> EditTextShapeExpand()
            SHAP_DASH_LINE -> DashLineShape()
            SHAP_WAVE_LINE -> WaveLineShape()
            SHAP_ARROW_LINE -> ArrowLineShape()
            SHAP_MOSAIC -> MosaicShape()
            else -> NoteUtils.createShape(shapeType, 0)
        }
        return shape
    }

}