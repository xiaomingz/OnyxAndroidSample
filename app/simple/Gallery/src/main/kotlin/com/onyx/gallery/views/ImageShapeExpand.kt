package com.onyx.gallery.views

import android.graphics.Bitmap
import com.onyx.android.sdk.scribble.shape.ImageShape

/**
 * Created by Leung on 2020/6/24
 */
class ImageShapeExpand : ImageShape() {

    fun getImageBitmap(): Bitmap = resourceBitmap

}