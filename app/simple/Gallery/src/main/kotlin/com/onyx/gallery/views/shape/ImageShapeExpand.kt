package com.onyx.gallery.views.shape

import android.graphics.Bitmap
import com.onyx.android.sdk.scribble.shape.ImageShape

/**
 * Created by Leung on 2020/6/24
 */
class ImageShapeExpand : ImageShape() {

    fun getImageBitmap(): Bitmap = resourceBitmap

    fun setResourceBitmap(imageBitmap: Bitmap) {
        resourceBitmap = imageBitmap
    }

}