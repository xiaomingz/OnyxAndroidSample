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

        val surfaceRect = drawHandler.surfaceRect
        val imageSize = Size(imageBitmap.width, imageBitmap.height)

        val left = (surfaceRect.width() - imageSize.width) / 2f
        val top = (surfaceRect.height() - imageSize.height) / 2f
        val layerCount = canvas.saveLayer(left, top, left + imageSize.width.toFloat(), top + imageSize.height.toFloat(), null, Canvas.ALL_SAVE_FLAG)

        val pathPaint = getSavePathPaint(drawHandler, normalizedMatrix)
        for (mosaicPath in mosaicPathList) {
            mosaicPath.transform(normalizedMatrix)
            canvas.drawPath(mosaicPath, pathPaint)
        }
        val mosaicBitmap = getMosaicBitmap(imageBitmap)
        canvas.drawBitmap(mosaicBitmap, left, top, getMosaicPaint())
        canvas.restoreToCount(layerCount)
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

    fun getMosaicBitmap(imageBitmap: Bitmap): Bitmap {
        val imageWidth = imageBitmap.width
        val imageHeight = imageBitmap.height
        val width = Math.round(imageWidth / MOSAIC_SCALE_FACTOR)
        val height = Math.round(imageHeight / MOSAIC_SCALE_FACTOR)
        val mosaicBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        return Bitmap.createScaledBitmap(mosaicBitmap, imageWidth, imageHeight, false)
    }


}