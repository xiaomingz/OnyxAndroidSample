package com.onyx.gallery.helpers

import android.graphics.Color
import com.onyx.android.sdk.pen.TouchHelper
import com.onyx.android.sdk.scribble.shape.ShapeFactory

/**
 * Created by Leung on 2020/5/25
 */
class DrawArgs {
    companion object {
        const val defaultEraserWidth = 20.0f
        const val stepStrokeWidth = 1
        const val maxStrokeWidth = 20
        const val minStrokeWidth = 1
        const val defaultStrokeWidth = 2
        const val defaultSelectionPathIntervals = 8f
        const val defaultStrokeColor = Color.BLACK
        const val defaultShape = ShapeFactory.SHAPE_PENCIL_SCRIBBLE
        const val defaultStrokeType = TouchHelper.STROKE_STYLE_PENCIL
    }

    var normalizeScale = 1f
    var strokeColor = defaultStrokeColor
    var strokeWidth = defaultStrokeWidth
    var eraserWidth = defaultEraserWidth
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
        eraserWidth = defaultEraserWidth
        strokeStyle = defaultStrokeType
        lastShapeType = defaultShape
        currShapeType = defaultShape
    }

    fun getRendererScale(): Float {
        return 1f / normalizeScale
    }

}

