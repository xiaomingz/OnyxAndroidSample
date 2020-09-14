package com.onyx.gallery.models

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.views.shape.ImageShapeExpand

/**
 * Created by Leung 2020/7/16 15:40
 *
 **/
data class CropSnapshot(
        val orgImageSize: Size,
        val renderImageSize: Size,
        val offsetX: Float = 0f,
        val offsetY: Float = 0f,
        val initScaleFactor: Float,
        val imagePath: String,
        val orgLimitRect: Rect,
        val limitRect: Rect,
        val cropRect: RectF,
        val rotateAngle: Float,
        val imageShape: ImageShapeExpand,
        val imageBitmap: Bitmap,
        val handwritingShape: MutableList<Shape> = mutableListOf()
)