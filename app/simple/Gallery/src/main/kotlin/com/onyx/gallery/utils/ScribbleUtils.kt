package com.onyx.gallery.utils

import android.graphics.*
import androidx.annotation.WorkerThread
import androidx.core.graphics.values
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.scribble.data.RenderColorConfig
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.views.shape.ImageTrackShape
import com.onyx.gallery.views.shape.MosaicShape

/**
 * Created by Leung on 2020/7/9
 */
object ScribbleUtils {

    @WorkerThread
    fun drawScribbleToImage(drawHandler: DrawHandler, imagePath: String, normalizedMatrix: Matrix): Bitmap {
        val newBitmap = createBitmap(imagePath)
        val imageBitmap = decodeFile(imagePath)
        val shapeBitmap = createShapeBitmap(drawHandler, normalizedMatrix, imageBitmap)
        drawBitmap(newBitmap, imageBitmap, shapeBitmap)
        return newBitmap
    }

    private fun createBitmap(path: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        val config = Bitmap.Config.ARGB_8888
        return Bitmap.createBitmap(options.outWidth, options.outHeight, config)
    }

    private fun decodeFile(path: String): Bitmap {
        return BitmapFactory.decodeFile(path, BitmapFactory.Options())
    }

    private fun createShapeBitmap(drawHandler: DrawHandler, normalizedMatrix: Matrix, imageBitmap: Bitmap): Bitmap {
        val imageSize = Size(imageBitmap.width, imageBitmap.height)
        val renderContext = createRenderContext(imageSize.width, imageSize.height)
        val handwritingShape = ExpandShapeFactory.ShapeListClone(drawHandler.getHandwritingShape())
        val scaleFactor = normalizedMatrix.values()[Matrix.MSCALE_X]
        val mosaicScaleFactor = scaleFactor * MosaicUtils.MOSAIC_SCALE_FACTOR
        val mosaicBitmap = MosaicUtils.getMosaicBitmap(imageBitmap, mosaicScaleFactor)
        handwritingShape.forEach { shape ->
            if (shape is MosaicShape) {
                shape.backgroundBitmap = mosaicBitmap
                shape.imageSize = imageSize
            } else if (shape is ImageTrackShape) {
                shape.backgroundBitmap = imageBitmap
                shape.imageSize = imageSize
            }
        }
        ShapeUtils.renderShapes(handwritingShape, renderContext, false)
        return renderContext.getBitmap()
    }

    private fun createRenderContext(width: Int, height: Int): RenderContext {
        val pngRect = Rect(0, 0, width, height)
        val renderContext = RenderContext.create(Paint(), Matrix())
        renderContext.createBitmap(pngRect)
        renderContext.updateCanvas()
        renderContext.renderColorConfig = RenderColorConfig.RAW_RENDER_COLOR
        return renderContext
    }

    private fun drawBitmap(newBitmap: Bitmap, imageBitmap: Bitmap, shapeBitmap: Bitmap) {
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(imageBitmap, 0f, 0f, null)
        canvas.drawBitmap(shapeBitmap, 0f, 0f, null)
    }

}