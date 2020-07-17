package com.onyx.gallery.models

import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.views.ImageShapeExpand

/**
 * Created by Leung 2020/7/16 15:40
 *
 **/
data class CropSnapshot(
        val initDx: Float,
        val initDy: Float,
        val initScaleFactor: Float,
        val imagePath: String,
        val orgLimitRect: Rect,
        val limitRect: Rect,
        val cropRect: RectF,
        val imageShape: ImageShapeExpand,
        val handwritingShape: MutableList<Shape> = mutableListOf(),
        val mosaicPathList: MutableList<Path> = mutableListOf()
)