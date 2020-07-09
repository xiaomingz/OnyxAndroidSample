package com.onyx.gallery.utils

import android.graphics.*
import androidx.core.graphics.values
import com.onyx.android.sdk.data.Size
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/7/8
 */
object SaveMosaicUtils {
    const val MOSAIC_SCALE_FACTOR = 16f

    fun renderMosaicToCanvas(drawHandler: DrawHandler, canvas: Canvas, normalizedMatrix: Matrix, imageBitmap: Bitmap) {
        val mosaicPathList = drawHandler.getMosaicPathList()
        if (mosaicPathList.isEmpty()) return
        val imageSize = Size(imageBitmap.width, imageBitmap.height)
        val layerCount = canvas.saveLayer(0f, 0f, imageSize.width.toFloat(), imageSize.height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        val mosaicPath = Path()
        for (path in mosaicPathList) {
            mosaicPath.addPath(path)
        }
        mosaicPath.transform(normalizedMatrix)
        val pathPaint = getSavePathPaint(drawHandler, normalizedMatrix)
        canvas.drawPath(mosaicPath, pathPaint)
        val scaleFactor = normalizedMatrix.values()[Matrix.MSCALE_X]
        val mosaicScaleFactor = scaleFactor * MOSAIC_SCALE_FACTOR
        val mosaicBitmap = getMosaicBitmap(imageBitmap, mosaicScaleFactor)
        canvas.drawBitmap(mosaicBitmap, 0f, 0f, getMosaicPaint())
        canvas.restoreToCount(layerCount)
        mosaicBitmap.recycle()
    }

    fun getMosaicPaint(): Paint {
        val mosaicPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mosaicPaint.setFilterBitmap(false)
        mosaicPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        return mosaicPaint
    }

    fun getPathPaint(drawHandler: DrawHandler): Paint {
        val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pathPaint.setDither(true)
        pathPaint.setAntiAlias(true)
        pathPaint.setStyle(Paint.Style.STROKE)
        pathPaint.setTextAlign(Paint.Align.CENTER)
        pathPaint.setStrokeCap(Paint.Cap.ROUND)
        pathPaint.setStrokeJoin(Paint.Join.ROUND)
        pathPaint.setStrokeWidth(drawHandler.getStrokeWidth())
        return pathPaint
    }

    private fun getSavePathPaint(drawHandler: DrawHandler, normalizedMatrix: Matrix): Paint {
        val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pathPaint.setDither(true)
        pathPaint.setAntiAlias(true)
        pathPaint.setStyle(Paint.Style.STROKE)
        pathPaint.setTextAlign(Paint.Align.CENTER)
        pathPaint.setStrokeCap(Paint.Cap.ROUND)
        pathPaint.setStrokeJoin(Paint.Join.ROUND)
        var strokeWidth = drawHandler.getStrokeWidth()
        val scaleFactor = normalizedMatrix.values()[Matrix.MSCALE_X]
        strokeWidth *= scaleFactor
        pathPaint.setStrokeWidth(strokeWidth)
        return pathPaint
    }

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