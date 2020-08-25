package com.onyx.gallery.utils

import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.views.shape.*

/**
 * Created by Leung on 2020/6/30
 */
object ExpandShapeFactory {

    const val IMAGE_SHAPE_EXPAND = 22
    const val EDIT_TEXT_SHAPE_EXPAND = 23
    const val SHAPE_DASH_LINE = 24
    const val SHAPE_WAVE_LINE = 25
    const val SHAPE_ARROW_LINE = 26
    const val SHAPE_MOSAIC = 27
    const val CROP = 28
    const val ERASE = 29
    const val SHAPE_IMAGE_TRACK = 30

    @JvmStatic
    fun createShape(shapeType: Int): Shape {
        val shape = when (shapeType) {
            IMAGE_SHAPE_EXPAND -> ImageShapeExpand()
            EDIT_TEXT_SHAPE_EXPAND -> EditTextShapeExpand()
            SHAPE_DASH_LINE -> DashLineShape()
            SHAPE_WAVE_LINE -> WaveLineShape()
            SHAPE_ARROW_LINE -> ArrowLineShape()
            SHAPE_MOSAIC -> MosaicShape()
            SHAPE_IMAGE_TRACK -> ImageTrackShape()
            else -> NoteUtils.createShape(shapeType, 0)
        }
        return shape
    }

}