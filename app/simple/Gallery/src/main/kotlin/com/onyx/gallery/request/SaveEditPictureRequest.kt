package com.onyx.gallery.request

import android.graphics.*
import androidx.core.graphics.values
import com.onyx.android.sdk.scribble.data.RenderColorConfig
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.utils.BitmapUtils

/**
 * Created by Leung on 2020/5/8
 */
class SaveEditPictureRequest(private val filePath: String) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val imageBitmap = decodeFile(filePath)
        val newBitmap = createBitmap(filePath)
        val shapeBitmap = createShapeBitmap(imageBitmap.width, imageBitmap.height)
        drawBitmap(newBitmap, imageBitmap, shapeBitmap)
        BitmapUtils.saveBitmapToFile(context, filePath, newBitmap)
        shapeBitmap.recycle()
        newBitmap.recycle()
        imageBitmap.recycle()
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

    private fun createShapeBitmap(outWidth: Int, outHeight: Int): Bitmap {
        val renderContext = createRenderContext(outWidth, outHeight)
        val handwritingShape = drawHandler.getHandwritingShape()
        val normalizedMatrix = globalEditBundle.getNormalizedMatrix()
        handwritingShape.forEach { it.postConcat(normalizedMatrix) }
        updateShapeStrokeWidth(handwritingShape)
        ShapeUtils.renderShapes(handwritingShape, renderContext, true)
        return renderContext.getBitmap()
    }

    private fun updateShapeStrokeWidth(shapes: List<Shape>) {
        val scaleFactor = globalEditBundle.getNormalizedMatrix().values()[Matrix.MSCALE_X]
        for (shape in shapes) {
            shape.strokeWidth *= scaleFactor
        }
    }

    private fun createRenderContext(width: Int, height: Int): RenderContext {
        val pngRect = Rect(0, 0, width, height)
        val renderContext = RenderContext.create(Paint(), Matrix())
        renderContext.createBitmap(pngRect)
        renderContext.updateCanvas()
        renderContext.renderColorConfig = RenderColorConfig.EPD_RENDER_COLOR
        return renderContext
    }

    private fun drawBitmap(newBitmap: Bitmap, imageBitmap: Bitmap, shapeBitmap: Bitmap) {
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(imageBitmap, 0f, 0f, null)
        canvas.drawBitmap(shapeBitmap, 0f, 0f, null)
    }

}