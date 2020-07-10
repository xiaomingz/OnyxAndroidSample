package com.onyx.gallery.helpers

import android.graphics.Color
import com.onyx.android.sdk.pen.TouchHelper
import com.onyx.android.sdk.scribble.shape.ShapeFactory

/**
 * Created by Leung on 2020/5/25
 */
class DrawArgs {
    companion object {
        const val stepStrokeWidth = 3
        const val maxStrokeWidth = 40
        const val minStrokeWidth = 10
        const val defaultStrokeWidth = 15
        const val defaultStrokeColor = Color.BLACK
        const val defaultShape = ShapeFactory.SHAPE_PENCIL_SCRIBBLE
        const val defaultStrokeType = TouchHelper.STROKE_STYLE_PENCIL
    }

    var normalizeScale = 1f
    var strokeColor = defaultStrokeColor
    var strokeWidth = defaultStrokeWidth
    var strokeStyle = defaultStrokeType
    var lastShapeType = defaultShape
    var currShapeType = defaultShape
        set(value) {
            if (field == value) {
                return
            }
            lastShapeType = currShapeType
            field = value
        }

    fun reset() {
        strokeColor = defaultStrokeColor
        strokeWidth = defaultStrokeWidth
        strokeStyle = defaultStrokeType
        lastShapeType = defaultShape
        currShapeType = defaultShape
    }

    fun getRendererScale(): Float {
        return 1f / normalizeScale
    }

}

