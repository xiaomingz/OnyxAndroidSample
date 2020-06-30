package com.onyx.gallery.utils

import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.views.EditTextShapeExpand
import com.onyx.gallery.views.ImageShapeExpand

/**
 * Created by Leung on 2020/6/30
 */
object ExpandShapeFactory {

    const val IMAGE_SHAPE_EXPAND = 1

    const val EDITTEXT_SHAPE_EXPAND = 2

    @JvmStatic
    fun createShape(shapeType: Int): Shape {
        val shape = when (shapeType) {
            IMAGE_SHAPE_EXPAND -> ImageShapeExpand()
            EDITTEXT_SHAPE_EXPAND -> EditTextShapeExpand()
            else -> NoteUtils.createShape(shapeType, 0)
        }
        return shape
    }

}