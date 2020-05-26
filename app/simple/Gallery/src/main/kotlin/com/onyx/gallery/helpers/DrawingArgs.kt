package com.onyx.gallery.helpers

import android.graphics.Color
import com.onyx.android.sdk.pen.TouchHelper
import com.onyx.android.sdk.scribble.shape.ShapeFactory

/**
 * Created by Leung on 2020/5/25
 */
class DrawingArgs {
    private val DEFAULT_STROKE_WIDTH = 10f
    private val DEFAULT_STROKE_COLOR = Color.BLACK
    var strokeColor = defaultStrokeColor()
    var strokeWidth = defaultStrokeWidth()
    var strokeStyle = defaultStrokeType()
    var lastShapeType = defaultShape()
    var currShapeType = defaultShape()
        set(value) {
            if (field == value) {
                return
            }
            lastShapeType = currShapeType
            field = value
        }

    fun defaultShape(): Int = ShapeFactory.SHAPE_PENCIL_SCRIBBLE
    fun defaultStrokeColor(): Int = DEFAULT_STROKE_COLOR
    fun defaultStrokeWidth(): Float = DEFAULT_STROKE_WIDTH
    fun defaultStrokeType(): Int = TouchHelper.STROKE_STYLE_PENCIL

    fun initArgs(noteManager: NoteManager) {
        noteManager.strokeColor = defaultStrokeColor()
        noteManager.strokeWidth = defaultStrokeWidth()
    }

    fun reset() {
        strokeColor = defaultStrokeColor()
        strokeWidth = defaultStrokeWidth()
        strokeStyle = defaultStrokeType()
        lastShapeType = defaultShape()
        currShapeType = defaultShape()
    }


}

