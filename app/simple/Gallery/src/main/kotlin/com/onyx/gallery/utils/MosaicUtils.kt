package com.onyx.gallery.utils

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.annotation.WorkerThread

/**
 * Created by Leung on 2020/7/8
 */
object MosaicUtils {
    const val MOSAIC_SCALE_FACTOR = 16f

    fun getMosaicPaint(): Paint {
        val mosaicPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mosaicPaint.setFilterBitmap(false)
        mosaicPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        return mosaicPaint
    }

    @WorkerThread
    fun getMosaicBitmap(imageBitmap: Bitmap, scaleFactor: Float = MOSAIC_SCALE_FACTOR): Bitmap {
        val imageWidth = imageBitmap.width
        val imageHeight = imageBitmap.height
        val width = Math.round(imageWidth / scaleFactor)
        val height = Math.round(imageHeight / scaleFactor)
        val scaleBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        val mosaicBitmap = Bitmap.createScaledBitmap(scaleBitmap, imageWidth, imageHeight, false)
        scaleBitmap.recycle()
        return mosaicBitmap
    }

}