package com.onyx.gallery.utils

import android.graphics.Matrix
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.views.shape.*
import java.util.*

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

    @Throws(CloneNotSupportedException::class)
    fun ShapeListClone(shapes: List<Shape>): MutableList<Shape> {
        val newShapes: MutableList<Shape> = ArrayList()
        for (shape in shapes) {
            newShapes.add(ShapeClone(shape))
        }
        return newShapes
    }

    @Throws(CloneNotSupportedException::class)
    fun ShapeClone(shape: Shape): Shape {
        val newShape = createShape(shape.type)
        newShape.documentUniqueId = shape.documentUniqueId
        newShape.pageUniqueId = shape.pageUniqueId
        newShape.color = shape.color
        newShape.strokeWidth = shape.strokeWidth
        newShape.pageOriginWidth = shape.pageOriginWidth
        newShape.pageOriginHeight = shape.pageOriginHeight
        newShape.layoutType = shape.layoutType
        newShape.orientation = shape.orientation
        newShape.rotationPointXCoordinate = shape.rotationPointXCoordinate
        newShape.rotationPointYCoordinate = shape.rotationPointYCoordinate
        newShape.setText(shape.text)
        newShape.createdAt = shape.createdAt
        newShape.updatedAt = shape.updatedAt
        newShape.isSaved = false
        //id
        newShape.ensureShapeUniqueId()

        //matrix
        if (shape.matrix != null) {
            val values = FloatArray(9)
            shape.matrix.getValues(values)
            newShape.setMatrix(values)
        }
        if (shape.transformMatrix != null) {
            newShape.transformMatrix = Matrix(shape.transformMatrix)
        }

        //clone object
        newShape.addPoints(shape.points.clone())
        if (shape.shapeExtraAttributes != null) {
            newShape.shapeExtraAttributes = shape.shapeExtraAttributes.clone()
        }
        if (shape.textStyle != null) {
            newShape.textStyle = shape.textStyle.clone()
        }
        if (shape.resource != null) {
            newShape.resource = shape.resource.clone()
        }
        if (shape is ImageTrackShape) {
            (newShape as ImageTrackShape).apply {
                imageTrackType = shape.imageTrackType
                backgroundBitmap = shape.backgroundBitmap
                imageSize = shape.imageSize
            }
        }
        if (shape is EditTextShapeExpand) {
            (newShape as EditTextShapeExpand).apply {
                isIndentation = shape.isIndentation
                isTraditional = shape.isTraditional
            }
        }
        return newShape
    }


}